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

package com.jeroenjanssens.presto;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.jeroenjanssens.presto.actions.ExportAction;
import com.jeroenjanssens.presto.actions.ICommandIds;
import com.jeroenjanssens.presto.actions.LoadBackgroundAction;
import com.jeroenjanssens.presto.actions.NewScenarioAction;
import com.jeroenjanssens.presto.actions.OpenAction;
import com.jeroenjanssens.presto.actions.ResetEarthViewAction;
import com.jeroenjanssens.presto.actions.ToolAction;
import com.jeroenjanssens.presto.views.earth.tools.EarthViewToolBar;

/**
 * @author Jeroen Janssens
 * @created Oct 19, 2009
 *
 */


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	
	private NewScenarioAction newScenarioAction;
	private OpenAction openAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAsAction;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction closeAction;
	private IWorkbenchAction closeAllAction;

	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private ExportAction exportAction;
	private LoadBackgroundAction loadBackgroundAction;
	
	ToolAction navigationToolAction;
	ToolAction vertexSelectionToolAction;
	ToolAction trackSelectionToolAction;
	ToolAction drawTrackToolAction;
	ToolAction insertVertexToolAction;
	ToolAction deleteVetexToolAction;
	
	private ResetEarthViewAction resetEarthViewAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		newScenarioAction = new NewScenarioAction(window);
		register(newScenarioAction);

		openAction = new OpenAction(window);
		register(openAction);

		saveAction = ActionFactory.SAVE.create(window);
		saveAction.setImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save.gif"));
		saveAction.setDisabledImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save_disabled.gif"));
		register(saveAction);

		saveAsAction = ActionFactory.SAVE_AS.create(window);
		saveAsAction.setImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save_as.gif"));
		saveAsAction.setDisabledImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save_as_disabled.gif"));
		register(saveAsAction);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		saveAllAction.setImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save_all.gif"));
		saveAllAction.setDisabledImageDescriptor(com.jeroenjanssens.presto.Activator
				.getImageDescriptor("icons/save_all_disabled.gif"));
		register(saveAllAction);
		
		exportAction = new ExportAction(window, "Export Scenario...");
		register(exportAction);
		
		loadBackgroundAction = new LoadBackgroundAction(window, "Load Background...");
		register(loadBackgroundAction);
		

		closeAction = ActionFactory.CLOSE.create(window);
		register(closeAction);

		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		register(closeAllAction);

		exitAction = ActionFactory.QUIT.create(window);

		register(exitAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

		navigationToolAction = new ToolAction(window, "Navigation Tool", ICommandIds.CMD_NAVIGATION, "icons/navigate.png", EarthViewToolBar.TOOL_NAVIGATION);
		trackSelectionToolAction = new ToolAction(window, "Track Selection Tool", ICommandIds.CMD_TRACKSELECTION, "icons/tool_trackselection.png", EarthViewToolBar.TOOL_TRACKSELECTION);
		vertexSelectionToolAction = new ToolAction(window, "Waypoint Selection Tool", ICommandIds.CMD_VERTEXSELECTION, "icons/tool_waypointselection.png", EarthViewToolBar.TOOL_WAYPOINTSELECTION);
		drawTrackToolAction = new ToolAction(window, "Draw Track Tool", ICommandIds.CMD_DRAWTRACK, "icons/tool_drawtrack.png", EarthViewToolBar.TOOL_DRAWTRACK);
		insertVertexToolAction = new ToolAction(window, "Insert Waypoint Tool", ICommandIds.CMD_INSERTVERTEX, "icons/add_vertext_mode.gif", EarthViewToolBar.TOOL_INSERTWAYPOINT);
		deleteVetexToolAction = new ToolAction(window, "Remove Waypoint Tool", ICommandIds.CMD_REMOVEVERTEX, "icons/tool_remove_vertex.png", EarthViewToolBar.TOOL_REMOVEWAYPOINT);
		
		register(navigationToolAction);
		register(trackSelectionToolAction);
		register(vertexSelectionToolAction);
		register(drawTrackToolAction);
		register(insertVertexToolAction);
		register(deleteVetexToolAction);
	
		resetEarthViewAction = new ResetEarthViewAction(window,
		"Reset Earth View");
		register(resetEarthViewAction);
	}


	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
		MenuManager viewMenu = new MenuManager("&View",	IWorkbenchActionConstants.M_VIEW);
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);

		menuBar.add(fileMenu);
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(editMenu);

		menuBar.add(viewMenu);
		viewMenu.add(resetEarthViewAction);
		viewMenu.add(loadBackgroundAction);
		
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		// File
		fileMenu.add(newScenarioAction);
		fileMenu.add(openAction);
		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		// fileMenu.add(newWindowAction);
		fileMenu.add(new Separator());
		// fileMenu.add(messagePopupAction);
		// fileMenu.add(openViewAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		editMenu.add(new Separator());
		
		editMenu.add(navigationToolAction);
		editMenu.add(trackSelectionToolAction);
		editMenu.add(vertexSelectionToolAction);
		editMenu.add(drawTrackToolAction);
		editMenu.add(insertVertexToolAction);
		editMenu.add(deleteVetexToolAction);
		
		helpMenu.add(aboutAction);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager mainToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(mainToolbar, "main"));
		coolBar.setLockLayout(true);
		mainToolbar.add(newScenarioAction);
		mainToolbar.add(openAction);
		mainToolbar.add(saveAction);
		mainToolbar.add(saveAsAction);
		mainToolbar.add(saveAllAction);
	
		IToolBarManager editToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(editToolbar, "edit"));
		editToolbar.add(exportAction);

		
		IToolBarManager viewToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(viewToolbar, "view"));
		viewToolbar.add(resetEarthViewAction);
		viewToolbar.add(loadBackgroundAction);
	}

}