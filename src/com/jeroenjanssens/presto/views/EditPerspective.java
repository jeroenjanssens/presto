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

package com.jeroenjanssens.presto.views;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.properties.GeneralPropertiesView;




/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class EditPerspective implements IPerspectiveFactory {

	public static String ID = "com.jeroenjanssens.presto.perspectives.editperspective";
	
	public void createInitialLayout(IPageLayout layout) {
		
		layout.addView(EarthView.ID, IPageLayout.BOTTOM, 0.2f, IPageLayout.ID_EDITOR_AREA);	
		layout.getViewLayout(EarthView.ID).setCloseable(false);
		layout.getViewLayout(EarthView.ID).setMoveable(false);
		
		layout.addView(GeneralPropertiesView.ID, IPageLayout.RIGHT, 0.8f, EarthView.ID);
		layout.getViewLayout(GeneralPropertiesView.ID).setCloseable(false);
		layout.getViewLayout(GeneralPropertiesView.ID).setMoveable(false);
	}
}
