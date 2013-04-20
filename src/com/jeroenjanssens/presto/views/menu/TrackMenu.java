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

package com.jeroenjanssens.presto.views.menu;

import gov.nasa.worldwind.geom.Position;

import java.util.ArrayList;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.TreeModel;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackMenu {

	private Menu menu;
	private MenuItem itemRename;
	private MenuItem itemFlyToStart;
	private MenuItem itemFlyToVessel;
	private MenuItem itemWarpToStart;

	private EarthView earthView;
	private Track track;
	
	public TrackMenu(final EarthView earthView) {
		this.earthView = earthView;
		menu = new Menu(earthView.getSite().getShell(), SWT.PUSH);
		
		itemRename = new MenuItem(menu, SWT.PUSH);
		itemRename.setText("Rename...");
		itemRename.setImage(Activator.getImageDescriptor("icons/rename1.gif").createImage(earthView.getDisplay()));
		itemRename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				earthView.getCurrentScenarioEditor().getTrackTreeViewer().renameSelected();
			}
		});
		
		@SuppressWarnings("unused")
		MenuItem sep1 = new MenuItem(menu, SWT.SEPARATOR);
		
		itemFlyToStart = new MenuItem(menu, SWT.PUSH);
		itemFlyToStart.setText("Fly To Start");
		itemFlyToStart.setImage(Activator.getImageDescriptor("icons/flyto.gif").createImage(earthView.getDisplay()));
		itemFlyToStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Waypoint w = track.getWaypoint(0);
				if(w != null) {
					Position p = w.getPosition();
					if(p != null) {
						earthView.flyTo(p);
					}
				}
			}
		});
		
		
		
		
		itemFlyToVessel = new MenuItem(menu, SWT.PUSH);
		itemFlyToVessel.setText("Fly To Vessel");
		itemFlyToVessel.setImage(Activator.getImageDescriptor("icons/flyto.gif").createImage(earthView.getDisplay()));
		itemFlyToVessel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Position p = track.getVessel().getPositionHeading().getPosition();
				if(p != null) {
					earthView.flyTo(p);
				} 
			}
		});
		
		@SuppressWarnings("unused")
		MenuItem sep2 = new MenuItem(menu, SWT.SEPARATOR);
		
		itemWarpToStart = new MenuItem(menu, SWT.PUSH);
		itemWarpToStart.setText("Warp To Start");
		itemWarpToStart.setImage(Activator.getImageDescriptor("icons/warpto.gif").createImage(earthView.getDisplay()));
		itemWarpToStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Waypoint w = track.getWaypoint(0);
				if(w != null) {
					earthView.getCurrentScenarioEditor().getTimeLine().setCursorTime(w.getTime());
					earthView.getCurrentScenarioEditor().getTimeLine().doRedraw();
					earthView.getWWD().redraw();
				}
			}
		});

	}
	
	public void showMenu(Track track, Point location) {
		if(track == null) return;
		
		ArrayList<TreeModel> al = new ArrayList<TreeModel>();
		al.add(track);
		earthView.getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().setSelection(new StructuredSelection(al), true);
		
		
		this.track = track;
		menu.setLocation(location);
		menu.setVisible(true);
		
		Position p = track.getVessel().getPositionHeading().getPosition();
		if(p == null) {
			itemFlyToVessel.setEnabled(false);
		} else {
			itemFlyToVessel.setEnabled(true);
		}
		
		Waypoint w = track.getWaypoint(0);
		if(w != null) {
			p = w.getPosition();
			if(p != null) {
				itemFlyToStart.setEnabled(true);
				itemWarpToStart.setEnabled(true);

			} else {
				itemFlyToStart.setEnabled(false);
				itemWarpToStart.setEnabled(false);
			}
		} else {
			itemFlyToStart.setEnabled(false);
			itemWarpToStart.setEnabled(false);
		}		
	}
	
	public void showMenu(final Track track, final java.awt.Point location) {
		earthView.getDisplay().syncExec(new Runnable() {
			public void run() {
				showMenu(track, new Point(location.x, location.y));
			}
		});
	}

	public void disposeImages() {
		itemRename.getImage().dispose();
		itemFlyToStart.getImage().dispose();
		itemFlyToVessel.getImage().dispose();
		itemWarpToStart.getImage().dispose();
	}
}
