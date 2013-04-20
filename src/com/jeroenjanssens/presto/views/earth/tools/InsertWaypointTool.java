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

package com.jeroenjanssens.presto.views.earth.tools;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;

import java.awt.event.MouseEvent;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.sailing.TrackTools;
import com.jeroenjanssens.presto.views.earth.layers.TrackLayer;
import com.jeroenjanssens.presto.views.earth.renderables.VirtualWaypoint;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class InsertWaypointTool extends AbstractTool {

	private VirtualWaypoint virtualWaypoint = null;
	
	public InsertWaypointTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Insert Waypoint Tool (Alt+I)","icons/add_vertext_mode.gif", EarthViewToolBar.TOOL_INSERTWAYPOINT);
	}

	@Override
	public void setFocus(boolean focus) {
		if(!focus) {
			removeVirtualWaypoint();
		}
	}
	
	@Override
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		if(picking) {
			return (layer instanceof TrackLayer);
		}
		return true;
	}
	
	@Override
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		if(getWWD().getObjectsAtCurrentPosition() == null) return;
		
		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Track) {
			//TODO: insert waypoint cursor
			setCursor(PrestoCursor.CURSOR_HOVER_WAYPOINT);
		} else {
			setCursor(PrestoCursor.CURSOR_DEFAULT);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);
		Object o = this.getPrestoInputHandler().getObjectsAtButtonPress().getTopObject();
	
		if(o instanceof Track) {
			Track track = (Track) o;
			insertWaypoint(track, getWWD().getCurrentPosition()); 
		}
		
		update(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		
		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Track) {
			Track track = (Track) o;
			updateVirtualWaypoint(track, getWWD().getCurrentPosition());
		} else {
			removeVirtualWaypoint();
		}
		
		update(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);
		
		update(e);
	}
	
	private void insertWaypoint(Track track, Position currentPosition) {
		TrackTools.getInsertPosition(track, currentPosition, true);
		track.update(this.earthViewToolBar.getEarthView());
		setScenarioChanged();
	}
	
	private void updateVirtualWaypoint(Track track, Position currentPosition) {
		Position p = TrackTools.getInsertPosition(track, currentPosition, false);
		if(p == null) return;
		if(virtualWaypoint != null) {
			virtualWaypoint.setPosition(p);
			virtualWaypoint.setColor(track.getColor());
		} else { 
			virtualWaypoint = new VirtualWaypoint(p, track.getColor());
			toolLayer.addRenderable(virtualWaypoint);
		}
	}
	
	private void removeVirtualWaypoint() {
		if(virtualWaypoint != null) {
			toolLayer.removeRenderable(virtualWaypoint);
			virtualWaypoint = null;
		}
	}

}
