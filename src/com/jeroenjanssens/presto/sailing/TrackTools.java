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
import gov.nasa.worldwind.globes.Earth;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.jeroenjanssens.presto.model.BezierVertex;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackTools {

	public static double KNOT_TO_MS = 0.514444444;

	public static synchronized ArrayList<BezierVertex> recalculateVertices(Waypoint waypoint, Position endPosition) {

		//waypoint.getVertices().clear();

		ArrayList<BezierVertex> vertices = new ArrayList<BezierVertex>();

		//Waypoint nextWaypoint = waypoint.getTrack().getNextWaypoint(waypoint);

		//the begin and end control points
		Position posA = waypoint.getPosition();
		Position posD = endPosition;

		//Determine the second and third control point
		Angle segmentAzimuth = null;
		Angle segmentDistance = LatLon.greatCircleDistance(posA.getLatLon(), posD.getLatLon());
		LatLon posB, posC = null;
		if(waypoint.isFirst()) {
			//posB is heel klein en in het verlengde van de hemelsbrede lijn	
			segmentAzimuth = LatLon.greatCircleAzimuth(posA.getLatLon(), posD.getLatLon());
			segmentDistance = LatLon.greatCircleDistance(posA.getLatLon(), posD.getLatLon());
			posB = LatLon.greatCircleEndPosition(posA.getLatLon(), segmentAzimuth, Angle.fromDegrees(0.000001));
		} else {
			//posB haaks enzo
			Waypoint previousWaypoint = waypoint.getTrack().getPreviousWaypoint(waypoint);

			Angle aCwNw = LatLon.greatCircleAzimuth(posA.getLatLon(), posD.getLatLon());
			Angle aCwPw = LatLon.greatCircleAzimuth(posA.getLatLon(), previousWaypoint.getPosition().getLatLon());
			Angle sum = aCwNw.subtract(aCwPw);
			Angle handle;

			double d = sum.degrees;
			if(d < -180) {
				//System.out.println("(1) d < -180");
				handle = aCwNw.addDegrees((Math.abs(d)-180)/2);
			} else if(d < 0) {
				//System.out.println("(2) -180 > d < 0");
				handle = aCwNw.subtractDegrees((180-Math.abs(d))/2);
				//handle = aCwNw.subtractDegrees(((360-d)-180)/2);
			} else if(d < 180) {
				//System.out.println("(3) 0 > d < 180");
				handle = aCwNw.addDegrees(((360-d)-180)/2);
			} else {
				//System.out.println("(4) d >= 180");
				handle = aCwNw.subtractDegrees((d-180)/2);
			}

			//System.out.println("The corner is: " + d);

			posB = LatLon.greatCircleEndPosition(posA.getLatLon(), handle, Angle.fromDegrees(waypoint.getAngle()+0.000001));
		}

		//posC is heel klein en in het verlengde van de hemelsbrede lijn
		segmentAzimuth = LatLon.greatCircleAzimuth(posD.getLatLon(), posA.getLatLon());
		segmentDistance = LatLon.greatCircleDistance(posD.getLatLon(), posA.getLatLon());
		posC = LatLon.greatCircleEndPosition(posD.getLatLon(), segmentAzimuth, Angle.fromDegrees(0.001));


		BezierVertex vecA = geodeticToCartesian(posA.getLatLon(), 200);
		BezierVertex vecB = geodeticToCartesian(posB, 200);
		BezierVertex vecC = geodeticToCartesian(posC, 200);
		BezierVertex vecD = geodeticToCartesian(posD.getLatLon(), 200);

		double x, y, z;

		double k = segmentDistance.degrees / 0.005;
		if (k > 200) k = 200;
		if (k < 20) k = 20;
		k = 1 / k;

		for(double t=0;t<1;t+=k){
			x=(vecA.x+t*(-vecA.x*3+t*(3*vecA.x-vecA.x*t)))+t*(3*vecB.x+t*(-6*vecB.x+vecB.x*3*t))+t*t*(vecC.x*3-vecC.x*3*t)+vecD.x*t*t*t;
			y=(vecA.y+t*(-vecA.y*3+t*(3*vecA.y-vecA.y*t)))+t*(3*vecB.y+t*(-6*vecB.y+vecB.y*3*t))+t*t*(vecC.y*3-vecC.y*3*t)+vecD.y*t*t*t;
			z=(vecA.z+t*(-vecA.z*3+t*(3*vecA.z-vecA.z*t)))+t*(3*vecB.z+t*(-6*vecB.z+vecB.z*3*t))+t*t*(vecC.z*3-vecC.z*3*t)+vecD.z*t*t*t;
			vertices.add(cartesianToGeodetic(x, y, z));
		}
		
		return vertices;
	}


	public static void recalculateVertices(Track track) {
		//kan nog wat sneller door meteen latlon te nemen van PosA en PosD
		track.setStatus(TrackStatus.DIRTY_VERTICES_AND_DIRTY_TIMES);
		double t;
		
		for(Waypoint waypoint : track.getWaypoints()) {
			waypoint.getVertices().clear();
			if(waypoint.isLast()) {
				waypoint.getVertices().add(geodeticToCartesian(waypoint.getPosition().getLatLon(), 100));
				waypoint.setTotalDistance(0);
			} else {
				Waypoint nextWaypoint = waypoint.getTrack().getNextWaypoint(waypoint);

				//the begin and end control points
				Position posA = waypoint.getPosition();
				Position posD = nextWaypoint.getPosition();

				//Determine the second and third control point
				Angle segmentAzimuth = null;
				Angle segmentDistance = LatLon.greatCircleDistance(posA.getLatLon(), posD.getLatLon());
				LatLon posB, posC = null;
				if(waypoint.isFirst()) {
					//posB is heel klein en in het verlengde van de hemelsbrede lijn	
					segmentAzimuth = LatLon.greatCircleAzimuth(posA.getLatLon(), posD.getLatLon());
					segmentDistance = LatLon.greatCircleDistance(posA.getLatLon(), posD.getLatLon());
					posB = LatLon.greatCircleEndPosition(posA.getLatLon(), segmentAzimuth, Angle.fromDegrees(0.000001));
				} else {
					//posB haaks enzo
					Waypoint previousWaypoint = waypoint.getTrack().getPreviousWaypoint(waypoint);

					Angle aCwNw = LatLon.greatCircleAzimuth(posA.getLatLon(), posD.getLatLon());
					Angle aCwPw = LatLon.greatCircleAzimuth(posA.getLatLon(), previousWaypoint.getPosition().getLatLon());
					Angle sum = aCwNw.subtract(aCwPw);
					Angle handle;

					double d = sum.degrees;
					if(d < -180) {
						//System.out.println("(1) d < -180");
						handle = aCwNw.addDegrees((Math.abs(d)-180)/2);
					} else if(d < 0) {
						//System.out.println("(2) -180 > d < 0");
						handle = aCwNw.subtractDegrees((180-Math.abs(d))/2);
						//handle = aCwNw.subtractDegrees(((360-d)-180)/2);
					} else if(d < 180) {
						//System.out.println("(3) 0 > d < 180");
						handle = aCwNw.addDegrees(((360-d)-180)/2);
					} else {
						//System.out.println("(4) d >= 180");
						handle = aCwNw.subtractDegrees((d-180)/2);
					}

					//System.out.println("The corner is: " + d);

					posB = LatLon.greatCircleEndPosition(posA.getLatLon(), handle, Angle.fromDegrees(waypoint.getAngle()+0.000001));
				}

				if(nextWaypoint.isLast()) {
					//posC is heel klein en in het verlengde van de hemelsbrede lijn
					segmentAzimuth = LatLon.greatCircleAzimuth(posD.getLatLon(), posA.getLatLon());
					segmentDistance = LatLon.greatCircleDistance(posD.getLatLon(), posA.getLatLon());
					posC = LatLon.greatCircleEndPosition(posD.getLatLon(), segmentAzimuth, Angle.fromDegrees(0.001));
				} else {
					//posC haaks enzo 

					Waypoint secondNextWaypoint = nextWaypoint.getTrack().getNextWaypoint(nextWaypoint);

					Angle aNwSw = LatLon.greatCircleAzimuth(posD.getLatLon(), secondNextWaypoint.getPosition().getLatLon());
					Angle aNwCw = LatLon.greatCircleAzimuth(posD.getLatLon(), posA.getLatLon());
					Angle sum = aNwCw.subtract(aNwSw);
					Angle handle;

					double d = sum.degrees;
					if(d < -180) {
						//System.out.println("(1) d < -180");
						handle = aNwCw.addDegrees((Math.abs(d)-180)/2);
					} else if(d < 0) {
						//System.out.println("(2) -180 > d < 0");
						handle = aNwCw.subtractDegrees((180-Math.abs(d))/2);
					} else if(d < 180) {
						//System.out.println("(3) 0 > d < 180");
						handle = aNwCw.addDegrees(((360-d)-180)/2);
					} else {
						//System.out.println("(4) d >= 180");
						handle = aNwCw.subtractDegrees((d-180)/2);
					}

					//System.out.println("The corner is: " + d);

					posC = LatLon.greatCircleEndPosition(posD.getLatLon(), handle, Angle.fromDegrees(nextWaypoint.getAngle()+0.001));
				}

				BezierVertex vecA = geodeticToCartesian(posA.getLatLon(), 100);
				BezierVertex vecB = geodeticToCartesian(posB, 100);
				BezierVertex vecC = geodeticToCartesian(posC, 100);
				BezierVertex vecD = geodeticToCartesian(posD.getLatLon(), 100);

				double x, y, z;

				double k = segmentDistance.degrees / 0.005;
				if (k > 200) k = 200;
				if (k < 20) k = 20;
				k = 1 / k;

				waypoint.getVertices().add(geodeticToCartesian(waypoint.getPosition().getLatLon(), 100));
				
				for(t=0;t<=1;t+=k){
					x=(vecA.x+t*(-vecA.x*3+t*(3*vecA.x-vecA.x*t)))+t*(3*vecB.x+t*(-6*vecB.x+vecB.x*3*t))+t*t*(vecC.x*3-vecC.x*3*t)+vecD.x*t*t*t;
					y=(vecA.y+t*(-vecA.y*3+t*(3*vecA.y-vecA.y*t)))+t*(3*vecB.y+t*(-6*vecB.y+vecB.y*3*t))+t*t*(vecC.y*3-vecC.y*3*t)+vecD.y*t*t*t;
					z=(vecA.z+t*(-vecA.z*3+t*(3*vecA.z-vecA.z*t)))+t*(3*vecB.z+t*(-6*vecB.z+vecB.z*3*t))+t*t*(vecC.z*3-vecC.z*3*t)+vecD.z*t*t*t;
					waypoint.getVertices().add(cartesianToGeodetic(x, y, z));
				}
				
				waypoint.getVertices().add(geodeticToCartesian(nextWaypoint.getPosition().getLatLon(), 100));
				
				CopyOnWriteArrayList<BezierVertex> vertices = waypoint.getVertices();
				double totalDistance = 0;
				for(int i = 0; i < vertices.size()-1; i++) {
					LatLon l1 = LatLon.fromDegrees(vertices.get(i).latitude, vertices.get(i).longitude);
					LatLon l2 = LatLon.fromDegrees(vertices.get(i+1).latitude, vertices.get(i+1).longitude);
					double totalVertexDistance = LatLon.ellipsoidalDistance(l1, l2, Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS);
					if(Double.isNaN(totalVertexDistance)) totalVertexDistance = 0;
					vertices.get(i).distanceToNextVertex = totalVertexDistance;
					totalDistance += totalVertexDistance;
				}
				waypoint.setTotalDistance(totalDistance);
			}
			
			
			
		}
			
		track.setStatus(TrackStatus.CLEAN_VERTICES_AND_DIRTY_TIMES);
	}

	private static BezierVertex cartesianToGeodetic(double myx, double myy, double myz) {
		double ra2 = 1 / (Earth.WGS84_EQUATORIAL_RADIUS * Earth.WGS84_EQUATORIAL_RADIUS);

		double X = myz;
		double Y = myx;
		double Z = myy;

		double e2 = Earth.WGS84_ES;
		double e4 = e2 * e2;

		double XXpYY = X * X + Y * Y;
		double sqrtXXpYY = Math.sqrt(XXpYY);
		double p = XXpYY * ra2;
		double q = Z * Z * (1 - e2) * ra2;
		double r = 1 / 6.0 * (p + q - e4);
		double s = e4 * p * q / (4 * r * r * r);
		double t = Math.pow(1 + s + Math.sqrt(s * (2 + s)), 1 / 3.0);
		double u = r * (1 + t + 1 / t);
		double v = Math.sqrt(u * u + e4 * q);
		double w = e2 * (u + v - q) / (2 * v);
		double k = Math.sqrt(u + v + w * w) - w;
		double D = k * sqrtXXpYY / (k + e2);
		double lon = 2 * Math.atan2(Y, X + sqrtXXpYY);
		double sqrtDDpZZ = Math.sqrt(D * D + Z * Z);
		double lat = 2 * Math.atan2(Z, D + sqrtDDpZZ);
		//double elevation = (k + e2 - 1) * sqrtDDpZZ / k;

		return geodeticToCartesian(LatLon.fromRadians(lat, lon), 100);
	}

	private static LatLon bezierVertexToLatLon(BezierVertex vertex) {
		double ra2 = 1 / (Earth.WGS84_EQUATORIAL_RADIUS * Earth.WGS84_EQUATORIAL_RADIUS);

		double X = vertex.z;
		double Y = vertex.x;
		double Z = vertex.y;

		double e2 = Earth.WGS84_ES;
		double e4 = e2 * e2;

		double XXpYY = X * X + Y * Y;
		double sqrtXXpYY = Math.sqrt(XXpYY);
		double p = XXpYY * ra2;
		double q = Z * Z * (1 - e2) * ra2;
		double r = 1 / 6.0 * (p + q - e4);
		double s = e4 * p * q / (4 * r * r * r);
		double t = Math.pow(1 + s + Math.sqrt(s * (2 + s)), 1 / 3.0);
		double u = r * (1 + t + 1 / t);
		double v = Math.sqrt(u * u + e4 * q);
		double w = e2 * (u + v - q) / (2 * v);
		double k = Math.sqrt(u + v + w * w) - w;
		double D = k * sqrtXXpYY / (k + e2);
		double lon = 2 * Math.atan2(Y, X + sqrtXXpYY);
		double sqrtDDpZZ = Math.sqrt(D * D + Z * Z);
		double lat = 2 * Math.atan2(Z, D + sqrtDDpZZ);
		//double elevation = (k + e2 - 1) * sqrtDDpZZ / k;

		return LatLon.fromRadians(lat, lon);
	}
	
	
	public static BezierVertex geodeticToCartesian(LatLon latLon, double metersElevation) {

		double cosLat = Math.cos(latLon.getLatitude().radians);
		double sinLat = Math.sin(latLon.getLatitude().radians);
		double cosLon = Math.cos(latLon.getLongitude().radians);
		double sinLon = Math.sin(latLon.getLongitude().radians);

		double rpm = Earth.WGS84_EQUATORIAL_RADIUS / Math.sqrt(1.0 - Earth.WGS84_ES * sinLat * sinLat);

		double x = (rpm + metersElevation) * cosLat * sinLon;
		double y = (rpm * (1.0 - Earth.WGS84_ES) + metersElevation) * sinLat;
		double z = (rpm + metersElevation) * cosLat * cosLon;

		BezierVertex v = new BezierVertex(x, y, z);
		v.latitude = latLon.getLatitude().degrees;
		v.longitude = latLon.getLongitude().degrees;

		return v; 
	}

	public static Position getInsertPosition(Track track, Position p, boolean insert) {
		if(track == null) return null;
		if(p == null) return null;
		
		
		BezierVertex vi = geodeticToCartesian(p.getLatLon(), 100);
		double bestDifference = 1000000;
		BezierVertex bestBezierVertex1 = null;
		BezierVertex bestBezierVertex2 = null;
		Waypoint bestWaypoint = null;
		
		for(int j = 0; j < track.getWaypoints().size()-1; j++) {
			Waypoint waypoint = track.getWaypoint(j);
			
			for(int i = 0; i < waypoint.getVertices().size()-1; i++) {
				BezierVertex v1 = waypoint.getBezierVertex(i);
				BezierVertex v2 = waypoint.getBezierVertex(i+1);
				
				double distance1 = distanceBetweenBezierVertices(v1, v2);
				double distance2 = distanceBetweenBezierVertices(v1, vi) + distanceBetweenBezierVertices(vi, v2);
				double difference = distance2 - distance1;
				
				if(difference < bestDifference) {
					//System.out.println("bestDifference: " + )
					bestBezierVertex1 = v1;
					bestBezierVertex2 = v2;
					bestWaypoint = waypoint;
					bestDifference = difference;
				}
			}
		}
		
		
		if(bestBezierVertex1 == null) return null;
		if(bestBezierVertex2 == null) return null;
		if(bestWaypoint == null) return null;
		
		//System.out.println("bestWaypoint: " + bestWaypoint.getId());
		
		int bestIndex = bestWaypoint.getId();
		
		//Compute the location of the new vertex on the line
		//In order to keep the line straight

		//The two positions of the old segment
		LatLon a = bezierVertexToLatLon(bestBezierVertex1);
		LatLon e = bezierVertexToLatLon(bestBezierVertex2);

		//The clicked position
		LatLon b = p.getLatLon();
		//Compute angels between them
		//Angle azAB = LatLon.rhumbAzimuth(a, b);
		//Angle azAE = LatLon.rhumbAzimuth(a, e);
		Angle azAB = LatLon.greatCircleAzimuth(a, b);
		Angle azAE = LatLon.greatCircleAzimuth(a, e);

		Angle diffABAE = azAB.subtract(azAE);

		//Compute the position opposite to b
		//Angle dis = LatLon.rhumbDistance(a, b);
		//LatLon c = LatLon.rhumbEndPosition(a, (azAB.subtract(diffABAE)).subtract(diffABAE), dis);
		Angle dis = LatLon.greatCircleDistance(a, b);
		LatLon c = LatLon.greatCircleEndPosition(a, (azAB.subtract(diffABAE)).subtract(diffABAE), dis);

		//Get the point that is in between them
		LatLon d = LatLon.interpolate(0.5, b, c);

		//Insert the vertex at the appropirate location
		Position insertPosition = Position.fromDegrees(d.getLatitude().degrees, d.getLongitude().degrees, 100);
		if(insert) {	
			track.addWaypoint(bestIndex+1, new Waypoint(track, d.getLatitude().degrees, d.getLongitude().degrees, bestWaypoint.getSpeed(), bestWaypoint.getAngle()));
		}

		return insertPosition;

	}
	
	public static double distanceBetweenBezierVertices(BezierVertex v1, BezierVertex v2) {
		double tmp;
        double result = 0.0;
        tmp = v1.x - v2.x;
        result += tmp * tmp;
        tmp = v1.y - v2.y;
        result += tmp * tmp;
        tmp = v1.z - v2.z;
        result += tmp * tmp;
        return result;
	}
	
	public static String colorToHex(Color color) {
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());
		if(r.length() < 2) r = "0" + r;
		if(g.length() < 2) g = "0" + g;
		if(b.length() < 2) b = "0" + b;
		return "#" + r + g + b;
	}

	public static Color hexToColor(String hex) {
		int r = Integer.parseInt(hex.substring(1, 3), 16);
		int g = Integer.parseInt(hex.substring(3, 5), 16);
		int b = Integer.parseInt(hex.substring(5, 7), 16);
		return new Color(Display.getDefault(), r, g, b);
	}
}
