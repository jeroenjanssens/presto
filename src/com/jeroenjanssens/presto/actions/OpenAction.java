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

package com.jeroenjanssens.presto.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditorInput;
import com.jeroenjanssens.presto.views.scenario.ScenarioManager;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class OpenAction extends Action {

	private final IWorkbenchWindow window;

	public OpenAction(IWorkbenchWindow window) {
		super("Open Scenario");
		this.window = window;
		setId(ICommandIds.CMD_OPEN);
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		setImageDescriptor(com.jeroenjanssens.presto.Activator.getImageDescriptor("icons/open.png"));
	}

	@Override
	public void run() {
		FileDialog fd = new FileDialog(window.getShell(), SWT.OPEN);
		fd.setText("Open Scenario");
		fd.setFilterPath("C:/");
		fd.setFilterNames(ScenarioManager.FILTER_NAMES);
		fd.setFilterExtensions(ScenarioManager.FILTER_EXTS);
		String selected = fd.open();
		//System.out.println(selected);
		if(selected != null) {
			Scenario s = Activator.getDefault().getScenarioManager().readScenarioFromFile(selected);
			if(s != null) {
				//System.out.println(s.toXML());
				ScenarioEditorInput sei = new ScenarioEditorInput(s);
				try {
					window.getActivePage().openEditor(sei, ScenarioEditor.ID, true);
				} catch (PartInitException ex) {
					Activator.getDefault().getLog().log(ex.getStatus());
				}
			} else {
				//System.out.println("Scenario is null");
			}
		}
	}
}
