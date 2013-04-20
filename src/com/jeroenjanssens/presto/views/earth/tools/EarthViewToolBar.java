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

package com.jeroenjanssens.presto.views.earth.tools;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.scenario.timeline.TimeLine;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class EarthViewToolBar {

	public static final int TOOL_NAVIGATION 			= 0;
	public static final int TOOL_TRACKSELECTION 		= 1;
	public static final int TOOL_WAYPOINTSELECTION 		= 2;
	public static final int TOOL_DRAWTRACK				= 3;	
	public static final int TOOL_INSERTWAYPOINT			= 4;
	public static final int TOOL_REMOVEWAYPOINT 		= 5;
	public static final int TOOL_FREETRANSFORM			= 6;

	private Display display;
	private ToolBar toolBar;

	private ArrayList<ITool> tools;
	

	private ITool currentTool;

	private EarthView earthView;
	private PrestoCursor prestoCursor;
	
	private boolean spaceBarDown;

	public EarthViewToolBar(final EarthView earthView, Composite parent) {

		prestoCursor = new PrestoCursor();
		
		this.earthView = earthView;

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
		composite.setLayoutData(gridData);

		display = parent.getDisplay();

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.VERTICAL);

		tools = new ArrayList<ITool>();
		tools.add(new NavigationTool(this));
		tools.add(new TrackSelectionTool(this));
		tools.add(new WaypointSelectionTool(this));
		tools.add(new DrawTrackTool(this));
		tools.add(new InsertWaypointTool(this));
		tools.add(new RemoveWaypointTool(this));
		//tools.add(new FreeTransformTool(this));

		toolBar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				earthView.getMenuManager().disposeImages();
				for(ITool t : tools) {
					t.disposeImage();
				}
				
				for(int i = 0; i < 9; i++) {
					//System.out.println("Disposing TimeLine images (dot and stuff) " + i);
					TimeLine.getImage(i).dispose();
				}
				
				
			}
		});
		
		//MenuItem[] menuItems = this.getEarthView().getSite().getWorkbenchWindow().getShell().getMenuBar().getItems();
		
		
		
		//this.getEarthView().getSite().getWorkbenchWindow().getWorkbench().get
		
		//Activator.getDefault().ge
		
		
		/*
		for(int i = 0; i < menuItems.length; i++) {
			System.out.println("MenuItem: " + menuItems[i].getText());
			
			if(menuItems[i].getText().equals("&Edit")) {
				menuItems[i].getMenu()
			}
		}
		*/
		
		for(ITool a : tools) {
			a.addToToolBar(display, toolBar);
		}

		toolBar.pack ();
	}


	public PrestoCursor getPrestoCursor() {
		return prestoCursor;
	}


	public ToolBar getToolBar() {
		return toolBar;
	}

	public ITool getCurrentTool() {
		return currentTool;
	}

	public EarthView getEarthView() {
		return earthView;
	}


	public void selectTool(int toolState) {
		if(currentTool != null) {
			if((toolState == EarthViewToolBar.TOOL_NAVIGATION) && (currentTool.equals(tools.get(EarthViewToolBar.TOOL_NAVIGATION)))) {
				currentTool.getToolItem().setSelection(true);
				return;
			}
		}

		//System.out.println("selectTool");
		ITool newTool = null;
		try {
			newTool = tools.get(toolState);
		} catch (IndexOutOfBoundsException e) {
			return;
		}
		if(newTool == null) return;
		//System.out.println("selected: " + newTool.getName());
		if(newTool.equals(currentTool)) newTool = tools.get(EarthViewToolBar.TOOL_NAVIGATION);
		if(newTool.equals(currentTool)) {
			return;
		}
		//If there was already a tool registered, make him stop listening to events
		if(currentTool != null) {
			removeListeners(currentTool);
			currentTool.setFocus(false);
		}

		//Uncheck all other tools just to be safe
		for(ITool tool : tools) {
			if(!tool.equals(newTool)) {
				tool.getToolItem().setSelection(false);
			}
		}

		//Make the new tool listen to events
		addListeners(newTool);
		//Probably not neccessary to set the current action to checked
		newTool.getToolItem().setSelection(true);
		newTool.setFocus(true);
		//Change the cursor
		earthView.getWWD().setCursor(newTool.getDefaultCursor());
		//Change the current tool to the new tool
		currentTool = newTool;
		earthView.getWWD().redraw();
		setSpaceBarDown(false);
		earthView.getWWD().requestFocus();
	}
	
	
	public void updateTrackTreeViewer() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				getEarthView().getCurrentScenarioEditor().getTrackTreeViewer().getTreeViewer().refresh();
			}
		});
	}
	
	private void addListeners(ITool tool) {
		earthView.getPrestoInputHandler().addKeyListener(tool);
		earthView.getPrestoInputHandler().addMouseListener(tool);
		earthView.getPrestoInputHandler().addMouseMotionListener(tool);
		earthView.getPrestoInputHandler().addMouseWheelListener(tool);
	}
	
	private void removeListeners(ITool tool) {
		earthView.getPrestoInputHandler().removeKeyListener(tool);
		earthView.getPrestoInputHandler().removeMouseListener(tool);
		earthView.getPrestoInputHandler().removeMouseMotionListener(tool);
		earthView.getPrestoInputHandler().removeMouseWheelListener(tool);
		
	}


	public void setSpaceBarDown(boolean spaceBarDown) {
		this.spaceBarDown = spaceBarDown;
	}

	public boolean isSpaceBarDown() {
		return spaceBarDown;
	}	
	
	public ArrayList<ITool> getTools() {
		return tools;
	}
}
