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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class WaypointMenu {
	
	private Menu menu;
	private MenuItem item;
	private EarthView earthView;
	private Waypoint waypoint;

	public WaypointMenu(final EarthView earthView) {
		this.earthView = earthView;
		menu = new Menu(earthView.getSite().getShell(), SWT.PUSH);
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Remove Waypoint");
		item.setImage(Activator.getImageDescriptor("icons/delete_obj.gif").createImage(earthView.getDisplay()));
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				waypoint.getTrack().removeWaypoint(waypoint);
				earthView.getDisplay().syncExec(new Runnable() {
					public void run() {
						waypoint.getTrack().update(earthView);
						earthView.getWWD().redraw();
						earthView.getCurrentScenarioEditor().getTimeLine().doRedraw();
					}
				});
			}
		});
	}
	
	public void showMenu(Waypoint waypoint, Point location) {
		if(waypoint == null) return;
		this.waypoint = waypoint;
		menu.setLocation(location);
		menu.setVisible(true);
	}
	
	public void showMenu(final Waypoint waypoint, final java.awt.Point location) {
		earthView.getDisplay().syncExec(new Runnable() {
			public void run() {
				showMenu(waypoint, new Point(location.x, location.y));
			}
		});
	}
	
	public void disposeImages() {
		item.getImage().dispose();
	}
}
