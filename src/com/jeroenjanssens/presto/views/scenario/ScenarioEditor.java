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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.views.earth.EarthView;
import com.jeroenjanssens.presto.views.scenario.timeline.Animator;
import com.jeroenjanssens.presto.views.scenario.timeline.TimeLine;
import com.jeroenjanssens.presto.views.scenario.timeline.TimeLineToolBar;
import com.jeroenjanssens.presto.views.scenario.tree.TrackTreeViewer;
import com.jeroenjanssens.presto.views.scenario.tree.TrackTreeViewerToolBar;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ScenarioEditor extends EditorPart implements ISelectionListener, FocusListener {

	public static final String ID = "com.jeroenjanssens.presto.editors.scenarioeditor"; //$NON-NLS-1$
	private TimeLine timeLine;
	private Animator animator;
	private TimeLineToolBar timeLineToolBar;
	private TrackTreeViewerToolBar tracksTreeToolBar;

	
	private TrackTreeViewer trackTreeViewer;

	private EarthView earthView;
	private boolean changed = false;
	
	@Override
	public void createPartControl(Composite parent) {
		Scenario scenario = this.getScenario();
 
		/*
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
 
		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name:");
 
		this.name = new Text(container, SWT.BORDER);
		this.name.setLayoutData(new GridData(229, SWT.DEFAULT));
		this.name.setText(s.getName());
 
		Label addressLabel = new Label(container, SWT.NONE);
		addressLabel.setText("Address:");
 
		this.address = new Text(container, SWT.BORDER);
		this.address.setLayoutData(new GridData(229, SWT.DEFAULT));
		this.address.setText(s.getText());
		
		
		*/
		
		
		//scenario.createModel();
		
		GridLayout gridLayout = new GridLayout();
			
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;

		parent.setLayout(gridLayout);

		/*
		tracksTree = new TracksTreeba(parent);
		tracksTree.setScenario(scenario);
		getSite().setSelectionProvider(tracksTree.getTreeViewer());
		 */
		
		Composite mainComposite = new Composite(parent, SWT.BORDER);
		GridLayout gridLayoutMain = new GridLayout();
		gridLayoutMain.numColumns = 2;
		gridLayoutMain.horizontalSpacing = 0;
		gridLayoutMain.verticalSpacing = 0;
		gridLayoutMain.marginWidth = 0;
		gridLayoutMain.marginHeight = 0;
		gridLayoutMain.marginTop = 0;
		gridLayoutMain.marginBottom = 0;
		mainComposite.setLayout(gridLayoutMain);
		GridData mainGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mainComposite.setLayoutData(mainGridData);
		
		
		
		Composite treeComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gridLayoutTree = new GridLayout();
		gridLayoutTree.numColumns = 1;
		gridLayoutTree.horizontalSpacing = 0;
		gridLayoutTree.verticalSpacing = 0;
		gridLayoutTree.marginWidth = 0;
		gridLayoutTree.marginHeight = 0;
		gridLayoutTree.marginTop = 0;
		gridLayoutTree.marginBottom = 0;
		treeComposite.setLayout(gridLayoutTree);
		GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, false, true);
		treeGridData.widthHint = 250;
		treeComposite.setLayoutData(treeGridData);
		
		final Display display = this.getSite().getShell().getDisplay();
		
		Canvas fill = new Canvas(treeComposite, SWT.NONE);
		fill.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				event.gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				event.gc.drawLine(0, 18, 250, 18);
				event.gc.drawText("Track name", 5, 3);
			}
		});
		
		GridData fillGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		fillGridData.widthHint = 250;
		fillGridData.heightHint = 19;
		fill.setLayoutData(fillGridData);
		
		trackTreeViewer = new TrackTreeViewer(this, treeComposite);
		
		timeLine = new TimeLine(this, mainComposite, SWT.DOUBLE_BUFFERED);
		GridData timeLineGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		timeLine.setLayoutData(timeLineGridData);
		
		Composite toolBarComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayoutToolBar = new GridLayout();
		gridLayoutToolBar.numColumns = 2;
		gridLayoutToolBar.horizontalSpacing = 0;
		gridLayoutToolBar.verticalSpacing = 0;
		gridLayoutToolBar.marginWidth = 0;
		gridLayoutToolBar.marginHeight = 0;
		gridLayoutToolBar.marginTop = 0;
		gridLayoutToolBar.marginBottom = 0;
		toolBarComposite.setLayout(gridLayoutMain);
		
		tracksTreeToolBar = new TrackTreeViewerToolBar(this, toolBarComposite);
		
		animator = new Animator(timeLine);
		timeLineToolBar = new TimeLineToolBar(this, toolBarComposite);
		toolBarComposite.pack();
		
		this.setPartName(scenario.getName());
		this.getSite().setSelectionProvider(trackTreeViewer.getTreeViewer());
		this.getSite().getPage().addSelectionListener(this);
	}
 
	public Scenario getScenario() {
		return ((ScenarioEditorInput) getEditorInput()).getScenario();
	}
	
	public TimeLine getTimeLine() {
		return timeLine;
	}
	
	public Animator getAnimator() {
		return animator;
	}
	
	
	public TrackTreeViewer getTrackTreeViewer() {
		return trackTreeViewer;
	}

	
	@Override
	public void doSave(IProgressMonitor monitor) {
		Scenario s = this.getScenario();
		if(Activator.getDefault().getScenarioManager().saveScenario(s)) {
			ScenarioEditorInput sei = new ScenarioEditorInput(s);
			setInput(sei);
			setPartName(s.getName());
			this.firePropertyChange(IWorkbenchPart.PROP_TITLE);
			this.firePropertyChange(IEditorPart.PROP_INPUT);
			setChanged(false);
			Activator.setTitle("Presto - " + s.getName());
			this.setFocus();
		}
	}

	@Override
	public void doSaveAs() {
		Scenario s = this.getScenario();
		if(Activator.getDefault().getScenarioManager().saveAsScenario(this.getScenario())) {
			ScenarioEditorInput sei = new ScenarioEditorInput(s);
			setInput(sei);
			setPartName(s.getName());
			this.firePropertyChange(IWorkbenchPart.PROP_TITLE);
			this.firePropertyChange(IEditorPart.PROP_INPUT);
			setChanged(false);
			Activator.setTitle("Presto - " + s.getName());
			this.setFocus();
		}
		//doSave(getProgressMonitor());
	}
 
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}
 
	@Override
	public boolean isDirty() {
		return changed;
	}
 
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
 
	@Override
	public void setFocus() {
		timeLine.setFocus();
		
		//this.getSite().getWorkbenchWindow().getWorkbench().getActiveWorkbenchWindow().getW
		//System.out.println("Scenario " + this.getScenario().getFileName() + " gets focus.");
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//IStructuredSelection ssel = (IStructuredSelection) selection;
		//trackTreeViewer.getTreeViewer().setSelection(selection, true);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
	}
	/*
	public void dispose() {
		System.out.println("I'm closing! " + this.getPartName());
		super.dispose();
	}
	*/
	
	public void registerEarthView(EarthView earthView) {
		this.earthView = earthView;
	}
	
	public EarthView getEarthView() {
		return this.earthView;
	}
	
	private IProgressMonitor getProgressMonitor() {

		IProgressMonitor pm = null;

		IStatusLineManager manager= getEditorSite().getActionBars().getStatusLineManager();
		if (manager != null)
			pm= manager.getProgressMonitor();
		return pm != null ? pm : new NullProgressMonitor();
	}

	@Override
	public void dispose() {
		Activator.getDefault().getScenarioManager().closeScenario(this.getScenario());
		super.dispose();
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	public boolean isChanged() {
		return changed;
	}
	
	public TimeLineToolBar getTimeLineToolBar() {
		return timeLineToolBar;
	}
	
	public TrackTreeViewerToolBar getTracksTreeToolBar() {
		return tracksTreeToolBar;
	}
	
	

}
