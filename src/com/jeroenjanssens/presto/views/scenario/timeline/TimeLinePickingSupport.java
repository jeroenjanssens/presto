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

package com.jeroenjanssens.presto.views.scenario.timeline;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TimeLinePickingSupport extends HashMap<Rectangle, Object> {
	
	private static final long serialVersionUID = 2789255260948654235L;

	public void start() {
		this.clear();
	}
	
	public Object getTopPickedObject(Point point) {
		
		/*
		for(Rectangle r : this.keySet()) {
			if(r.contains(point)) {
				return this.get(r);
			}
		}
		*/
		
		ArrayList<Object> pickedObjects = getPickedObjects(point);
		if(pickedObjects.size() < 1) return null;
		
		return pickedObjects.get(pickedObjects.size()-1);
		
		
		//return null;
	}
	
	public ArrayList<Object> getPickedObjects(Point point) {
		ArrayList<Object> pickedObjects = new ArrayList<Object>();
		for(Rectangle r : this.keySet()) {
			if(r.contains(point)) {
				pickedObjects.add(this.get(r));
			}
		}
		return pickedObjects;
	}
	
	public void addPickableObject(Rectangle r, Object o) {
		this.put(r, o);
	}
}
