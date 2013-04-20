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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.BaseSaveAction;

import com.jeroenjanssens.presto.sailing.ExportDialog;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ExportAction extends BaseSaveAction {

	private final IWorkbenchWindow window;

	public ExportAction(IWorkbenchWindow window, String label) {
		super(label, window);
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT);
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EXPORT);
		setImageDescriptor(com.jeroenjanssens.presto.Activator.getImageDescriptor("/icons/export.gif"));
		setDisabledImageDescriptor(com.jeroenjanssens.presto.Activator.getImageDescriptor("/icons/export_disabled.gif"));
	}

	@Override
	public void run() {
		if(window != null) {	

			EarthView earthView = null;
			try {
				earthView = (EarthView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(EarthView.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}

			if(earthView != null) {
				if(earthView.getCurrentScenarioEditor() != null) {
					ExportDialog ed = new ExportDialog(window.getShell(), earthView.getCurrentScenarioEditor().getScenario());
					ed.open();
				}
			}
		}
	}
	
	@Override
	protected void updateState() {

        IEditorPart editor = getActiveEditor();
        setEnabled(editor != null);
    }
}

