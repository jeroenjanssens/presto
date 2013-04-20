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

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;

import javax.media.opengl.GL;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.background.Background;
import com.jeroenjanssens.presto.model.background.BackgroundVessel;
import com.jeroenjanssens.presto.model.background.BackgroundVesselSample;
import com.jeroenjanssens.presto.model.background.OneSecondOfSamples;
import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.earth.renderables.BackgroundVesselRenderable;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class BackgroundLayer extends RenderableLayer {

	private EarthView earthView;
	public EarthView getEarthView() {
		return earthView;
	}


	private long time;
	private Background background = null;

	private ArrayList<BackgroundVesselRenderable> currentBackgroundVesselRenderables;
	private ArrayList<BackgroundVessel> currentBackgroundVessels;
	private Long currentTime;
	private boolean backgroundLoaded = false;

	double vesselSize = 0;
	double oldVesselSize = 0;
	private void updateBackground(Long time) {
		oldVesselSize = vesselSize;
		vesselSize = earthView.getVesselSize();
		if((!time.equals(currentTime)) || (vesselSize != oldVesselSize)) {
			currentBackgroundVesselRenderables = new ArrayList<BackgroundVesselRenderable>();
			currentBackgroundVessels = new ArrayList<BackgroundVessel>();
			background.getSamples().get(time);

			//System.out.println("update background: " + vesselSize);

			
			
			for(int t = 0; t < 20; t++) {
				OneSecondOfSamples oneSecond = background.getSamples().get(time - t);

				if(oneSecond != null) {
					for(BackgroundVesselSample bgvs : oneSecond.getVesselSamples()) {
						if(!currentBackgroundVessels.contains(bgvs.getVessel())) {

							Position positionO = Position.fromDegrees(bgvs.getLatitude(), bgvs.getLongitude(), 100);
							Position positionF = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(bgvs.getHeading()), Angle.fromDegrees(3*vesselSize)), (1000*vesselSize)+10);
							Position positionL = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(bgvs.getHeading()).add(Angle.NEG90), Angle.fromDegrees(vesselSize)), (1000*vesselSize)+10);
							Position positionR = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(bgvs.getHeading()).add(Angle.POS90), Angle.fromDegrees(vesselSize)), (1000*vesselSize)+10);

							Vec4 vecF = earthView.getWWD().getModel().getGlobe().computePointFromPosition(positionF.getLatitude(), positionF.getLongitude(), (1000*vesselSize)+10);
							Vec4 vecL = earthView.getWWD().getModel().getGlobe().computePointFromPosition(positionL.getLatitude(), positionL.getLongitude(), (1000*vesselSize)+10);
							Vec4 vecR = earthView.getWWD().getModel().getGlobe().computePointFromPosition(positionR.getLatitude(), positionR.getLongitude(), (1000*vesselSize)+10);

							currentBackgroundVesselRenderables.add(new BackgroundVesselRenderable(vecF.x, vecF.y, vecF.z, vecL.x, vecL.y, vecL.z, vecR.x, vecR.y, vecR.z));

							currentBackgroundVessels.add(bgvs.getVessel());
						}
					}	
				}
			}
			currentTime = time;
		}
	}

	public BackgroundLayer(EarthView earthView) {
		this.earthView = earthView;
		this.setName("Background Vessels");
	}

	@Override
	public void doPick(DrawContext dc, java.awt.Point pickPoint) {

	}

	@Override
	public void doRender(DrawContext dc) {

		if(!backgroundLoaded || (background == null)) return;

		if(earthView.getCurrentScenarioEditor() == null) return;
		if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
		
		time = earthView.getCurrentScenarioEditor().getTimeLine().getCursorTime();

		//System.out.println("Time: " + time);

		updateBackground(new Long((time+7200000) / 1000));

		GL gl = dc.getGL();

		//render the vessels
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

			int primitive = GL.GL_POLYGON;

			gl.glColor4ub((byte)200, (byte)255, (byte)200, (byte) 255);

			for(BackgroundVesselRenderable bgvr : currentBackgroundVesselRenderables) {
				//System.out.println("DrawVessel: " + bgvs.getVessel().getIMONumber());

				gl.glBegin(primitive);
				gl.glVertex3d(bgvr.vFx, bgvr.vFy, bgvr.vFz);
				gl.glVertex3d(bgvr.vLx, bgvr.vLy, bgvr.vLz);
				gl.glVertex3d(bgvr.vRx, bgvr.vRy, bgvr.vRz);
				gl.glEnd();
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

	public void setBackgroundLoaded(boolean b) {
		backgroundLoaded  = b;
		earthView.getWWD().redraw();
		background = Activator.getDefault().getBackgroundManager().getBackground();
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
