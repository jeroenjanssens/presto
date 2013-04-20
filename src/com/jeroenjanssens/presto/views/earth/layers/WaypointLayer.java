/**
 * Copyright 2009 Tilburg University. All rights reserved.
 * 
 * This file is part of Presto.
 *
 * Presto is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Presto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Presto.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroenjanssens.presto.views.earth.layers;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.opengl.GL;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.earth.tools.EarthViewToolBar;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class WaypointLayer extends RenderableLayer implements LockedLayer {

	public static final int RENDER_WAYPOINT_PICK = -1;
	public static final int RENDER_WAYPOINT_HIDE = 0;
	public static final int RENDER_WAYPOINT_NORMAL = 1;
	public static final int RENDER_WAYPOINT_SELECTED = 2;

	EarthView earthView;
	private final PickSupport pickSupport = new PickSupport();

	public WaypointLayer(EarthView earthView) {
		this.earthView = earthView;
		this.setName("Waypoints");
	}

	@Override
	public void doRender(DrawContext dc) {
		if(earthView.getEarthViewToolBar().getCurrentTool().mayLayerRender(this, false)) {
			renderWaypoints(dc, null);
		}
	}

	@Override
	public void doPick(DrawContext dc, Point point) {
		if(earthView.getEarthViewToolBar().getCurrentTool().mayLayerRender(this, true)) {
			renderWaypoints(dc, point);
		}
	}

	private void renderWaypoints(DrawContext dc, Point point) {
		if(earthView.getCurrentScenarioEditor() == null) return;
		if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
		
		ArrayList<Track> allTracks = earthView.getCurrentScenarioEditor().getScenario().getRootFolder().getAllTracks(true, dc.isPickingMode());

		GL gl = dc.getGL();

		boolean attribsPushed = false;
		boolean modelviewPushed = false;
		boolean projectionPushed = false;

		try {
			gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT
					| GL.GL_COLOR_BUFFER_BIT
					| GL.GL_ENABLE_BIT
					| GL.GL_TRANSFORM_BIT
					| GL.GL_VIEWPORT_BIT
					| GL.GL_CURRENT_BIT);
			attribsPushed = true;

			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL.GL_BLEND);

			gl.glHint(GL.GL_FASTEST, GL.GL_POLYGON_SMOOTH_HINT);
			gl.glEnable(GL.GL_POLYGON_SMOOTH_HINT);

			java.awt.Rectangle viewport = dc.getView().getViewport();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			projectionPushed = true;
			gl.glLoadIdentity();
			int width = 100;
			int height = 100;
			double maxwh = width > height ? width : height;

			gl.glOrtho(0d, viewport.width, 0d, viewport.height, -1 * maxwh, 1 * maxwh);
			gl.glLineWidth(1);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			modelviewPushed = true;
			gl.glLoadIdentity();

			float[] currentColor = new float[4];
			if(dc.isPickingMode()) {
				dc.getGL().glGetFloatv(GL.GL_CURRENT_COLOR, currentColor, 0);
				pickSupport.clearPickList();
				pickSupport.beginPicking(dc);
			}
			boolean hoverTool = false;
			int toolstate = earthView.getEarthViewToolBar().getCurrentTool().getToolState();
			if((toolstate == EarthViewToolBar.TOOL_INSERTWAYPOINT) || (toolstate == EarthViewToolBar.TOOL_REMOVEWAYPOINT) || (toolstate == EarthViewToolBar.TOOL_WAYPOINTSELECTION)) {
				hoverTool = true;
			}	



			for(Track track : allTracks) {

				boolean isTrackSelected = (earthView.getCurrentSelectedTracks().contains(track)); 



				boolean hasTrackSelectedWaypoint = false;
				for(Waypoint w : earthView.getCurrentSelectedWaypoints()) {
					if(track.getWaypoints().contains(w)) {
						hasTrackSelectedWaypoint = true;
					}
				}

				//draw the waypoints on top of the track if needed
				CopyOnWriteArrayList<Waypoint> allWaypoints = track.getWaypoints();
				for(Waypoint waypoint : allWaypoints) {
					int primitive = GL.GL_POLYGON;
					int size = 3;
					Position position = waypoint.getPosition();

					//picking per waypoint
					if(dc.isPickingMode()) {
						Color color = dc.getUniquePickColor();
						//dc.getGL().glColor3i(color.getRed(), color.getGreen(), color.getBlue());
						gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
						pickSupport.addPickableObject(color.getRGB(), waypoint);
						size = 5;
					} else {

						if(hoverTool) {
							Object o = earthView.getWWD().getObjectsAtCurrentPosition().getTopObject();
							if (o != null) {
								if(o.equals(track)) {
									isTrackSelected = true;
								} else {
									if(o instanceof Waypoint) {
										if(((Waypoint)o).getTrack().equals(track)) {
											isTrackSelected = true;
										}
									}
								}
							}
						}

						if(waypoint.equals(dc.getPickedObjects().getTopObject())) {
							gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 255);
						} else {
							gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 230);
						}

						if(earthView.getCurrentSelectedWaypoints().contains(waypoint)) {
							//this waypoint is selected
							primitive = GL.GL_POLYGON;
						} else if(hasTrackSelectedWaypoint || isTrackSelected) {
							//draw normal
							primitive = GL.GL_LINE_LOOP;
						} else {
							primitive = 0;
						}			
					}

					if(primitive != 0) {
						Vec4 surfacePoint = this.computePoint(dc, position, true);
						//BezierVertex surfacePoint = waypoint.getVertices().get(0);
						double horizon = dc.getView().computeHorizonDistance();
						double eyeDistance = dc.getView().getEyePoint().distanceTo3(new Vec4(surfacePoint.x, surfacePoint.y, surfacePoint.z));
						if (eyeDistance < horizon) {
							Vec4 screenPoint = getPoint(dc, position);
							waypoint.setScreenPoint(new Point((int)screenPoint.x, (int)screenPoint.y));

							gl.glBegin(primitive);
							gl.glVertex2d(screenPoint.x-size, screenPoint.y-size);
							gl.glVertex2d(screenPoint.x+size, screenPoint.y-size);
							gl.glVertex2d(screenPoint.x+size, screenPoint.y+size);
							gl.glVertex2d(screenPoint.x-size, screenPoint.y+size);
							gl.glEnd();
						} //end if visible
					}
				} //end for each waypoint
			} //end for each track

			if(dc.isPickingMode()) {
				pickSupport.endPicking(dc);
				pickSupport.resolvePick(dc, point, this);
				dc.getGL().glColor4fv(currentColor, 0);
			}

		} finally {

			if (projectionPushed) {
				gl.glMatrixMode(GL.GL_PROJECTION);
				gl.glPopMatrix();
			}
			if (modelviewPushed) {
				gl.glMatrixMode(GL.GL_MODELVIEW);
				gl.glPopMatrix();
			}
			if (attribsPushed) {
				gl.glPopAttrib();
			}
		}
	}

	/* From LatLon to Vec4 in world */
	private Vec4 computePoint(DrawContext dc, Position pos, boolean applyOffset)
	{
		double height = pos.getElevation();
		return dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(), height);
	}

	/* From LatLon to Vec4 on Screen */
	public Vec4 getPoint(DrawContext dc, Position pos) {
		Vec4 loc = null;
		//if (pos.getElevation() < dc.getGlobe().getMaxElevation())loc = dc.getSurfaceGeometry().getSurfacePoint(pos);
		//if (loc == null)
		loc = dc.getGlobe().computePointFromPosition(pos);
		Vec4 screenPoint = dc.getView().project(loc);
		return screenPoint;
	}
}
