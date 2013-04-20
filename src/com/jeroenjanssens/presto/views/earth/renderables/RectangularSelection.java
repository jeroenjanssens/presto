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

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.media.opengl.GL;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class RectangularSelection implements Renderable {
	
	private Color color = new Color(1f, 1f , 1f, 0.2f);
	
	private Point p1 = null;
	private Point p2 = null;
	
	public RectangularSelection() {
		
	}
	
	public void setFirstPoint(Point p) {
		p1 = p;
	}
	
	public Point getFirstPoint() {
		return p1;
	}
	
	public void setSecondPoint(Point p) {
		p2 = p;
	}
	
	public Point getSecondPoint() {
		return p2;
	}
	
	public Rectangle getRectangle() {
		//upperleft corner;
		
		int x = (p1.x < p2.x) ? p1.x : p2.x;
		int y = (p1.y < p2.y) ? p1.y : p2.y;
		
		int w = Math.abs(p1.x - p2.x);
		int h = Math.abs(p1.y - p2.y);
		
		return new Rectangle(x, y, w, h);
	}
	
	public void render(DrawContext dc) {
		
		if((p1 == null) || (p2 == null)) return;
		if(p1.equals(p2)) return;
		
		boolean attribsPushed = false;
		boolean modelviewPushed = false;
		boolean projectionPushed = false;

	
		
		GL gl = dc.getGL();

		try {
			gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT
					| GL.GL_COLOR_BUFFER_BIT
					| GL.GL_ENABLE_BIT
					| GL.GL_TEXTURE_BIT
					| GL.GL_TRANSFORM_BIT
					| GL.GL_VIEWPORT_BIT
					| GL.GL_CURRENT_BIT);
			attribsPushed = true;

			gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL.GL_DEPTH_TEST);
			
			
			java.awt.Rectangle viewport = dc.getView().getViewport();
			gl.glMatrixMode(javax.media.opengl.GL.GL_PROJECTION);
			gl.glPushMatrix();
			projectionPushed = true;
			gl.glLoadIdentity();
			int width = 100;
			int height = 100;
			double maxwh = width > height ? width : height;
			//gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

			gl.glOrtho(0d, viewport.width, 0d, viewport.height, -1 * maxwh, 1 * maxwh);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			modelviewPushed = true;
			gl.glLoadIdentity();

			//System.out.println("Viewport: " + viewport.getWidth() + ", " + viewport.getHeight());

			double x = viewport.getWidth() / 2;
			double y = viewport.getHeight() / 2;
			
			
			gl.glTranslated(p1.x, p1.y, 0);
			
			gl.glLineWidth(1);
			
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(0+0.5f, 0+0.5f);
			gl.glVertex2d(0+0.5f, p2.y-p1.y+0.5f);
			gl.glVertex2d(p2.x-p1.x+0.5f, p2.y-p1.y+0.5f);
			gl.glVertex2d(p2.x-p1.x+0.5f, 0+0.5f);
			gl.glVertex2d(0+0.5f, 0+0.5f);
			gl.glEnd();
			
			//if(!dc.isPickingMode()) {
				dc.getGL().glColor4ub((byte) this.color.getRed(), (byte) this.color.getGreen(), (byte) this.color.getBlue(), (byte) this.color.getAlpha());
			//}
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex2d(0, 0);
			gl.glVertex2d(0, p2.y-p1.y);
			gl.glVertex2d(p2.x-p1.x, p2.y-p1.y);
			gl.glVertex2d(p2.x-p1.x, 0);
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
	
	
	
}
