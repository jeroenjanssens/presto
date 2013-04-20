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

import com.jeroenjanssens.presto.model.BezierVertex;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackLayer extends RenderableLayer implements LockedLayer {

	public static final int RENDER_TRACK_PICK = -1;
	public static final int RENDER_TRACK_HIDE = 0;
	public static final int RENDER_TRACK_NORMAL = 1;
	public static final int RENDER_TRACK_SELECTED = 2;

	private EarthView earthView;
	private final PickSupport pickSupport = new PickSupport();
	
	public TrackLayer(EarthView earthView) {
		this.earthView = earthView;
		this.setName("Tracks");
	}
	
	@Override
	public void doRender(DrawContext dc) {
        Position groundPos = dc.getViewportCenterPosition();
        if(groundPos != null)
        {
            Vec4 groundTarget = dc.getGlobe().computePointFromPosition(groundPos);
            Double distance = dc.getView().getEyePoint().distanceTo3(groundTarget);
            double zoom = distance / 100000.0;
    		zoom = Math.max(zoom, 0.02);
    		zoom = Math.min(zoom, 1);
    		this.earthView.setVesselSize(0.01 * zoom);
        }
		
		if(earthView.getEarthViewToolBar().getCurrentTool().mayLayerRender(this, false)) {
			renderTracks(dc, null);
		}
	}
	
	@Override
	public void doPick(DrawContext dc, Point point) {
		if(earthView.getEarthViewToolBar().getCurrentTool().mayLayerRender(this, true)) {
			renderTracks(dc, point);
		}
	}
	
	//TODO: Anti-alias the tracks
	private void renderTracks(DrawContext dc, Point point) {
		if(earthView.getCurrentScenarioEditor() == null) return;
		if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
		ArrayList<Track> allTracks = earthView.getCurrentScenarioEditor().getScenario().getRootFolder().getAllTracks(true, dc.isPickingMode());

		GL gl = dc.getGL();
		gl.glColor4d(1.0, 1.0, 1.0, 1.0);
		gl.glPushAttrib(GL.GL_HINT_BIT 
				| GL.GL_CURRENT_BIT 
				| GL.GL_LINE_BIT
		| GL.GL_COLOR_BUFFER_BIT
		);
		
		float[] currentColor = new float[4];
		if(dc.isPickingMode()) {
			dc.getGL().glGetFloatv(GL.GL_CURRENT_COLOR, currentColor, 0);
			pickSupport.clearPickList();
			pickSupport.beginPicking(dc);
		} else {
			
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		
		for(Track track : allTracks) {
			
			if(dc.isPickingMode()) {
				Color color = dc.getUniquePickColor();
				gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
				pickSupport.addPickableObject(color.getRGB(), track);
				gl.glLineWidth(8.0f);
			} else {
				gl.glLineWidth(1.0f);
				
				if(earthView.getCurrentSelectedTracks().contains(track)) {
					gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 255);
					gl.glLineWidth(2.0f);
				} else {
					gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 150);
				}
			}
			
			gl.glBegin(GL.GL_LINE_STRIP);
			for(Waypoint waypoint : track.getWaypoints()) {
				
				CopyOnWriteArrayList<BezierVertex> allVertices = waypoint.getVertices();
				for(BezierVertex vertex : allVertices) {
					gl.glVertex3d(vertex.x, vertex.y, vertex.z);
				} //end vertices
			} //end waypoints
			gl.glEnd();
		} //end tracks
		
		
		if(dc.isPickingMode()) {
			pickSupport.endPicking(dc);
			pickSupport.resolvePick(dc, point, this);	
			dc.getGL().glColor4fv(currentColor, 0);
		}
		
		gl.glPopAttrib();
	}

}
