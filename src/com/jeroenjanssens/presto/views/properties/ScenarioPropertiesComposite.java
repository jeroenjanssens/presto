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

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jeroenjanssens.presto.model.Scenario;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ScenarioPropertiesComposite extends AbstractPropertiesComposite {

	private JiglooScenarioComposite contentComposite = null;
	private GeneralPropertiesView propertiesView;
	private Scenario currentScenario;
	
	public ScenarioPropertiesComposite(Composite parent, int style, GeneralPropertiesView propertiesView) {
		super(parent, style, propertiesView);
		this.propertiesView = propertiesView;
		this.title = "Scenario";
		
		this.setAlwaysShowScrollBars(true);
		this.setLayout(new GridLayout());
		
		GridData group1LData = new GridData();
		group1LData.verticalAlignment = GridData.FILL;
		group1LData.grabExcessHorizontalSpace = true;
		group1LData.grabExcessVerticalSpace = true;
		group1LData.horizontalAlignment = GridData.FILL;
		
		this.setLayoutData(group1LData);
		contentComposite = new JiglooScenarioComposite(this, SWT.NONE);
		contentComposite.setLayoutData(group1LData);

		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		this.setContent(contentComposite);
		this.setSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setMinHeight(400);
	}

	public void setSelection(Scenario currentScenario) {
		this.currentScenario = currentScenario;
		
		if(currentScenario != null) {
			contentComposite.enable(true);
			loadName(currentScenario);
			loadAuthor(currentScenario);
			loadDifficulty(currentScenario);
			loadCreated(currentScenario);
			loadDescription(currentScenario);
		}
	}
	

	public void restore() {
		setSelection(currentScenario);
	}
	
	public void clearValues() {
		contentComposite.enable(false);
		contentComposite.vName.setText("");
		contentComposite.vAuthor.setText("");
		contentComposite.vDifficulty.setSelection(1);
		contentComposite.vCreated.setSelection(null);
		contentComposite.vDescription.setText("");
	}
	
	private void loadName(Scenario scenario) {
		contentComposite.vName.setText(scenario.getDescription().getName());
	}	
	
	private void loadAuthor(Scenario scenario) {
		contentComposite.vAuthor.setText(scenario.getDescription().getAuthor());
	}
	
	private void loadDifficulty(Scenario scenario) {
		contentComposite.vDifficulty.setSelection(scenario.getDescription().getDifficulty());
	}
	
	private void loadCreated(Scenario scenario) {
		contentComposite.vCreated.setSelection(new Date(scenario.getDescription().getCreated()));
	}

	private void loadDescription(Scenario scenario) {
		contentComposite.vDescription.setText(scenario.getDescription().getDescriptionText());
	}
	
	public void saveName() {
		if(currentScenario == null) return;		
		currentScenario.getDescription().setName(contentComposite.vName.getText());
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}

	public void saveAuthor() {
		if(currentScenario == null) return;	
		currentScenario.getDescription().setAuthor(contentComposite.vAuthor.getText());
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}
	
	public void saveDifficulty() {
		if(currentScenario == null) return;		
		currentScenario.getDescription().setDifficulty(contentComposite.vDifficulty.getSelection());
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}
	
	public void saveCreated() {
		if(currentScenario == null) return;		
		currentScenario.getDescription().setCreated(contentComposite.vCreated.getSelection().getTime());
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}
	
	public void saveDescription() {
		if(currentScenario == null) return;		
		currentScenario.getDescription().setDescriptionText(contentComposite.vDescription.getText());
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}

}
