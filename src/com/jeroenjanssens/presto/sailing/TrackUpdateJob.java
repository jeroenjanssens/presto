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

package com.jeroenjanssens.presto.sailing;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.jeroenjanssens.presto.model.Track;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackUpdateJob extends Job {

	private Track track;

	public TrackUpdateJob(final Track track) {
		super("Update " + track.getName());
		this.track = track;
		this.setPriority(Job.SHORT);
		this.setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if(track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES)) {
			//System.out.println("TrackUpdateJob start times");
			
			track.getVessel().getSailor().recalculateTimes(this);
			
			/*
			if(track.getPivotWaypoint() == null) {
				System.out.println("TrackUpdateJob start times normal");
				track.getVessel().getSailor().recalculateTimes(this);
			} else {	
				System.out.println("TrackUpdateJob start times with pivot 1");
				
				
				long oldPivotTime = track.getPivotTime();
				track.getVessel().getSailor().recalculateTimes(this);
				long newPivotTime = track.getPivotWaypoint().getTime();
				long difference = newPivotTime - oldPivotTime;
				long oldTrackTime = track.getTime();
				long newTrackTime = oldTrackTime + difference;
				
				
				
				track.setTime(newTrackTime);
				track.setStatus(TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES);
				System.out.println("TrackUpdateJob start times with pivot 1");
				
				track.getVessel().getSailor().recalculateTimes(this);
				
				
				track.setPivotTime(track.getPivotWaypoint().getTime());
			}
			*/
			//System.out.println("TrackUpdateJob finished times");
		}
		return Status.OK_STATUS;
	}
	
}
