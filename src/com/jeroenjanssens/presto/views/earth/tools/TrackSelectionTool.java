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

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.WorldMapLayer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.TrackTools;
import com.jeroenjanssens.presto.views.earth.layers.TrackLayer;
import com.jeroenjanssens.presto.views.earth.renderables.RectangularSelection;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackSelectionTool extends AbstractTool {

	private Position currentPosition = null;
	private RectangularSelection rectangularSelection = null;

	private ArrayList<Track> tracksToUpdate = new ArrayList<Track>();


	public TrackSelectionTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Track Selection Tool (Alt+T)", "icons/tool_trackselection.png", EarthViewToolBar.TOOL_TRACKSELECTION);
	}

	private void synchronizeSelection() {
		earthViewToolBar.getEarthView().getDisplay().asyncExec(new Runnable () {
			public void run () {
				earthViewToolBar.getEarthView().getPropertiesView().updateTrackSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks());
				earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().setSelection(new StructuredSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks()), true);
			}
		});
	}
	
	@Override
	public void setFocus(boolean focus) {
		if(!focus) {
			removeRectangularSelection();
		}
	}

	@Override
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		if(picking) return true;
		return (layer instanceof TrackLayer);
	}

	@Override
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		if(getWWD().getObjectsAtCurrentPosition() == null) return;

		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Track) {
			setCursor(PrestoCursor.CURSOR_HOVER_TRACK);
			return;
		} else if(o instanceof Waypoint) {
			setCursor(PrestoCursor.CURSOR_HOVER_TRACK);
			return;
		}
		setCursor(PrestoCursor.CURSOR_DEFAULT);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);
		tracksToUpdate.clear();
		Object object = this.getPrestoInputHandler().getObjectsAtButtonPress().getTopObject();
		
		
		
		if(downLeft) {
			if(object instanceof Track) {
				Track track = (Track) object;
				currentPosition = this.getWWD().getCurrentPosition();
				if(!downShift) selectOnly(track);
				if(downShift && !downCtrl) alsoSelect(track);
				if(downShift && downCtrl) toggleSelect(track);
			} else if(object instanceof Waypoint) {
				Track track = ((Waypoint) object).getTrack();
				currentPosition = this.getWWD().getCurrentPosition();
				if(!downShift) selectOnly(track);
				if(downShift && !downCtrl) alsoSelect(track);
				if(downShift && downCtrl) toggleSelect(track);		
			} else if(!(object instanceof WorldMapLayer)) {
				startRectangularSelection(e);
			} 
			
		} else if(downRight) {
			if(object instanceof Waypoint) {
				Waypoint waypoint = (Waypoint) object;
				earthViewToolBar.getEarthView().getMenuManager().getWaypointMenu().showMenu(waypoint, e.getLocationOnScreen());
			} else if(object instanceof Track) {
				Track track = (Track) object;
				earthViewToolBar.getEarthView().getMenuManager().getTrackMenu().showMenu(track, e.getLocationOnScreen());
			}
		}
		synchronizeSelection();
		update(e);
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);

		if(downLeft) {
			if(rectangularSelectionExists()) {
				updateRectangularSelection(e);
			} else {
				moveSelectedTracks(e);
			}
		}

		update(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);

		if(rectangularSelectionExists()) {
			System.out.println("rectangularSelectionExists");
			if(rectangularSelectionDrawn()) {
				System.out.println("rectangularSelectionDrawn");

				if(downShift && !downCtrl) addTracksWithinRectangularSelection(false);
				if(downShift && downCtrl) removeWaypointsWithinRectangularSelection();
				if(!downShift) addTracksWithinRectangularSelection(true);
			} else {
				System.out.println("clearSelection");
				if(!downShift) {
					clearSelection();
				}
			}
			removeRectangularSelection();
			synchronizeSelection();
		} 
		
		for(Track track : tracksToUpdate) {
			//TrackTools.recalculateVertices(track);
			//track.getVessel().getSailor().recalculateTimes();
			//track.getVessel().update();
			track.update(this.earthViewToolBar.getEarthView());
		}
		
		update(e);
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			
			deleteTracks();
			e.consume();
		} else {
			super.keyPressed(e);
		}
	}
	
	private void deleteTracks() {
		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				for(Track track : earthViewToolBar.getEarthView().getCurrentSelectedTracks()) {
					track.getParent().remove(track);
				}
				earthViewToolBar.updateTrackTreeViewer();
			}
			
		});
		setScenarioChanged();
		update(null);
	}

	private void clearSelection() {
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().clear();
		tracksToUpdate.clear();
	}

	private void selectOnly(Track track) {
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().clear();
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(track);
	}

	private void alsoSelect(Track track) {
		if(!earthViewToolBar.getEarthView().getCurrentSelectedTracks().contains(track)) {
			earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(track);
		}
	}

	private void toggleSelect(Track track) {
		if(earthViewToolBar.getEarthView().getCurrentSelectedTracks().contains(track)) {
			earthViewToolBar.getEarthView().getCurrentSelectedTracks().remove(track);
		} else {
			earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(track);
		}
	}

	private void startRectangularSelection(MouseEvent e) {
		rectangularSelection = new RectangularSelection();
		toolLayer.addRenderable(rectangularSelection);
		Point first = this.getWWD().getMousePosition();
		if(first != null) {
			first.y = this.getWWD().getHeight() - first.y;
			rectangularSelection.setFirstPoint(first);
		}
	}

	private boolean rectangularSelectionExists() {
		if(rectangularSelection == null) return false; 
		if(rectangularSelection.getFirstPoint() == null) return false; 
		return true;
	}

	private boolean rectangularSelectionDrawn() {
		if(rectangularSelection == null) return false; 
		if(rectangularSelection.getFirstPoint() == null) return false; 
		if(rectangularSelection.getSecondPoint() == null) return false;
		return true;
	}

	private void updateRectangularSelection(MouseEvent e) {
		if(rectangularSelection == null) return; 
		if(rectangularSelection.getFirstPoint() == null) return; 

		Point second = this.getWWD().getMousePosition();
		if(second != null) {
			second.y = this.getWWD().getHeight() - second.y;
			rectangularSelection.setSecondPoint(second);
		}
	}

	private void removeRectangularSelection() {
		if(rectangularSelection != null) {
			toolLayer.removeRenderable(rectangularSelection);
			rectangularSelection = null;
		}
	}

	private void addTracksWithinRectangularSelection(boolean clear) {
		if(clear) earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().clear();

		if(rectangularSelection == null) return; 
		if(rectangularSelection.getFirstPoint() == null) return; 
		if(rectangularSelection.getSecondPoint() == null) return; 

		Rectangle rect = rectangularSelection.getRectangle();


		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor() == null) return;
		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario() == null) return;

		ArrayList<Waypoint> waypoints = earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder().getAllWaypoints(true, true);
		for(Waypoint w : waypoints) {
			if(rect.contains(w.getScreenPoint())) {
				if(!earthViewToolBar.getEarthView().getCurrentSelectedTracks().contains(w.getTrack())) {
					earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(w.getTrack());
				}
			}
		}
	}

	private void removeWaypointsWithinRectangularSelection() {

		if(rectangularSelection == null) return; 
		if(rectangularSelection.getFirstPoint() == null) return; 
		if(rectangularSelection.getSecondPoint() == null) return; 

		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor() == null) return;
		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario() == null) return;

		Rectangle rect = rectangularSelection.getRectangle();
		ArrayList<Waypoint> waypoints = earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder().getAllWaypoints(true, true);
		for(Waypoint w : waypoints) {
			if(rect.contains(w.getScreenPoint())) {

				if(earthViewToolBar.getEarthView().getCurrentSelectedTracks().contains(w.getTrack())) {
					earthViewToolBar.getEarthView().getCurrentSelectedTracks().remove(w.getTrack());
				}				
			}
		}
	}

	private void moveSelectedTracks(MouseEvent e) {		

		Position newPosition = this.getWWD().getCurrentPosition();
		if(newPosition == null) return;
		//Angle diffAzimuth = LatLon.greatCircleAzimuth(currentPosition.getLatLon(), newPosition.getLatLon());
		//Angle diffDistance = LatLon.greatCircleDistance(currentPosition.getLatLon(), newPosition.getLatLon());

		tracksToUpdate.clear();

		ArrayList<Track> tracksToMove = earthViewToolBar.getEarthView().getCurrentSelectedTracks();

		for(Track track : tracksToMove) {
			for(Waypoint waypoint : track.getWaypoints()) {
				Angle az = LatLon.greatCircleAzimuth(currentPosition.getLatLon(), waypoint.getPosition().getLatLon());
				Angle dis = LatLon.greatCircleDistance(currentPosition.getLatLon(), waypoint.getPosition().getLatLon());
				waypoint.setPosition(new Position(LatLon.greatCircleEndPosition(newPosition.getLatLon(), az, dis), 200));
			}
			
			if(!tracksToUpdate.contains(track)) {
				tracksToUpdate.add(track);
			}
		}

		for(Track track : tracksToUpdate) {
			//track.update();
			track.getVessel().update();
			this.earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTimeLine().doRedraw();
			TrackTools.recalculateVertices(track);
		}

		currentPosition = newPosition;
		setScenarioChanged();



	}


}
