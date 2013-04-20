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


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class BezierVertex {
	public double x;
	public double y;
	public double z;
	public double latitude;
	public double longitude;
	public long time;							
	public double segmentDistance = 0;			
	public double distanceToNextVertex = 0;
	
	public BezierVertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
