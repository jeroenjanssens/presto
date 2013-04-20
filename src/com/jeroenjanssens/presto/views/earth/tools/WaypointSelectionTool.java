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
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.viewers.StructuredSelection;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.TrackTools;
import com.jeroenjanssens.presto.views.earth.renderables.RectangularSelection;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class WaypointSelectionTool extends AbstractTool {

	private Position currentPosition = null;
	private RectangularSelection rectangularSelection = null;

	private ArrayList<Track> tracksToUpdate = new ArrayList<Track>();

	public WaypointSelectionTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Waypoint Selection Tool (Alt+W)", "icons/tool_waypointselection.png", EarthViewToolBar.TOOL_WAYPOINTSELECTION);	
	}

	private void synchronizeSelection() {
		earthViewToolBar.getEarthView().getDisplay().asyncExec(new Runnable () {
			public void run () {
				earthViewToolBar.getEarthView().getPropertiesView().updateWaypointSelection(earthViewToolBar.getEarthView().getCurrentSelectedWaypoints());
				earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().setSelection(new StructuredSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks()), true);
			}
		});
	}
	
	private void synchronizeSelectionTracks() {
		earthViewToolBar.getEarthView().getDisplay().asyncExec(new Runnable () {
			public void run () {
				earthViewToolBar.getEarthView().getPropertiesView().updateTrackSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks());
				earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().setSelection(new StructuredSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks()), true);
			}
		});
	}

	private void updateSelectionLatitudeLongitude() {
		earthViewToolBar.getEarthView().getDisplay().asyncExec(new Runnable () {
			public void run () {
				earthViewToolBar.getEarthView().getPropertiesView().updateSelectionLatitudeLongitude(earthViewToolBar.getEarthView().getCurrentSelectedWaypoints());
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
		return true;
	}

	@Override
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		if(getWWD().getObjectsAtCurrentPosition() == null) return;

		Object o = getWWD().getObjectsAtCurrentPosition().getTopObject();
		if(o instanceof Waypoint) {
			setCursor(PrestoCursor.CURSOR_HOVER_WAYPOINT);
			return;
		} else if(o instanceof Track) {
			setCursor(PrestoCursor.CURSOR_HOVER_TRACK);
			return;
		}
		setCursor(PrestoCursor.CURSOR_DEFAULT);
	}



	@Override
	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		setDowns(e);
		Object object = this.getPrestoInputHandler().getObjectsAtButtonPress().getTopObject();

		if(downLeft) {
			if(object instanceof Waypoint) {
				Waypoint waypoint = (Waypoint) object;
				currentPosition = this.getWWD().getCurrentPosition();
				if(!downShift) selectOnly(waypoint);
				if(downShift && !downCtrl) alsoSelect(waypoint);
				if(downShift && downCtrl) toggleSelect(waypoint);
			} else if(object instanceof Track) {
				clearSelection();
				Track track = (Track) object;
				selectOnly(track);
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
				moveSelectedWaypoints(e);
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

				if(downShift && !downCtrl) addWaypointsWithinRectangularSelection(false);
				if(downShift && downCtrl) removeWaypointsWithinRectangularSelection();
				if(!downShift) addWaypointsWithinRectangularSelection(true);
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
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteWaypoints();
			e.consume();
		} else {
			super.keyPressed(e);
		}
	}
	
	private void deleteWaypoints() {
		tracksToUpdate.clear();
		for(Waypoint waypoint : earthViewToolBar.getEarthView().getCurrentSelectedWaypoints()) {
			waypoint.getTrack().removeWaypoint(waypoint);
			if(!tracksToUpdate.contains(waypoint.getTrack())) {
				tracksToUpdate.add(waypoint.getTrack());
			}
		}
		
		for(Track track : tracksToUpdate) {
			track.update(this.earthViewToolBar.getEarthView());
		}
		
		earthViewToolBar.updateTrackTreeViewer();
		setScenarioChanged();
		update(null);
	}

	private void clearSelection() {
		earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().clear();
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().clear();
		tracksToUpdate.clear();
		synchronizeSelection();
	}

	private void selectOnly(Waypoint waypoint) {
		earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().clear();
		earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().add(waypoint);
		synchronizeSelection();
		updateSelectedTracks();
	}

	private void selectOnly(Track track) {
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().clear();
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(track);
		synchronizeSelection();
		synchronizeSelectionTracks();
	}


	private void alsoSelect(Waypoint waypoint) {
		if(!earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().contains(waypoint)) {
			earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().add(waypoint);
		}
		synchronizeSelection();
		updateSelectedTracks();
	}

	private void toggleSelect(Waypoint waypoint) {
		if(earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().contains(waypoint)) {
			earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().remove(waypoint);
		} else {
			earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().add(waypoint);
		}
		synchronizeSelection();
		updateSelectedTracks();
	}

	private void updateSelectedTracks() {
		earthViewToolBar.getEarthView().getCurrentSelectedTracks().clear();
		for(Waypoint waypoint : earthViewToolBar.getEarthView().getCurrentSelectedWaypoints()) {
			if(!earthViewToolBar.getEarthView().getCurrentSelectedTracks().contains(waypoint.getTrack())) {
				earthViewToolBar.getEarthView().getCurrentSelectedTracks().add(waypoint.getTrack());
			}
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

	private void addWaypointsWithinRectangularSelection(boolean clear) {
		if(clear) earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().clear();

		if(rectangularSelection == null) return; 
		if(rectangularSelection.getFirstPoint() == null) return; 
		if(rectangularSelection.getSecondPoint() == null) return; 

		Rectangle rect = rectangularSelection.getRectangle();

		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor() == null) return;
		if(earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario() == null) return;

		ArrayList<Waypoint> waypoints = earthViewToolBar.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder().getAllWaypoints(true, true);
		for(Waypoint w : waypoints) {
			if(w.getScreenPoint() != null) {
				if(rect.contains(w.getScreenPoint())) {
					if(!earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().contains(w)) {
						earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().add(w);
					}
				}
			}
		}
		updateSelectedTracks();
		synchronizeSelection();
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
				if(earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().contains(w)) {
					earthViewToolBar.getEarthView().getCurrentSelectedWaypoints().remove(w);
				}
			}
		}
		updateSelectedTracks();
		synchronizeSelection();
	}

	private void moveSelectedWaypoints(MouseEvent e) {

		Position newPosition = this.getWWD().getCurrentPosition();
		if(newPosition == null) return;
		//Angle diffAzimuth = LatLon.greatCircleAzimuth(currentPosition.getLatLon(), newPosition.getLatLon());
		//Angle diffDistance = LatLon.greatCircleDistance(currentPosition.getLatLon(), newPosition.getLatLon());

		tracksToUpdate.clear();

		CopyOnWriteArrayList<Waypoint> waypointsToMove = earthViewToolBar.getEarthView().getCurrentSelectedWaypoints();

		for(Waypoint waypoint : waypointsToMove) {
			Angle az = LatLon.greatCircleAzimuth(currentPosition.getLatLon(), waypoint.getPosition().getLatLon());
			Angle dis = LatLon.greatCircleDistance(currentPosition.getLatLon(), waypoint.getPosition().getLatLon());

			waypoint.setPosition(new Position(LatLon.greatCircleEndPosition(newPosition.getLatLon(), az, dis), 200));
			if(!tracksToUpdate.contains(waypoint.getTrack())) {
				tracksToUpdate.add(waypoint.getTrack());
			}
		}

		for(Track track : tracksToUpdate) {
			//track.update();
			track.getVessel().update();
			this.earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTimeLine().doRedraw();
			TrackTools.recalculateVertices(track);
		}

		updateSelectionLatitudeLongitude();

		currentPosition = newPosition;
		setScenarioChanged();
	}
}
