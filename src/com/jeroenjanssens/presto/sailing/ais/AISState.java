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

package com.jeroenjanssens.presto.sailing.ais;

import java.io.Serializable;
import java.util.HashMap;

import org.eclipse.nebula.widgets.cdatetime.CDateTime;

import com.jeroenjanssens.presto.tools.DeepCopy;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class AISState extends HashMap<AISParameter, Object> implements Comparable<AISState>, Serializable {
	private static final long serialVersionUID = 1587226128757843439L;
	private long time = 0;

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public int compareTo(AISState otherAISMessage) {
		return Long.valueOf(this.getTime()).compareTo(Long.valueOf(otherAISMessage.getTime()));
	}

	public void merge(AISState newAISState) {
		for(AISParameter p : newAISState.keySet()) {
			this.put(p, DeepCopy.copy(newAISState.get(p)));
		}
	
	}
	
	/*
	public Object put(AISParameter key, Object value) {
		System.out.println("Put: key: " + key.getTextName() + " value: " + value + " (instance of " + value.getClass() + ")");
		return super.put(key, value);
	}
	*/

	public String toXML(int indent) {
		String tab = "";
		for(int t = 0; t < indent; t++) tab += "\t";
		String tabs = tab + "\t";
		
		String s = ""; 
		s += tab + "<aisvalues>\n";
		
		for(AISParameter aisParameter : this.keySet()) {
			Object value = this.get(aisParameter);
			String newValue = "";
			
			if(aisParameter.getType().equals(String.class)) {
				newValue = (String)value;
			} else if(aisParameter.getType().equals(Boolean.class)) {
				newValue = "" + value;
			} else if(aisParameter.getType().equals(Double.class)) {
				newValue = "" + value;
			} else if(aisParameter.getType().equals(Integer.class)) {
				newValue = "" + value;
			} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
				newValue = "" + value;
			} else if(aisParameter.getType().equals(CDateTime.class)) {
				newValue = "" + value;
			} 
			s += tabs + "<aisvalue parameter='" + aisParameter.getTextName() + "' value='" + newValue + "' />\n";
		}
		
		s += tab + "</aisvalues>\n";
		return s;
	}
	
	public Object getSafe(AISParameter aisParameter) {
		//System.out.println("getSafe(" + aisParameter.getTextName() + ") --> " + aisParameter.getType());
		Object o = get(aisParameter);
		if(o == null) {
			if(aisParameter.getType().equals(String.class)) {
				o = "";
			} else if(aisParameter.getType().equals(Boolean.class)) {
				o = false;
			} else if(aisParameter.getType().equals(Double.class)) {
				o = Double.valueOf(0.0d);
			} else if(aisParameter.getType().equals(Integer.class)) {
				o = 0;
			} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
				o = 0;
			} else if(aisParameter.getType().equals(CDateTime.class)) {
				o = (long) 0; // BUG: it seems it is of type double
			} 
		} 
		return o;
	}
}
