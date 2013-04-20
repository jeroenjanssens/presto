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

package com.jeroenjanssens.presto.views.earth.renderables;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import javax.media.opengl.GL;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.sailing.PositionHeading;
import com.jeroenjanssens.presto.sailing.Sailor;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Vessel implements Renderable {

	private PositionHeading positionHeading;
	private Track track;
	private Sailor sailor;

	private long time = 0L;
	private double vesselSize;

	public Vessel(Track track) {
		this.track = track;
		this.sailor = new Sailor(track);
		this.positionHeading = null;
	}

	public PositionHeading getPositionHeading() {
		return positionHeading;
	}

	public void setPositionHeading(PositionHeading positionv) {
		//this.positionHeading = positionHeading;
	}

	public void setTime(long time) {
		if(this.time != time) {
			//Position p = sailor.getVesselPosition(time);
			//position = sailor.getVesselPositionFromBeginning(time);
			//System.out.println("FB: " + position + " OP: " + p);
			//long start = System.currentTimeMillis();
			positionHeading = sailor.getVesselPositionHeading(time);
			//long duration = System.currentTimeMillis() - start;
			//System.out.println("duration: " + duration);
			this.time = time;
		}
	}

	public void render(DrawContext dc) {
		if(dc.isPickingMode()) return;
		if(positionHeading == null) return;
		if(positionHeading.getPosition() == null) return;

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





			if(positionHeading.isDirty()) { 
				gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 150);
			} else {
				gl.glColor4ub((byte)track.getColor().getRed(), (byte)track.getColor().getGreen(), (byte)track.getColor().getBlue(), (byte) 255);
			}
				
			Position positionO = positionHeading.getPosition();
			Position positionF = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(positionHeading.getHeading()), Angle.fromDegrees(3*vesselSize)), 100);
			Position positionL = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(positionHeading.getHeading()).add(Angle.NEG90), Angle.fromDegrees(vesselSize)), 100);
			Position positionR = new Position(LatLon.greatCircleEndPosition(positionO.getLatLon(), Angle.fromDegrees(positionHeading.getHeading()).add(Angle.POS90), Angle.fromDegrees(vesselSize)), 100);

			Vec4 vF = dc.getModel().getGlobe().computePointFromPosition(positionF.getLatitude(), positionF.getLongitude(), 100);
			Vec4 vL = dc.getModel().getGlobe().computePointFromPosition(positionL.getLatitude(), positionL.getLongitude(), 100);
			Vec4 vR = dc.getModel().getGlobe().computePointFromPosition(positionR.getLatitude(), positionR.getLongitude(), 100);

			gl.glBegin(primitive);
			gl.glVertex3d(vF.x, vF.y, vF.z);
			gl.glVertex3d(vL.x, vL.y, vL.z);
			gl.glVertex3d(vR.x, vR.y, vR.z);
			gl.glEnd();

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


	public void render(DrawContext dc, long time) {
		setTime(time);
		render(dc);
	}

	public Sailor getSailor() {
		return sailor;
	}

	public void update() {
		positionHeading = sailor.getVesselPositionHeading(time);
	}

	public void setPositionHeadingNull() {
		positionHeading = null;
	}
	
	public void setVesselSize(double vesselSize) {
		this.vesselSize = vesselSize;
	}

}
