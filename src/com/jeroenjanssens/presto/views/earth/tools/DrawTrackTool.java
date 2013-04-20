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
import gov.nasa.worldwind.layers.WorldMapLayer;

import java.awt.event.MouseEvent;

import org.eclipse.swt.widgets.Display;

import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.layers.WaypointLayer;
import com.jeroenjanssens.presto.views.earth.renderables.VirtualSegment;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class DrawTrackTool extends AbstractTool {

	private VirtualSegment virtualSegment = null;
	
	public DrawTrackTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Draw Track Tool (Alt+D)", "icons/tool_drawtrack.png", EarthViewToolBar.TOOL_DRAWTRACK);
	}

	@Override
	public void setFocus(boolean focus) {
		if(!focus) {
			clearVirtualSegment();
		} else {
			clearVirtualSegment();
		}
	}
	
	@Override
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		if(!picking) return true;
		return (layer instanceof WaypointLayer);
	}
	
	@Override
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		if(getWWD().getObjectsAtCurrentPosition() == null) return;
		
		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Waypoint) {
			Waypoint w = (Waypoint) o;
			if(w.isFirst() || w.isLast()) {
				setCursor(PrestoCursor.CURSOR_HOVER_WAYPOINT);
				return;
			}
		} 
		setCursor(PrestoCursor.CURSOR_DEFAULT);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		//System.out.println("mousePressed in " + name);
		setDowns(e);
		Object object = this.getPrestoInputHandler().getObjectsAtButtonPress().getTopObject();
		
		if(downLeft) {
			if(object instanceof Waypoint) {
				Waypoint waypoint = (Waypoint) object;
				if(virtualSegmentExists()) {
					//Joint two tracks;
					//connectToWaypoint(waypoint);
					clearVirtualSegment();
					//do not start a new virtual segment
				} else {
					clearVirtualSegment();
					if(waypoint.isFirst() || waypoint.isLast()) {
						newVirtualSegment(waypoint);
					}
				}
			} else if(!(object instanceof WorldMapLayer)) {
				if(virtualSegmentExists()) {
					Waypoint newWaypoint = getNewWaypointFromCurrentMousePosition();
					extendTrack(newWaypoint);
					clearVirtualSegment();
					newVirtualSegment(newWaypoint);
				} else {
					Waypoint newWaypoint = startNewTrack();
					clearVirtualSegment();
					if(newWaypoint != null) {
						newVirtualSegment(newWaypoint);
					}
				}
				
			}
		} else {
			if(virtualSegmentExists()) {
				Waypoint w = virtualSegment.getStartWaypoint();
				if(w != null) {
					Track t = w.getTrack();
					if(t != null) {
						if(t.getWaypoints().size() < 2) {
							t.removeWaypoint(w);
						}
					}
				}
			}
			
			clearVirtualSegment();
		}
		
		update(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);
		if(virtualSegmentExists()) {
			Position position = getWWD().getCurrentPosition();
			if(position != null) {
				updateVirtualSegment(position);
			}
		}
		update(e);
	}
	
	private Waypoint startNewTrack() {
		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor() == null) return null;
		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario() == null) return null;
		final Folder root = earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder();
		
		final Waypoint newWaypoint = new Waypoint();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Track track = new Track("New Track");
				track.setTime(earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTimeLine().getCursorTime());
				newWaypoint.updateWaypoint(track, getWWD().getCurrentPosition());
				root.add(track);
				track.addWaypoint(newWaypoint);
			}
		});
		newWaypoint.getTrack().update(this.earthViewToolBar.getEarthView());
		setScenarioChanged();
		return newWaypoint;
	}

	private boolean virtualSegmentExists() {
		if (virtualSegment == null) return false;
		if(virtualSegment.getStartWaypoint() == null) return false;
		return true;
	}

	private void newVirtualSegment(Waypoint waypoint) {
		virtualSegment = new VirtualSegment(waypoint);
		virtualSegment.setEndPosition(waypoint.getPosition());
		toolLayer.addRenderable(virtualSegment);
	}
	
	private void clearVirtualSegment() {
		if(virtualSegment != null) {
			toolLayer.removeRenderable(virtualSegment);
			virtualSegment = null;
		}
	}
	
	private void updateVirtualSegment(Position position) {
		virtualSegment.setEndPosition(new Position(position.getLatLon(), 200));
	}
	
	private Waypoint getNewWaypointFromCurrentMousePosition() {
		Position position = getWWD().getCurrentPosition();
		if(position == null) return null;
		Track track = virtualSegment.getStartWaypoint().getTrack();
		Waypoint newWaypoint = new Waypoint(track, position);
		return newWaypoint;
	}
	
	private void extendTrack(Waypoint waypoint) {
		final Track track = virtualSegment.getStartWaypoint().getTrack();
		if(virtualSegment.getStartWaypoint().isLast()) {
			//System.out.println("isLast()");
			track.addWaypoint(waypoint);
			setScenarioChanged();
			track.update(this.earthViewToolBar.getEarthView());
			//TrackTools.recalculateVertices(track);
			//track.getVessel().getSailor().recalculateTimes();
		} else {
			//System.out.println("isFirst()");
			track.addWaypoint(0, waypoint);
			//track.getVessel().getSailor().recalculateTimes();
			//track.setTime(track.getTime() - (track.getWaypoint(1).getTime() - track.getTime()));
			//TrackTools.recalculateVertices(track);
			//track.getVessel().getSailor().recalculateTimes();
			setScenarioChanged();
			track.update(this.earthViewToolBar.getEarthView());
			
			/*
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					TrackTools.recalculateVertices(track);
					track.getVessel().getSailor().recalculateTimes();
					System.out.println("shifting time");
					track.shiftTime(-(track.getWaypoint(1).getTime() - track.getWaypoint(0).getTime()));
					System.out.println("done shifting time");
					track.getVessel().getSailor().recalculateTimes();
					System.out.println("done recalc times");
					for(Waypoint w : track.getWaypoints()) {
						System.out.println("w " + w.getId() + " -> " + w.getDistanceMarkers().size());
					}
					
					//track.getVessel().update();
				}
			});
			*/
			//track.update();
		}
	}
}
