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

public class Scenario {

	private String fileName;
	private ScenarioDescription description;
	private Folder rootFolder;
	
	public Scenario(String fileName) {
		this.fileName = fileName;
		this.description = new ScenarioDescription();
		rootFolder = new Folder("root");
	}
	
	public ScenarioDescription getDescription() {
		return description;
	}

	public void setDescription(ScenarioDescription description) {
		this.description = description;
	}

	public boolean isSame(String fileName) {
		return this.fileName.equals(fileName);
	}
		
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Folder getRootFolder() {
		return rootFolder;
	}
	
	public void setRootFolder(Folder root) {
		this.rootFolder = root;
	}
	
	public String getName() {
		return fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator"))+1);
	}
	
	/*
	public void createModel() {
		rootFolder = new Folder("root");
		
		Folder tracks = new Folder("Example");
		Track t1 = new Track("Track 1");
		Track t2 = new Track("Track 2");
		rootFolder.add(tracks);
		tracks.add(t1);
		tracks.add(t2);
		//t.addWaypoint(new Waypoint(t, 0 ,0));
		//t.addWaypoint(new Waypoint(t, 54 ,4));
		
		for(int i = 0; i < 10; i++) {
			t1.addWaypoint(new Waypoint(t1, (Math.random() * 4) + 50, (Math.random() * 4)));
		}
		
		
		for(int i = 0; i < 10; i++) {
			t2.addWaypoint(new Waypoint(t2, (Math.random() * 8) + 60, (Math.random() * 8) -20));
		}
		
		//t1.getWaypoint(0).setSpeed(0.1);
		t1.getWaypoint(9).setSpeed(100);
		//t1.getWaypoint(3).setSpeed(0.1);
		t1.update(null);
		t2.update(null);
	}
	*/
	public String toXML() {
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n";
		xml += "<scenario>\n";
		xml += description.toXML();
		xml += rootFolder.toXML(1);
		xml += "</scenario>\n";
		return xml;
	}
}
