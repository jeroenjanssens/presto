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

package com.jeroenjanssens.presto.views.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public abstract class AbstractPropertiesComposite extends ScrolledComposite implements IPropertiesComposite  {

	protected String title; 
	protected TabItem item;
	
	public AbstractPropertiesComposite(Composite parent, int style, GeneralPropertiesView propertiesView) {
		super(parent, style);
		title = "No Title";
	}

	
	public void addToTabFolder(TabFolder tabFolder) {
		item = new TabItem (tabFolder, SWT.NONE);
		item.setControl(this);
		item.setText(title);
	}
	
	public TabItem getTabItem() {
		return item;
	}
	
	public void build() {
		//this.setLayout(new GridLayout());
		//this.setAlwaysShowScrollBars(true);
		//this.setMinHeight(400);
		/*
		Composite contentComposite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		contentComposite.setLayout(gridLayout);
		
		this.setExpandVertical(true);
		this.setExpandHorizontal(true);

		Label l = new Label(contentComposite, SWT.NONE);
		l.setText("hallo!");
		
		
		
		Group g = new Group(contentComposite, SWT.NONE);
		g.setText("Position");
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		g.setLayoutData(layoutData);
		
		this.setContent(contentComposite);
		this.setSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		*/
	}
}
