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

package com.jeroenjanssens.presto.model;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;

import com.jeroenjanssens.presto.model.actions.IAction;
import com.jeroenjanssens.presto.sailing.TrackStatus;
import com.jeroenjanssens.presto.sailing.TrackTools;
import com.jeroenjanssens.presto.sailing.TrackUpdateJob;
import com.jeroenjanssens.presto.sailing.ais.AISState;
import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.earth.renderables.Vessel;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Track extends TreeModel implements Selectable, Comparable<Track> {

	private CopyOnWriteArrayList<Waypoint> waypoints;
	private ArrayList<IAction> actions;
	private long time;
	private Vessel vessel;
	private AISState defaultAISValues;
	
	private TrackUpdateJob trackUpdateJob = null;
	private Track thisTrack = this;
	
	private TrackStatus status = TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES;
	private EarthView earthView; 

	private Waypoint pivotWaypoint = null;
	private long pivotTime;
	
	public Track(String name) {
		super(name);
		waypoints = new CopyOnWriteArrayList<Waypoint>();
		actions = new ArrayList<IAction>();
		time = 1209970872203L;
		vessel = new Vessel(this);
		defaultAISValues = new AISState();
		trackUpdateJob = new TrackUpdateJob(this);
		trackUpdateJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if(thisTrack.getStatus() == TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES) {
					thisTrack.trackUpdateJob.schedule();
				} else {
					thisTrack.getVessel().update();
					thisTrack.updateEarthViewAndTime();
				}
			}
		});
	}

	public int getId(Waypoint waypoint) {
		return waypoints.indexOf(waypoint);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public void addAction(IAction action) {
		actions.add(action);
	}

	public void addWaypoint(Waypoint waypoint) {
		waypoints.add(waypoint);
	}

	public void addWaypoint(int index, Waypoint waypoint) {
		waypoints.add(index, waypoint);
	}

	public void removeWaypoint(Waypoint waypoint) {
		waypoints.remove(waypoint);
		if(waypoints.isEmpty()) {
			final Track thisTrack = this;
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					thisTrack.getParent().remove(thisTrack);
				}
			});
		} 
	}

	public CopyOnWriteArrayList<Waypoint> getWaypoints() {
		return this.waypoints;
	}

	public Waypoint getWaypoint(int id) {
		if (this.waypoints.size() < (id+1)) return null;
		return this.waypoints.get(id);
	}

	public Waypoint getPreviousWaypoint(Waypoint waypoint) {
		int index = waypoints.indexOf(waypoint);
		if(index > 0) {
			return waypoints.get(index-1);
		} else {
			return null;
		}
	}

	public Waypoint getNextWaypoint(Waypoint waypoint) {
		int index = waypoints.indexOf(waypoint);
		if(index < waypoints.size()-1) {
			return waypoints.get(index+1);
		} else {
			return null;
		}
	}
	
	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitTrack(this, passAlongArgument);
	}

	public String toXML(int indent) {

		String tabs = "";
		for(int t = 0; t < indent; t++) tabs += "\t";
		String xml = tabs + "<track name='" + name + "' color='" + TrackTools.colorToHex(getColor()) + "' visible='" + isVisible() + "' locked='" + isLocked() + "' time='" + this.getTime() + "'>\n";

		xml += this.defaultAISValues.toXML(indent+1);
		
		xml += tabs + "\t<waypoints>\n";
		for(Waypoint w : waypoints) {
			xml += w.toXML(indent+2);
		}
		xml += tabs + "\t</waypoints>\n";


		xml += tabs + "\t<actions>\n";
		for(IAction a : actions) {
			xml += a.toXML(indent+2);
		}
		xml += tabs + "\t</actions>\n";

		xml += tabs + "</track>\n";
		return xml;
	}

	public void setVessel(Vessel vessel) {
		this.vessel = vessel;
	}

	public Vessel getVessel() {
		return vessel;
	}

	public Waypoint getLastWaypoint() {
		if(this.waypoints.size() == 0) return null;
		return this.waypoints.get(this.waypoints.size()-1);
	}

	public void update(EarthView earthView) {
		this.earthView = earthView;
		this.getVessel().update();
		updateEarthViewAndTime();
		TrackTools.recalculateVertices(this);
		//System.out.println("Track: sceduling new UpdateJob");
		trackUpdateJob.schedule();
	}

	public void setDefaultAISValues(AISState defaultAISValues) {
		this.defaultAISValues = defaultAISValues;
	}

	public AISState getDefaultAISValues() {
		return defaultAISValues;
	}

	public int compareTo(Track otherTrack) {
		return this.getName().compareTo(otherTrack.getName());
	}

	public TrackStatus getStatus() {
		return this.status;
	}
	
	public void setStatus(TrackStatus status) {
		this.status = status;
	}
	
	public void updateEarthViewAndTime() {
		if(earthView != null) {
			earthView.getWWD().redraw();
			earthView.getCurrentScenarioEditor().getTimeLine().doRedraw();
		}
	}

	public void setPivotWaypoint(Waypoint pivotWaypoint) {
		this.pivotWaypoint = pivotWaypoint;
	}

	public Waypoint getPivotWaypoint() {
		return pivotWaypoint;
	}

	public void setPivotTime(long pivotTime) {
		this.pivotTime = pivotTime;
	}

	public long getPivotTime() {
		return pivotTime;
	}
}
