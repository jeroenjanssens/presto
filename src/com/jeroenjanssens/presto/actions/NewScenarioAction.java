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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditorInput;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class NewScenarioAction extends Action {

	private final IWorkbenchWindow window;
	
	public NewScenarioAction(IWorkbenchWindow window) {
		super("New Scenario");
		this.window = window;
		setId(ICommandIds.CMD_NEW);
		setActionDefinitionId(ICommandIds.CMD_NEW);
		setImageDescriptor(com.jeroenjanssens.presto.Activator.getImageDescriptor("icons/new.gif"));
	}
	
	
	@Override
	public void run() {
		Scenario s = Activator.getDefault().getScenarioManager().createNewScenario();
    	ScenarioEditorInput sei = new ScenarioEditorInput(s);
		try {
			ScenarioEditor se = (ScenarioEditor) window.getActivePage().openEditor(sei, ScenarioEditor.ID, true);
			se.setChanged(true);
		} catch (PartInitException ex) {
			Activator.getDefault().getLog().log(ex.getStatus());
		}
	}
}
