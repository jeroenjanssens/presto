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

package com.jeroenjanssens.presto.views.properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;




/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class WaypointPropertiesComposite extends AbstractPropertiesComposite {

	private JiglooWaypointComposite contentComposite = null;
	private CopyOnWriteArrayList<Waypoint> selectedWaypoints;
	private GeneralPropertiesView propertiesView;

	public WaypointPropertiesComposite(Composite parent, int style, GeneralPropertiesView propertiesView) {
		super(parent, style, propertiesView);
		this.propertiesView = propertiesView;
		this.title = "Waypoint";
		this.setAlwaysShowScrollBars(true);
		this.setLayout(new GridLayout());

		GridData group1LData = new GridData();
		group1LData.verticalAlignment = GridData.FILL;
		group1LData.grabExcessHorizontalSpace = true;
		group1LData.grabExcessVerticalSpace = true;
		group1LData.horizontalAlignment = GridData.FILL;

		this.setLayoutData(group1LData);
		contentComposite = new JiglooWaypointComposite(this, SWT.NONE);
		contentComposite.setLayoutData(group1LData);

		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		this.setContent(contentComposite);
		this.setSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setMinHeight(contentComposite.getBounds().height);
	}

	public GeneralPropertiesView getPropertiesView() {
		return propertiesView;
	}

	public void setSelectionWaypoint(Waypoint waypoint) {
		CopyOnWriteArrayList<Waypoint> waypoints = new CopyOnWriteArrayList<Waypoint>();
		waypoints.add(waypoint);
		propertiesView.updateWaypointSelection(waypoints);
	}

	public void setSelection(CopyOnWriteArrayList<Waypoint> waypoints) {
		if(waypoints == null) waypoints = new CopyOnWriteArrayList<Waypoint>();
		selectedWaypoints = waypoints;

		clearValues();

		boolean enableSelection =  ((this.propertiesView.getTrackComposite().getSelectedTracks() != null) && (this.propertiesView.getTrackComposite().getSelectedTracks().size() == 1));

		if(selectedWaypoints.isEmpty()) {
			contentComposite.enable(false, enableSelection);
			updateSelectionText();
		} else if(selectedWaypoints.size() == 1) {
			//There is only one waypoint selected
			Waypoint w = waypoints.get(0);
			contentComposite.enable(true, enableSelection);
			loadLatitude(w);
			loadLongitude(w);
			loadSpeed(w);
			loadAngle(w);
			loadETA(w);
			updateSelectionText();
		} else {
			//There are multiple waypoints selected			
			//Check if there are values which they have in common
			boolean vLatitudeSame = true;
			boolean vLongitudeSame = true;
			boolean vSpeedSame = true;
			boolean vAngleSame = true;
			contentComposite.enable(true, enableSelection);
			for(int i = 0; i < selectedWaypoints.size()-1; i++) {
				if(selectedWaypoints.get(i).getLatitude() != selectedWaypoints.get(i+1).getLatitude()) vLatitudeSame = false;
				if(selectedWaypoints.get(i).getLongitude() != selectedWaypoints.get(i+1).getLongitude()) vLongitudeSame = false;	
				if(selectedWaypoints.get(i).getSpeed() != selectedWaypoints.get(i+1).getSpeed()) vSpeedSame = false;
				if(selectedWaypoints.get(i).getAngle() != selectedWaypoints.get(i+1).getAngle()) vAngleSame = false;
			}

			if(vLatitudeSame) loadLatitude(selectedWaypoints.get(0));
			if(vLongitudeSame) loadLongitude(selectedWaypoints.get(0));
			if(vSpeedSame) loadSpeed(selectedWaypoints.get(0));
			if(vAngleSame) loadAngle(selectedWaypoints.get(0));
			//Always show the time and date of the first waypoint
			loadETA(selectedWaypoints.get(0));
			updateSelectionText();
		}
		this.propertiesView.getEarthView().setCurrentSelectedWaypoints(selectedWaypoints);
		this.propertiesView.updateEarthView();
	}



	public void restore() {
		setSelection(selectedWaypoints);
	}


	public void clearValues() {

		//andere velden ook toevoegen
		contentComposite.vSelection.setText("");
		contentComposite.vLatitude.setText("");
		contentComposite.vLongitude.setText("");
		contentComposite.vETA.setSelection(null);
		contentComposite.vSpeed.setText("");
		contentComposite.vAngle.setText("");
	}


	public void updateSelectionText() {
		if(selectedWaypoints.isEmpty()) {
			contentComposite.vSelection.setText("<no waypoint or track selected>");
		} else if(selectedWaypoints.size() == 1) {
			//select appropiate waypoint in list
			contentComposite.vSelection.removeAll();
			contentComposite.vSelection.add("<no waypoints>");
			contentComposite.vSelection.add("<all waypoint of track>");
			contentComposite.vSelection.add("<first waypoint of track>");
			contentComposite.vSelection.add("<last waypoint of track>");

			for(Waypoint w : selectedWaypoints.get(0).getTrack().getWaypoints()) {
				contentComposite.vSelection.add("Waypoint " + (w.getId()+1));
			}

			contentComposite.vSelection.select(selectedWaypoints.get(0).getId() + 4);

		} else {
			if((this.propertiesView.getTrackComposite().getSelectedTracks() == null) || this.propertiesView.getTrackComposite().getSelectedTracks().isEmpty()) {
				//System.out.println("I have selected multiple waypoint from no tracks???");
			}

			if(this.propertiesView.getTrackComposite().getSelectedTracks().size() == 1) {

				contentComposite.vSelection.removeAll();
				contentComposite.vSelection.add("<no waypoints>");
				contentComposite.vSelection.add("<all waypoint of track>");
				contentComposite.vSelection.add("<first waypoint of track>");
				contentComposite.vSelection.add("<last waypoint of track>");

				for(Waypoint w : selectedWaypoints.get(0).getTrack().getWaypoints()) {
					contentComposite.vSelection.add("Waypoint " + (w.getId()+1));
				}
				contentComposite.vSelection.setText("<" + selectedWaypoints.size() + " waypoints selected>");
			} else {
				contentComposite.vSelection.setText("<" + selectedWaypoints.size() + " waypoints of " + this.propertiesView.getTrackComposite().getSelectedTracks().size() + " tracks selected>"); 
			}
		}
	}

	public void loadLatitude(Waypoint w) {
		contentComposite.vLatitude.setText("" + w.getLatitude());
	}

	public void loadLongitude(Waypoint w) {
		contentComposite.vLongitude.setText("" + w.getLongitude());
	}

	private void loadETA(Waypoint w) {
		contentComposite.vETA.setSelection(new Date(w.getTime()));
	}

	public void loadSpeed(Waypoint w) {
		contentComposite.vSpeed.setText("" + w.getSpeed());
	}

	public void loadAngle(Waypoint w) {
		contentComposite.vAngle.setText("" + w.getAngle());
	}

	public void saveLatitude() {
		if(selectedWaypoints == null) return;

		double latitude = 0;
		try {
			latitude = Double.parseDouble(contentComposite.vLatitude.getText().replace(",", "."));
		} catch(NumberFormatException e) {
			latitude = selectedWaypoints.get(0).getLatitude();
			MessageDialog.openError(this.getShell(), "Error", "Please enter a real value (e.g., '0.001', '3.1415').");
		} finally {
			for(Waypoint w : selectedWaypoints) {
				w.setLatitude(latitude);	
			}
			restore();
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void saveLongitude() {
		if(selectedWaypoints == null) return;

		double longitude = 0;
		try {
			longitude = Double.parseDouble(contentComposite.vLongitude.getText().replace(",", "."));
		} catch(NumberFormatException e) {
			longitude = selectedWaypoints.get(0).getLongitude();
			MessageDialog.openError(this.getShell(), "Error", "Please enter a real value (e.g., '0.001', '3.1415').");
		} finally {
			for(Waypoint w : selectedWaypoints) {
				w.setLongitude(longitude);
			}
			restore();
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void saveETA() {
		if(selectedWaypoints == null) return;

		//System.out.println("saveETA!");
		
		long newWaypointTime = contentComposite.vETA.getSelection().getTime();

		for(Waypoint w : selectedWaypoints) {
			long oldWaypointTime = w.getTime();
			long difference = newWaypointTime - oldWaypointTime;
			
			Track t = w.getTrack();
			long oldTrackTime = t.getTime();
			long newTrackTime = oldTrackTime + difference;
			t.setTime(newTrackTime);
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void saveSpeed() {
		if(selectedWaypoints == null) return;

		double speed = -1;
		try {
			speed = Double.parseDouble(contentComposite.vSpeed.getText().replace(",", "."));
			if(speed < 1) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			speed = selectedWaypoints.get(0).getSpeed();
			MessageDialog.openError(this.getShell(), "Error", "Please enter a real value (e.g., '0.001', '3.1415').");
		} finally {
			for(Waypoint w : selectedWaypoints) {
				w.setSpeed(speed);
			}
			restore();
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void saveAngle() {
		if(selectedWaypoints == null) return;

		double angle = 0;
		try {
			angle = Double.parseDouble(contentComposite.vAngle.getText().replace(",", "."));
			if(angle < 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			angle = selectedWaypoints.get(0).getAngle();
			MessageDialog.openError(this.getShell(), "Error", "Please enter a real value (e.g., '0.001', '3.1415').");
		} finally {
			for(Waypoint w : selectedWaypoints) {
				w.setAngle(angle);
			}
			restore();
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void updateTracksOfWaypoints() {
		ArrayList<Track> tracksToUpdate = new ArrayList<Track>();

		for(Waypoint waypoint : selectedWaypoints) {
			if(!tracksToUpdate.contains(waypoint.getTrack())) {
				tracksToUpdate.add(waypoint.getTrack());
			}
		}

		for(Track track : tracksToUpdate) {
			track.update(this.propertiesView.getEarthView());
		}
	}

	public void updateSelectionLatitudeLongitude() {
		loadLatitude(selectedWaypoints.get(0));
		loadLongitude(selectedWaypoints.get(0));
	}

	public void updateAll() {
		propertiesView.getEarthView().getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateTracksOfWaypoints();
				propertiesView.getEarthView().getWWD().redraw();
				propertiesView.getEarthView().getCurrentScenarioEditor().getTimeLine().doRedraw();
			}
		});
	}

	public void selectFromCombo() {

		if(propertiesView.getTrackComposite().getSelectedTracks() == null) return;
		if(propertiesView.getTrackComposite().getSelectedTracks().size() > 1) return;

		Track track = propertiesView.getTrackComposite().getSelectedTracks().get(0);
		if(track == null) return;

		int index = contentComposite.vSelection.getSelectionIndex();

		switch(index) {
		case -1:
		case 0:
			this.setSelection(null);
			break;
		case 1:
			//System.out.println("Select all waypoints");
			this.setSelection(track.getWaypoints());
			break;
		case 2:
			//System.out.println("Select first waypoint");
			this.setSelectionWaypoint(track.getWaypoint(0));
			break;
		case 3:
			//System.out.println("Select last waypoint");
			this.setSelectionWaypoint(track.getLastWaypoint());
			break;
		default:
			//System.out.println("Select waypoint: " + (index -4));
		this.setSelectionWaypoint(track.getWaypoint(index-4));	
		break;
		}
	}
}
