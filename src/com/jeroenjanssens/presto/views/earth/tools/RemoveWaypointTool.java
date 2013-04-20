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

import gov.nasa.worldwind.layers.RenderableLayer;

import java.awt.event.MouseEvent;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class RemoveWaypointTool extends AbstractTool {

	public RemoveWaypointTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Remove Waypoint Tool (Alt+R)","icons/tool_remove_vertex.png", EarthViewToolBar.TOOL_REMOVEWAYPOINT);
	}

	@Override
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		return true;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		Object o = this.getPrestoInputHandler().getObjectsAtButtonPress().getTopObject();
		if(o instanceof Waypoint) {
			Waypoint w = (Waypoint) o;
			Track t = w.getTrack();
			t.removeWaypoint(w);
			earthViewToolBar.updateTrackTreeViewer();
			t.update(this.earthViewToolBar.getEarthView());
			setScenarioChanged();
		}
		update(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		update(e);
	}
	
	@Override
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		if(getWWD().getObjectsAtCurrentPosition() == null) return;
		
		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Waypoint) {
			setCursor(PrestoCursor.CURSOR_HOVER_WAYPOINT);
		} else if(o instanceof Track) {
				setCursor(PrestoCursor.CURSOR_DEFAULT);
		} else {
			setCursor(PrestoCursor.CURSOR_DEFAULT);
		}
	}
	
	
}
