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
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.part.ViewPart;

import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class GeneralPropertiesView extends ViewPart {

	public static final String ID = "com.jeroenjanssens.presto.views.properties.scenariopropertiesview";

	public static final int COMPOSITE_SCENARIO = 0;
	public static final int COMPOSITE_TRACK = 1;
	public static final int COMPOSITE_WAYPOINT = 2;

	private TabFolder tabFolder;
	private ArrayList<IPropertiesComposite> propertiesComposites = new ArrayList<IPropertiesComposite>();

	private EarthView earthView = null;

	private ScenarioPropertiesComposite scenarioComposite;
	private TrackPropertiesComposite trackComposite;
	private WaypointPropertiesComposite waypointComposite;

	@Override
	public void createPartControl(Composite parent) {
		//scenarioManager = Activator.getDefault().getScenarioManager();
		//scenarioManager.


		tabFolder = new TabFolder (parent, SWT.BORDER);
		//tabFolder.setEnabled(false);
		tabFolder.setVisible(false);
		scenarioComposite = new ScenarioPropertiesComposite(tabFolder, SWT.V_SCROLL | SWT.BORDER, this);
		trackComposite = new TrackPropertiesComposite(tabFolder, SWT.V_SCROLL | SWT.BORDER, this);
		waypointComposite = new WaypointPropertiesComposite(tabFolder, SWT.V_SCROLL | SWT.BORDER, this);

		propertiesComposites.add(scenarioComposite);
		propertiesComposites.add(trackComposite);
		propertiesComposites.add(waypointComposite);

		for(IPropertiesComposite pc : propertiesComposites) {
			pc.build();
			pc.addToTabFolder(tabFolder);
		}

		tabFolder.pack ();

		//this.select(GeneralPropertiesView.COMPOSITE_TRACK);
	}

	@Override
	public void setFocus() {

	}


	public void select(int composite) {
		tabFolder.setSelection(propertiesComposites.get(composite).getTabItem());
	}
	/*
	public void setPanelVisible(boolean visible) {
		tabFolder.setVisible(visible);
	}
	 */

	public void updateScenarioSelection(Scenario scenario) {
		//currentScenario = scenario;

		if(scenario == null) {
			tabFolder.setVisible(false);
			return;
		}
		tabFolder.setVisible(true);
		select(GeneralPropertiesView.COMPOSITE_SCENARIO);
		scenarioComposite.setSelection(scenario);

		//perhaps I should also update the track and waypoint composites here

	}

	public void updateWaypointSelection(CopyOnWriteArrayList<Waypoint> selectedWaypoints) {
		
		ArrayList<Track> tracksOfWaypoints = null;
		
		if((selectedWaypoints != null) && (!selectedWaypoints.isEmpty())) {
		    select(GeneralPropertiesView.COMPOSITE_WAYPOINT);
		    
		    tracksOfWaypoints = new ArrayList<Track>();
		    for(Waypoint w : selectedWaypoints) {
				if(!tracksOfWaypoints.contains(w.getTrack())) {
					tracksOfWaypoints.add(w.getTrack());
				}
			}
		}
		trackComposite.setSelection(tracksOfWaypoints);
		waypointComposite.setSelection(selectedWaypoints);
	}

	public void updateTrackSelection(ArrayList<Track> selectedTracks) {
		trackComposite.setSelection(selectedTracks);
		CopyOnWriteArrayList<Waypoint> waypointsOfTracks = null;
		
		if((selectedTracks != null) && (!selectedTracks.isEmpty())) {
			select(GeneralPropertiesView.COMPOSITE_TRACK);
			waypointsOfTracks = new CopyOnWriteArrayList<Waypoint>();
				for(Track t : selectedTracks) {
				waypointsOfTracks.addAll(t.getWaypoints());
		   }
		}
		
		waypointComposite.setSelection(waypointsOfTracks);
	}

	public void updateSelectionLatitudeLongitude(CopyOnWriteArrayList<Waypoint> currentSelectedWaypoints) {
		waypointComposite.updateSelectionLatitudeLongitude();
	}

	public void registerEarthView(EarthView earthView) {
		this.earthView = earthView;
		//System.out.println("EarhView is registered with propertiesView! " + earthView);
	}

	public EarthView getEarthView() {
		return earthView;
	}


	public void updateTrackTreeViewer() {
		
		getEarthView().getDisplay().syncExec(new Runnable() {
			public void run() {
				//System.out.println("test!!!");
				getEarthView().getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().refresh();
			}
		});
	}

	public void updateTimeLine() {
		getEarthView().getDisplay().syncExec(new Runnable() {
			public void run() {
				earthView.getCurrentScenarioEditor().getTimeLine().doRedraw();
			}
		});
	}
	
	public void updateEarthView() {
		getEarthView().getDisplay().syncExec(new Runnable() {
			public void run() {
				earthView.getWWD().redraw();
			}
		});
	}
	
	public ScenarioPropertiesComposite getScenarioComposite() {
		return scenarioComposite;
	}

	public TrackPropertiesComposite getTrackComposite() {
		return trackComposite;
	}

	public WaypointPropertiesComposite getWaypointComposite() {
		return waypointComposite;
	}
}
