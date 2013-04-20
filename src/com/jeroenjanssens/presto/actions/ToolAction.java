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
import org.eclipse.ui.PlatformUI;

import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ToolAction extends Action {
	
	private final IWorkbenchWindow window;
	
	private int tool;
	
	public ToolAction(IWorkbenchWindow window, String label, String id, String image, int tool) {
		this.window = window;
        setText(label);
        setId(id);
        setActionDefinitionId(id);
		setImageDescriptor(com.jeroenjanssens.presto.Activator.getImageDescriptor(image));
		this.tool = tool;
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
				earthView.getEarthViewToolBar().selectTool(tool);
			}
		}
	}
}

