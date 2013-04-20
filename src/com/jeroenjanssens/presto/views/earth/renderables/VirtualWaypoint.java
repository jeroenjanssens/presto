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

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import javax.media.opengl.GL;

import org.eclipse.swt.graphics.Color;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class VirtualWaypoint implements Renderable {

	private Position position;
	private Color color;

	public VirtualWaypoint(Position position, Color color) {
		this.position = position;
		this.color = color;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}


	public void render(DrawContext dc) {
		if(dc.isPickingMode()) return;
		if(color == null) return;
		if(position == null) return;

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

			int primitive = GL.GL_POLYGON;
			int size = 3;
			
			gl.glColor4ub((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte) 255);

			Vec4 surfacePoint = computePoint(dc, position, true);
			//BezierVertex surfacePoint = waypoint.getVertices().get(0);
			double horizon = dc.getView().computeHorizonDistance();
			double eyeDistance = dc.getView().getEyePoint().distanceTo3(new Vec4(surfacePoint.x, surfacePoint.y, surfacePoint.z));
			if (eyeDistance < horizon) {
				Vec4 screenPoint = getPoint(dc, position);

				gl.glBegin(primitive);
				gl.glVertex2d(screenPoint.x-size, screenPoint.y-size);
				gl.glVertex2d(screenPoint.x+size, screenPoint.y-size);
				gl.glVertex2d(screenPoint.x+size, screenPoint.y+size);
				gl.glVertex2d(screenPoint.x-size, screenPoint.y+size);
				gl.glEnd();
			} //end if visible

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
