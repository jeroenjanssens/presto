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

import gov.nasa.worldwind.Locatable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Waypoint implements Locatable, Selectable {

	private Track track;
	private double latitude;
	private double longitude;
	private long time;
	
	private double speed = 30;
	private double angle = 0;

	private Point screenPoint;
	private CopyOnWriteArrayList<BezierVertex> vertices;
	private ArrayList<Double> distanceMarkers = new ArrayList<Double>(); //the index is the time in whole minutes from the waypoint start
	//so if the waypoint has timestamp 100220284405 then the first marker is at 100220220000 something (don't really know but that doesn't matter)
	//the value is the segmentDistance in meters from this waypoitn
	
	//private ArrayList<BezierVertex> vertexLookupTable = new ArrayList<BezierVertex>(); //the index is the time in whole minutes from the waypoint start
	//the value represents the index in the vertices ArrayList
	
	private HashMap<Integer, BezierVertex> vertexLookupTable = new HashMap<Integer, BezierVertex>(); //the index is the time in whole minutes from the waypoint start
	private double totalDistance = 0;

	public Waypoint() {
		
	}

	
	public Waypoint(Track track, double latitude, double longitude, double speed, double angle) {
		this.track = track;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.angle = angle;
		
		this.vertices = new CopyOnWriteArrayList<BezierVertex>();
		this.distanceMarkers = new ArrayList<Double>();
	}
	
	
	public Waypoint(Track track, Position position) {
		this.track = track;
		this.latitude = position.getLatitude().degrees;
		this.longitude = position.getLongitude().degrees;
		this.vertices = new CopyOnWriteArrayList<BezierVertex>();
		this.distanceMarkers = new ArrayList<Double>();
		//this.speed = 20 + (20 * Math.random());
		this.speed = 20;
		this.angle = 0;
	}
	
	public void updateWaypoint(Track track, Position position) {
		this.track = track;
		this.latitude = position.getLatitude().degrees;
		this.longitude = position.getLongitude().degrees;
		this.vertices = new CopyOnWriteArrayList<BezierVertex>();
		//this.distanceMarkers = new ArrayList<Double>();
		//this.speed = 20 + (20 * Math.random());
		this.speed = 20;
	}
		
	public Track getTrack() {
		return this.track;
	}
	
	public int getId() {
		return track.getId(this); 
	}
	
	public void setPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setPosition(Position position) {
		this.latitude = position.getLatitude().degrees;
		this.longitude = position.getLongitude().degrees;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public Position getPosition() {
		return Position.fromDegrees(latitude, longitude, 100);
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public double getLongitude() {
		return this.longitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public CopyOnWriteArrayList<BezierVertex> getVertices() {
		return vertices;
	}
	
	public void setVertices(CopyOnWriteArrayList<BezierVertex> vertices) {
		this.vertices = vertices;
	}
	
	public boolean isLast() {
		if(track == null) return false;
		return track.getWaypoint(track.getWaypoints().size()-1).equals(this);
	}
	
	public boolean isFirst() {
		if(track == null) return false;
		return track.getWaypoint(0).equals(this);
	}
	
	public String toXML(int indent) {

		String tabs = "";
		for(int t = 0; t < indent; t++) tabs += "\t";
		String xml = tabs + "<waypoint id='" + this.getId() + "' latitude='" + latitude + "' longitude='" + longitude + "' speed='" + speed + "' angle='" + angle + "' />\n";
		return xml;
	}

	public void moveBy(Angle diffAzimuth, Angle diffDistance) {
		Position currentPosition = this.getPosition();
		Position newPosition = new Position(LatLon.greatCircleEndPosition(currentPosition.getLatLon(), diffAzimuth, diffDistance), 100);
		setPosition(newPosition);
	}

	public Point getScreenPoint() {
		return screenPoint;
	}

	public void setScreenPoint(Point screenPoint) {
		this.screenPoint = screenPoint;
	}

	public void setTrack(Track track) {
		this.track = track;
	}
	
	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setDistanceMarkers(ArrayList<Double> distanceMarkers) {
		this.distanceMarkers = distanceMarkers;
	}

	public ArrayList<Double> getDistanceMarkers() {
		return distanceMarkers;
	}
	
	public BezierVertex getNextBezierVertex(BezierVertex bv) {
		int index = vertices.indexOf(bv);
		if(index < vertices.size()-1) {
			return vertices.get(index+1);
		} else {
			return null;
		}
	}

	public BezierVertex getFirstBezierVertex() {
		if(vertices.size() > 0) {
			return vertices.get(0);
		}
		return null;
	}

	public BezierVertex getBezierVertex(int v) {
		if(vertices.size() > v) {
			return vertices.get(v);
		}
		return null;
	}
	/*
	public ArrayList<BezierVertex> getVertexLookupTable() {
		return vertexLookupTable;
	}
	*/
	public HashMap<Integer, BezierVertex> getVertexLookupTable() {
		return vertexLookupTable;
	}
	
	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
}
