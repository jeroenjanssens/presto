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
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.eclipse.swt.graphics.Color;

import com.jeroenjanssens.presto.model.BezierVertex;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.TrackTools;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class VirtualSegment implements Renderable {

	private Waypoint startWaypoint;
	private Position endPosition;
	
	private Color color;
	
	public VirtualSegment(Waypoint startWaypoint) {
		this.startWaypoint = startWaypoint;
		this.color = startWaypoint.getTrack().getColor();
	}
	
	public void render(DrawContext dc) {
		if(dc.isPickingMode()) return;
		
		if(startWaypoint == null) return;
		if(endPosition == null) return;
		
		ArrayList<BezierVertex> vertices = TrackTools.recalculateVertices(startWaypoint, endPosition);
		
		GL gl = dc.getGL();
		gl.glPushAttrib(GL.GL_HINT_BIT 
				| GL.GL_CURRENT_BIT 
				| GL.GL_LINE_BIT);
		
		gl.glLineWidth(2.0f);
		gl.glColor4ub((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte) 255);
		
		gl.glBegin(GL.GL_LINE_STRIP);
		for(BezierVertex vertex : vertices) {
			gl.glVertex3d(vertex.x, vertex.y, vertex.z);
		}
		gl.glEnd();
		
		gl.glPopAttrib();
		
	}
	
	public Position getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}

	public Waypoint getStartWaypoint() {
		return startWaypoint;
	}

}
