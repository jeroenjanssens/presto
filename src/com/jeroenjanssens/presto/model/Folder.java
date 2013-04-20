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

import com.jeroenjanssens.presto.sailing.TrackTools;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Folder extends TreeModel {
	
	private ArrayList<TreeModel> children;
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public Folder(String name) {
		super(name);
		children = new ArrayList<TreeModel>();
		
	}
	
	private static class Adder implements IModelVisitor {

		public void visitAnimatedLayer(AnimatedLayer layer, Object argument) {
			((Folder) argument).addAnimatedLayer(layer);
		}

		public void visitTrack(Track track, Object argument) {
			((Folder) argument).addTrack(track);
		}

		public void visitFolder(Folder folder, Object argument) {
			((Folder) argument).addFolder(folder);
		}

	}

	private static class Remover implements IModelVisitor {
		public void visitAnimatedLayer(AnimatedLayer layer, Object argument) {
			((Folder) argument).removeAnimatedLayer(layer);
		}
		
		public void visitTrack(Track track, Object argument) {
			((Folder) argument).removeTrack(track);
		}
		
		public void visitFolder(Folder box, Object argument) {
			((Folder) argument).removeFolder(box);
			box.addListener(NullDeltaListener.getSoleInstance());
		}

	}

	public ArrayList<TreeModel> getChildren() {
		return children;
	}
	
	public void addFolder(Folder folder) {
		children.add(folder);
		folder.setParent(this);
		fireAdd(folder);
	}
	
	public void addTrack(Track track) {
		children.add(track);
		track.setParent(this);
		fireAdd(track);
	}
	
	protected void addAnimatedLayer(AnimatedLayer layer) {
		children.add(layer);
		layer.setParent(this);
		fireAdd(layer);
	}		
	
	public void remove(TreeModel toRemove) {
		toRemove.accept(remover, this);
	}
	
	protected void removeAnimatedLayer(AnimatedLayer layer) {
		children.remove(layer);
		layer.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(layer);
	}
	
	protected void removeTrack(Track track) {
		children.remove(track);
		track.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(track);
	}
	
	protected void removeFolder(Folder folder) {
		children.remove(folder);
		folder.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(folder);	
	}

	public void add(TreeModel toAdd) {
		toAdd.accept(adder, this);
	}
	
	public ArrayList<Track> getAllTracks(boolean onlyVisible, boolean onlyUnLocked) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		for(TreeModel c : children) {
			if(onlyVisible && !c.isVisible()) continue;
			if(onlyUnLocked && c.isLocked()) continue;
			if(c instanceof Track) {
				tracks.add((Track)c);
			} else if(c instanceof Folder) {
				tracks.addAll(((Folder)c).getAllTracks(onlyVisible, onlyUnLocked));
			}
		}
		return tracks;
	}
	
	public ArrayList<Waypoint> getAllWaypoints(boolean onlyVisible, boolean onlyUnLocked) {
		ArrayList<Track> tracks = getAllTracks(onlyVisible, onlyUnLocked);
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		for(Track track: tracks) {
			waypoints.addAll(track.getWaypoints());
		}
		return waypoints;
	}
	
	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitFolder(this, passAlongArgument);
	}

	
	public String toXML(int indent) {
		String tabs = "";
		for(int t = 0; t < indent; t++) tabs += "\t";
		
		String xml = tabs + "<folder name='" + name + "' color='" + TrackTools.colorToHex(getColor()) + "' visible='" + isVisible() + "' locked='" + isLocked() + "'>\n";
		
		for(TreeModel c : children) {
			if(c instanceof Track) {
				xml += ((Track)c).toXML(indent+1);
			} else if(c instanceof Folder) {
				xml += ((Folder)c).toXML(indent+1);
			}
		}
		
		xml += tabs + "</folder>\n";
		return xml;
	}
}
