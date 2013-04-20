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

package com.jeroenjanssens.presto.views.scenario;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Scenario;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ScenarioEditorInput implements IEditorInput {

	private String name;
	private String fileName;

	public ScenarioEditorInput(Scenario scenario) {
		this.fileName = scenario.getFileName();
		this.name = scenario.getName();
	}

	public Scenario getScenario() {	
		return Activator.getDefault().getScenarioManager().getScenario(this.fileName);
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return this.fileName;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final ScenarioEditorInput other = (ScenarioEditorInput) obj;
		if (this.fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!this.fileName.equals(other.fileName)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.fileName == null) ? 0 : this.fileName.hashCode());
		return result;
	}

}
