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

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import com.jeroenjanssens.presto.model.BezierVertex;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.ais.AISParameter;
import com.jeroenjanssens.presto.sailing.ais.AISState;
import com.jeroenjanssens.presto.tools.DeepCopy;


/**
 * @author Jeroen Janssens
 *
 */


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Sailor {

	public static final double KNOT_TO_MS = 0.514444444;
	public static final int SPEED_INTERPOLATION_LINEAR = 0;
	public static final int SPEED_INTERPOLATION_QUADRATIC = 1;

	private Track track;						//the track we are sailin on

	private long startTime;						//time that the track begins
	private long currentTime;					//the time we are currently when evaluating
	private double stepDistance;				//the segmentDistance we sail in one step
	private int stepTime;						//time to step in milliseconds

	private Waypoint currentWaypoint;			//these values are never null
	private Waypoint nextWaypoint;				
	private BezierVertex currentVertex;
	private BezierVertex nextVertex;
	private LatLon currentVertexPosition;
	private LatLon nextVertexPosition;

	private double trackSailedDistance = 0;		//segmentDistance is in meters
	private double currentSpeed = 0; 			//speed is in knots

	private double segmentSpeedFraction = 0;	//between 0 and 1 indicating the ratio of speed of the current waypoint (=0) and of the next waypoint (=1)
	private double segmentTotalDistance = 0; 	//this needs to be calculated by looping over the vertices. A segment is between two waypoints
	private double segmentDistanceFraction = 0; //fraction of the waypoint fraction (this determines the segmentSpeedFraction)
	private double segmentSailedDistance = 0;	//in meters from one waypoint to the other

	private double vertexTotalDistance = 0;		//same holds for the minisegment between two vertices
	private double vertexSailedDistance = 0;	//in meters from one waypoint to the other

	PositionHeading nullPositionHeading = new PositionHeading(null, 0);
	private PositionHeading oldPositionHeading = nullPositionHeading;		//this one is used when the position and heading are requested, but not available due to recalcuation.

	private AISState currentAISValues;
	
	public Sailor(Track track) {
		this.track = track;
		this.currentAISValues = new AISState();
	}

	private boolean setSegment(int s) throws SailingException {
		currentWaypoint = track.getWaypoint(s);
		if(currentWaypoint == null) throw new SailingException("setSegment(" + s + ") : currentWaypoint == null");
		nextWaypoint = track.getNextWaypoint(currentWaypoint);
		if(nextWaypoint == null) throw new SailingException("setSegment(" + s + ") : nextWaypoint == null");
		resetSegmentParameters();
		return setVertex(0);
	}

	private boolean setNextSegment() throws SailingException {
		return setSegment(currentWaypoint.getId()+1);
	}

	private boolean setVertex(int v) throws SailingException {
		currentVertex = currentWaypoint.getBezierVertex(v);
		if(currentVertex == null) throw new SailingException("setVertex(" + v + ") : currentVertex == null");
		nextVertex = currentWaypoint.getNextBezierVertex(currentVertex);
		if(nextVertex == null) throw new SailingException("setVertex(" + v + ") : nextVertex == null");
		resetVertexParameters();
		return true;
	}

	private boolean setNextVertex() throws SailingException {
		currentVertex = nextVertex;
		if(currentVertex == null) throw new SailingException("currentVertex == null");
		nextVertex = currentWaypoint.getNextBezierVertex(currentVertex);
		if(nextVertex == null) throw new SailingException("nextVertex == null");
		resetVertexParameters();
		return true;
	}

	private void resetSegmentParameters() {
		//currentWaypointPosition = currentWaypoint.getPosition().getLatLon();
		//nextWaypointPosition = nextWaypoint.getPosition().getLatLon();
		segmentTotalDistance = getTotalSegmentDistance(currentWaypoint);
		segmentSailedDistance = 0;
		segmentDistanceFraction = 0;

		currentVertex = currentWaypoint.getFirstBezierVertex();
		nextVertex = currentWaypoint.getNextBezierVertex(currentVertex);
	}

	private void resetVertexParameters() {
		currentVertexPosition = LatLon.fromDegrees(currentVertex.latitude, currentVertex.longitude);
		nextVertexPosition = LatLon.fromDegrees(nextVertex.latitude, nextVertex.longitude);
		vertexTotalDistance = getVertexDistance(currentVertex, nextVertex);
		vertexSailedDistance = 0;
	}

	private void resetAllParameters() {
		startTime = 0;	
		currentTime = 0;				
		stepDistance = 0;				
		stepTime = 1000;		
		currentWaypoint = null;			
		nextWaypoint = null;				
		currentVertex = null;
		nextVertex = null;
		currentVertexPosition = null;
		nextVertexPosition = null;
		trackSailedDistance = 0;			
		currentSpeed = 0; 			
		segmentSpeedFraction = 0;	
		segmentTotalDistance = 0; 	
		segmentDistanceFraction = 0; 
		segmentSailedDistance = 0;	
		vertexTotalDistance = 0;		
		vertexSailedDistance = 0;	
	}

	private double getTotalSegmentDistance(Waypoint waypoint) {	
		return waypoint.getTotalDistance();
	}

	private double getVertexDistance(BezierVertex v1, BezierVertex v2) {
		return v1.distanceToNextVertex;
	}

	private double getSpeedFraction(double f) {
		//Perhaps I can interpolate differently (and let the user choose)
		if((f/=0.5) < 1) return 0.5*f*f;
		return -0.5 * ((--f)*(f-2)-1);
	}

	private void updateFractionsAndSpeed() {
		segmentDistanceFraction = segmentSailedDistance / segmentTotalDistance;
		//update segmentSpeedFraction and currentSpeed (which is in knots)
		segmentSpeedFraction = getSpeedFraction(segmentDistanceFraction);
		currentSpeed = (currentWaypoint.getSpeed() * (1.0d-segmentSpeedFraction)) + (nextWaypoint.getSpeed() * segmentSpeedFraction);
	}

	public void recalculateTimes(TrackUpdateJob trackUpdateJob) {
		this.currentAISValues = new AISState();
		this.currentAISValues.merge(track.getDefaultAISValues());
		//reset all variables;
		log("recalculateTimes()");

		if(track.getWaypoints().size() == 1) {
			log("We cannot set the first segment (probably the track has just one waypoint yet.");
			track.setStatus(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES);
			return;
		}

		resetAllParameters();

		startTime = track.getTime();
		currentTime = startTime;

		try {
			//set the first waypoint as current waypoint, next waypoint
			log("setSegment(0)");
			setSegment(0);
		} catch (SailingException se) {

			if(currentWaypoint != null) {
				log("currentWaypoint != null");
				currentWaypoint.setTime(currentTime);
				currentWaypoint.getVertexLookupTable().clear();
			}

			log("We cannot set the first segment (probably the track has just one waypoint yet.");
			log(se.getMessage());
			log("track.setStatus(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES);");
			track.setStatus(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES);
			return;
		}

		currentWaypoint.setTime(currentTime);
		currentWaypoint.getVertexLookupTable().clear();

		currentVertex.time = currentTime;
		currentVertex.segmentDistance = 0;
	
		currentWaypoint.getVertexLookupTable().put(0, currentVertex);

		while(true) {
			//log("entering while(true)");

			//Check whether the vertices are still clean, if not, exit.
			if(track.getStatus().equals(TrackStatus.DIRTY_VERTICES_AND_DIRTY_TIMES)) {
				log("Sailor: it appears that the track is dirty again, stopping");
				return;
			}

			updateFractionsAndSpeed();

			if(currentSpeed <= 0) {
				System.out.println("We cannot have a negative speed or a speed of zero knots!");
				return;
			}

			//The segmentDistance is the speed * times number of seconds
			stepTime = 1000;
			stepDistance = (currentSpeed * Sailor.KNOT_TO_MS) * ((stepTime) / 1000.0d);
			//log("stepDistance = " + stepDistance);
			double remainingStepDistance = stepDistance;

			//if(remainingStepDistance <= 0) {
				log("remainingStepDistance: " + remainingStepDistance + ", currentSpeed: " + currentSpeed + ", segmentSailedDistance: " + segmentSailedDistance + ", segmentTotalDistance: " + segmentTotalDistance);
			//}

			while(remainingStepDistance > 0) {
				log("entering while(remainingStepDistance > 0)");

				try {

					//Can we make the next step inside the current vertex?
					if(vertexSailedDistance + remainingStepDistance >= vertexTotalDistance) {
						log("we cannot make that step!");
						//no: perform two steps:
						//1: sail remaining vertex segmentDistance
						double actualStepDistance = vertexTotalDistance - vertexSailedDistance; 

						vertexSailedDistance += actualStepDistance; //Should be equal to the vertexTotalDistance 				
						segmentSailedDistance += actualStepDistance;
						trackSailedDistance += actualStepDistance;


						double actualStepTime = (int)(stepTime * (actualStepDistance / stepDistance));
						currentTime += actualStepTime;

						remainingStepDistance = remainingStepDistance - actualStepDistance;

						//2: set next Vertex or Waypoint
						try {
							setNextVertex();
						} catch(SailingException se1) {
							//We cannot set the next Vertex, so we need to move to the next Waypoint
							try {
								setNextSegment();
							} catch(SailingException se2) {
								//We cannot set the next Waypoint, so we have finished with Sailing the Track

								currentWaypoint.setTime(currentTime);
								log("We cannot set the next segment: done!");

								//update the status of the track
								if(track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES)) track.setStatus(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES);
								return;
							}
							//We have set the next Waypoint
							log("Current Waypoint: " + currentWaypoint.getId());
							currentWaypoint.getVertexLookupTable().clear();
							currentWaypoint.setTime(currentTime);
						}
						//We have set the next (or first) vertex
						currentVertex.time = currentTime;
						currentVertex.segmentDistance = segmentSailedDistance;

						int wholeMinuteOfWaypoint = (int) Math.ceil(currentWaypoint.getTime() / 60000.0d);
						int nextwholeMinute = (int) Math.ceil(currentTime / 60000.0d);
						int indexOfBezierVertexInsertion = nextwholeMinute - wholeMinuteOfWaypoint;

						log("Set currentVertex at " + indexOfBezierVertexInsertion);
						currentWaypoint.getVertexLookupTable().put(indexOfBezierVertexInsertion, currentVertex);
						log("Set currentVertex at " + indexOfBezierVertexInsertion + " done!");
					} else {
						log("yes we can make that step!");
						
						//yes: sail the complete remainingStepDistance and continue
						vertexSailedDistance += remainingStepDistance;
						segmentSailedDistance += remainingStepDistance;
						trackSailedDistance += remainingStepDistance;

						int actualStepTime = (int)(stepTime * (remainingStepDistance / stepDistance));
						currentTime += actualStepTime;

						remainingStepDistance = 0;
					} //end if (making step)

				} catch(Exception e) {

					System.out.println("ERROR: " + e.getMessage());
					track.setStatus(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES);
					return;
				}

			} // end while remainStepDistance > 0



		} //end while sailing loop
	}



	public PositionHeading getVesselPositionHeading(long requestedTime) {
		this.currentAISValues = new AISState();
		this.currentAISValues.merge(track.getDefaultAISValues());
		log2("getVesselPositionHeading()");

		//We should only compute a new PositionHeading when the times of the track are clean
		if((track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES)) || (track.getStatus().equals(TrackStatus.DIRTY_VERTICES_AND_DIRTY_TIMES))) {
			log2("The track has dirty times, returning the oldPosition");
			oldPositionHeading.setDirty();
			return oldPositionHeading;
		}

		if(track.getWaypoints().size() == 1) {
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		//reset all variables	
		resetAllParameters();

		//get the necessary times
		startTime = track.getTime();
		currentTime = startTime;

		if(requestedTime < startTime) {
			//the track is later in the timeline than we are viewing at the moment
			log2("the track is later in the timeline than we are viewing at the moment");
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		Waypoint lastWaypoint = track.getLastWaypoint();
		if(lastWaypoint == null) {
			//This is bad, why can't we set the last waypoint
			log2("We cannot set the last waypoint!");
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		log2("last Waypoint: " + lastWaypoint.getId());

		if(requestedTime > lastWaypoint.getTime()) {
			//the track is earlier in the timeline than we are viewing at the moment
			log2("We track is earlier in the timeline than we are viewing at the moment");
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		/*
		if(requestedTime == startTime)  {
			//Because the requestedTime is equal to the starting time of the track, no extra sailing is required 
			log2("Because the requestedTime is equal to the starting time of the track, no extra sailing is required");
			Angle az = LatLon.greatCircleAzimuth(track.getWaypoint(0).getPosition().getLatLon(), LatLon.fromDegrees(track.getWaypoint(0).getBezierVertex(1).latitude, track.getWaypoint(0).getFirstBezierVertex().longitude));
			oldPositionHeading = new PositionHeading(track.getWaypoint(0).getPosition(), az.degrees);
			return oldPositionHeading;
		}
		 */
		//Look up which waypoint to start from
		int waypointIndexToStartFrom = 0;
		while(requestedTime > track.getWaypoint(waypointIndexToStartFrom).getTime()) {
			waypointIndexToStartFrom++;
		}
		waypointIndexToStartFrom--;

		if(waypointIndexToStartFrom < 0) {
			//This is strange: waypointIndexToStartFrom < 0
			log2("This is strange: waypointIndexToStartFrom < 0");
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		try {
			setSegment(waypointIndexToStartFrom);
		} catch(SailingException se) {
			oldPositionHeading = nullPositionHeading;
			return oldPositionHeading;
		}

		log2("currentWaypoint: " + currentWaypoint.getId());
		currentTime = currentWaypoint.getTime();

		//Look in this waypoint the closest whole minute.
		int wholeMinuteOfWaypoint = (int) Math.ceil(currentWaypoint.getTime() / 60000.0d);
		int requestedWholeMinute = (int) Math.floor(requestedTime / 60000.0d);
		int indexOfBezierVertexLookup = requestedWholeMinute - wholeMinuteOfWaypoint;

		if(indexOfBezierVertexLookup >= 0) {
			//This is the place to set the vertex, and the corresponding time and segmentDistance
			currentVertex = currentWaypoint.getVertexLookupTable().get(indexOfBezierVertexLookup);
			while(currentVertex == null) {
				indexOfBezierVertexLookup--;
				currentVertex = currentWaypoint.getVertexLookupTable().get(indexOfBezierVertexLookup);
			}
			nextVertex = currentWaypoint.getNextBezierVertex(currentVertex);
			resetVertexParameters();
			segmentSailedDistance = currentVertex.segmentDistance;
			currentTime = currentVertex.time;
		} else {
			segmentSailedDistance = 0;
			currentTime = currentWaypoint.getTime();
		}		


		double remainingStepDistance;
		while(true) {

			updateFractionsAndSpeed();

			if(currentSpeed <= 0) {
				log2("We cannot have a negative speed or a speed of zero knots!");
				oldPositionHeading = nullPositionHeading;
				return oldPositionHeading;
			}

			//make a check wether we can make this step taking the requestedTime into account
			if(currentTime + stepTime > requestedTime) {
				stepTime = (int) (requestedTime - currentTime);
			}

			//The segmentDistance is the speed * times number of seconds
			stepDistance = (currentSpeed * Sailor.KNOT_TO_MS) * ((stepTime) / 1000.0d);
			remainingStepDistance = stepDistance;

			while(remainingStepDistance > 0) {

				//Can we make the next step inside the current vertex?
				if(vertexSailedDistance + remainingStepDistance >= vertexTotalDistance) {
					//no: perform two steps:
					//1: sail remaining vertex segmentDistance
					double actualStepDistance = vertexTotalDistance - vertexSailedDistance; 

					vertexSailedDistance += actualStepDistance; //Should be equal to the vertexTotalDistance 				
					segmentSailedDistance += actualStepDistance;
					trackSailedDistance += actualStepDistance;

					double actualStepTime = (int)(stepTime * (actualStepDistance / stepDistance));
					currentTime += actualStepTime;

					remainingStepDistance = remainingStepDistance - actualStepDistance;

					//2: set next Vertex or Waypoint
					try {
						setNextVertex();
					} catch(SailingException se1) {
						//We cannot set the next Vertex, so we need to move to the next Waypoint
						//This is quite weird, because we first looked up from which waypoint to sail
						log2("We cannot set the next vertex: weird!");
						try {
							setNextSegment();
						} catch(SailingException se2) {
							//We cannot set the next Waypoint, so we have finished with Sailing the Track
							//But have we reached the requestedTime?
							log2("We cannot set the next segment: even weirder!");
							oldPositionHeading = nullPositionHeading;
							return oldPositionHeading;
						}
						//We have set the next Waypoint
					}
					//We have set the next (or first) vertex
				} else {
					//yes: sail the complete remainingStepDistance and continue
					vertexSailedDistance += remainingStepDistance;
					segmentSailedDistance += remainingStepDistance;
					trackSailedDistance += remainingStepDistance;

					double actualStepTime = (int)(stepTime * (remainingStepDistance / stepDistance));
					currentTime += actualStepTime;

					remainingStepDistance = 0;
				} //end if (making step)
			} // end while remainStepDistance > 0

			if(currentTime >= requestedTime) {
				log2("we have sailed yes!");
				Angle az = LatLon.greatCircleAzimuth(currentVertexPosition, nextVertexPosition);
				LatLon ll = LatLon.interpolate(vertexSailedDistance / vertexTotalDistance, currentVertexPosition, nextVertexPosition);
				Position p = Position.fromDegrees(ll.getLatitude().degrees + (Double)currentAISValues.getSafe(AISParameter.LATITUDE), ll.getLongitude().degrees + (Double)currentAISValues.getSafe(AISParameter.LONGITUDE), 100);
				PositionHeading newPositionHeading = new PositionHeading(p, az.degrees + (Double)currentAISValues.getSafe(AISParameter.COURSE_OVER_GROUND));
				oldPositionHeading = newPositionHeading;
				return newPositionHeading;
			}
		} //end while sailing loop
	}

	private void log(String text) {
		//System.out.println("recalculate_times: " + text);
	}

	private void log2(String text) {
		//System.out.println("New Sailor: " + text);
	}

	public ArrayList<AISState> sail(IProgressMonitor monitor) {
		this.currentAISValues = new AISState();
		this.currentAISValues.merge(track.getDefaultAISValues());
		
		
		ArrayList<AISState> sentAISMessages = new ArrayList<AISState>();
		
		
		//reset all variables;
		log("recalculateTimes()");

		if(track.getWaypoints().size() == 1) {
			log("We cannot set the first segment (probably the track has just one waypoint yet.");
			return null;
		}

		resetAllParameters();

		startTime = track.getTime();
		currentTime = startTime;

		try {
			//set the first waypoint as current waypoint, next waypoint
			log("setSegment(0)");
			setSegment(0);
		} catch (SailingException se) {

			log("We cannot set the first segment (probably the track has just one waypoint yet.");
			log(se.getMessage());

			return null;
		}

		while(true) {
			if(monitor.isCanceled()) return null;
			//log("entering while(true)");

			//Check whether the vertices are still clean, if not, exit.
			if(track.getStatus().equals(TrackStatus.DIRTY_VERTICES_AND_DIRTY_TIMES)) {
				log("Sailor: it appears that the track is dirty again, stopping");
				return null;
			}

			updateFractionsAndSpeed();

			if(currentSpeed <= 0) {
				System.out.println("We cannot have a negative speed or a speed of zero knots!");
				return null;
			}

			//The segmentDistance is the speed * times number of seconds
			stepTime = 1000;
			stepDistance = (currentSpeed * Sailor.KNOT_TO_MS) * ((stepTime) / 1000.0d);
			//log("stepDistance = " + stepDistance);
			double remainingStepDistance = stepDistance;

			while(remainingStepDistance > 0) {
				log("entering while(remainingStepDistance > 0)");

				try {

					//Can we make the next step inside the current vertex?
					if(vertexSailedDistance + remainingStepDistance >= vertexTotalDistance) {
						log("we cannot make that step!");
						//no: perform two steps:
						//1: sail remaining vertex segmentDistance
						double actualStepDistance = vertexTotalDistance - vertexSailedDistance; 

						vertexSailedDistance += actualStepDistance; //Should be equal to the vertexTotalDistance 				
						segmentSailedDistance += actualStepDistance;
						trackSailedDistance += actualStepDistance;


						double actualStepTime = (int)(stepTime * (actualStepDistance / stepDistance));
						currentTime += actualStepTime;

						remainingStepDistance = remainingStepDistance - actualStepDistance;

						//2: set next Vertex or Waypoint
						try {
							setNextVertex();
						} catch(SailingException se1) {
							//We cannot set the next Vertex, so we need to move to the next Waypoint
							try {
								setNextSegment();
							} catch(SailingException se2) {
								//We cannot set the next Waypoint, so we have finished with Sailing the Track

								//currentWaypoint.setTime(currentTime);
								log("We cannot set the next segment: done!");

								//update the status of the track
								
								return sentAISMessages;
							}
							//We have set the next Waypoint
							log("Current Waypoint: " + currentWaypoint.getId());
							//currentWaypoint.getVertexLookupTable().clear();
							//currentWaypoint.setTime(currentTime);
						}
						//We have set the next (or first) vertex
						//currentVertex.time = currentTime;
						//currentVertex.segmentDistance = segmentSailedDistance;
						//currentVertex.coloring = currentSpeed;

						//int wholeMinuteOfWaypoint = (int) Math.ceil(currentWaypoint.getTime() / 60000.0d);
						//int nextwholeMinute = (int) Math.ceil(currentTime / 60000.0d);
						//int indexOfBezierVertexInsertion = nextwholeMinute - wholeMinuteOfWaypoint;

						//log("Set currentVertex at " + indexOfBezierVertexInsertion);
						//currentWaypoint.getVertexLookupTable().put(indexOfBezierVertexInsertion, currentVertex);
						//log("Set currentVertex at " + indexOfBezierVertexInsertion + " done!");
					} else {
						log("yes we can make that step!");
						
						//yes: sail the complete remainingStepDistance and continue
						vertexSailedDistance += remainingStepDistance;
						segmentSailedDistance += remainingStepDistance;
						trackSailedDistance += remainingStepDistance;

						int actualStepTime = (int)(stepTime * (remainingStepDistance / stepDistance));
						currentTime += actualStepTime;

						remainingStepDistance = 0;
					} //end if (making step)

				} catch(Exception e) {

					System.out.println("ERROR: " + e.getMessage());
					
					
					return null;
				}

			} // end while remainStepDistance > 0
			
			//construct the message and add it to the list
			Angle az = LatLon.greatCircleAzimuth(currentVertexPosition, nextVertexPosition);
			Position p = new Position(LatLon.interpolate(vertexSailedDistance / vertexTotalDistance, currentVertexPosition, nextVertexPosition), 100);
			
			sentAISMessages.add(getCurrentState(p.getLatitude().degrees, p.getLongitude().degrees, az.degrees, currentSpeed));
			
			//compute rate of turn
			if(sentAISMessages.size() > 9) {
				double rot = Math.abs((Double)sentAISMessages.get(sentAISMessages.size()-1).getSafe(AISParameter.COURSE_OVER_GROUND) - (Double)sentAISMessages.get(sentAISMessages.size()-10).getSafe(AISParameter.COURSE_OVER_GROUND));
				sentAISMessages.get(sentAISMessages.size()-1).put(AISParameter.RATE_OF_TURN, rot + (Double)sentAISMessages.get(sentAISMessages.size()-1).getSafe(AISParameter.RATE_OF_TURN));
			}
			
			monitor.worked(1);
		} //end while sailing loop
	}
	
	private AISState getCurrentState(double latitude, double longitude, double heading, double speed) {
		AISState currentAISState = (AISState) DeepCopy.copy(currentAISValues);
		
		currentAISState.setTime(currentTime);
		currentAISState.put(AISParameter.LATITUDE, Double.valueOf(latitude) + (Double)currentAISState.getSafe(AISParameter.LATITUDE));
		currentAISState.put(AISParameter.LONGITUDE, Double.valueOf(longitude) + (Double)currentAISState.getSafe(AISParameter.LONGITUDE));
		currentAISState.put(AISParameter.SPEED_OVER_GROUND, Double.valueOf(speed) + (Double)currentAISState.getSafe(AISParameter.SPEED_OVER_GROUND));
		currentAISState.put(AISParameter.COURSE_OVER_GROUND, Double.valueOf(heading) + (Double)currentAISState.getSafe(AISParameter.COURSE_OVER_GROUND));
		
		return currentAISState;
	}

}
