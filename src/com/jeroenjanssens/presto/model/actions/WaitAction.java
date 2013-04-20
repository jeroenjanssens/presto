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

package com.jeroenjanssens.presto.model.actions;

import com.jeroenjanssens.presto.model.Waypoint;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class WaitAction extends AbstractAction implements IAction {

	private long time;
	
	public WaitAction(Waypoint waypoint, long time) {
		super(waypoint);
		this.time = time;
	}

	public String toText() {
		return "Wait " + (time/1000) + " seconds";
	}
	
	public String toXML(int indent) {

		String tabs = "";
		for(int t = 0; t < indent; t++) tabs += "\t";
		
		String xml = tabs + "<action />\n";
		return xml;
	}
	
}
