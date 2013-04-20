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

package com.jeroenjanssens.presto.views.scenario.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackTreeViewerToolBar {

	private Display display;
	private TrackTreeViewer trackTreeViewer;

	private ToolItem upItem;
	private ToolItem downItem;
	private ToolItem leftItem;
	private ToolItem rightItem;
	private ToolItem renameItem;
	private ToolItem deleteItem;

	private Image newFolderIcon;
	private Image newTrackIcon;
	private Image renameIcon;
	private Image deleteIcon;
	private Image upIcon;
	private Image downIcon;
	private Image rightIcon;
	private Image leftIcon;

	public TrackTreeViewerToolBar(final ScenarioEditor scenarioEditor, Composite parent) {

		this.display = parent.getDisplay();
		this.trackTreeViewer = scenarioEditor.getTrackTreeViewer();

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 250;
		toolBar.setLayoutData(gridData);

		newFolderIcon = Activator.getImageDescriptor("icons/new_folder.png").createImage(display);
		newTrackIcon = Activator.getImageDescriptor("icons/new_track.png").createImage(display);
		renameIcon = Activator.getImageDescriptor("icons/rename1.gif").createImage(display);
		deleteIcon = Activator.getImageDescriptor("icons/trash.gif").createImage(display);

		//up down left right
		upIcon = Activator.getImageDescriptor("icons/up.gif").createImage(display);
		downIcon = Activator.getImageDescriptor("icons/down.gif").createImage(display);
		rightIcon = Activator.getImageDescriptor("icons/nav_forward.gif").createImage(display);
		leftIcon = Activator.getImageDescriptor("icons/nav_backward.gif").createImage(display);

		ToolItem newFolderItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem newTrackItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem sep1 = new ToolItem(toolBar, SWT.SEPARATOR);
		renameItem = new ToolItem (toolBar, SWT.PUSH);
		deleteItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);
		upItem = new ToolItem (toolBar, SWT.PUSH);
		downItem = new ToolItem (toolBar, SWT.PUSH);
		leftItem = new ToolItem (toolBar, SWT.PUSH);
		rightItem = new ToolItem (toolBar, SWT.PUSH);

		newFolderItem.setImage (newFolderIcon);
		newTrackItem.setImage (newTrackIcon);
		renameItem.setImage(renameIcon);
		deleteItem.setImage (deleteIcon);

		upItem.setImage (upIcon);
		downItem.setImage (downIcon);
		leftItem.setImage (leftIcon);
		rightItem.setImage (rightIcon);

		newFolderItem.setToolTipText("New Folder");
		newTrackItem.setToolTipText("New Track");
		renameItem.setToolTipText("Rename");
		deleteItem.setToolTipText("Delete");

		upItem.setToolTipText("Move Up");
		downItem.setToolTipText("Move Down");
		leftItem.setToolTipText("Move Out Folder");
		rightItem.setToolTipText("Move In Folder");

		renameItem.setEnabled(false);
		deleteItem.setEnabled(false);
		upItem.setEnabled(false);
		downItem.setEnabled(false);
		leftItem.setEnabled(false);
		rightItem.setEnabled(false);

		newTrackItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.addNewTrack();
				scenarioEditor.setChanged(true);
			}
		});


		newFolderItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.addNewFolder();
				scenarioEditor.setChanged(true);
			}
		});

		renameItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.renameSelected();
				scenarioEditor.setChanged(true);
			}
		});

		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.removeSelected();
				scenarioEditor.setChanged(true);
			}
		});


		upItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_UP, true);
				scenarioEditor.setChanged(true);
			}
		});

		downItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_DOWN, true);
				scenarioEditor.setChanged(true);
			}
		});
		leftItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_LEFT, true);
				scenarioEditor.setChanged(true);
			}
		});

		rightItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_RIGHT, true);
				scenarioEditor.setChanged(true);
			}
		});
		toolBar.pack ();
		
		
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				//System.out.println("Disposing images of Track Tree Toolbar!");
				newFolderIcon.dispose();
				newTrackIcon.dispose();
				renameIcon.dispose();
				deleteIcon.dispose();
				upIcon.dispose();
				downIcon.dispose();
				rightIcon.dispose();
				leftIcon.dispose();
			}
		});
	}

	public void updateButtons() {

		renameItem.setEnabled(trackTreeViewer.mayRename());
		deleteItem.setEnabled(trackTreeViewer.mayDelete());

		upItem.setEnabled(trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_UP, false));
		downItem.setEnabled(trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_DOWN, false));
		leftItem.setEnabled(trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_LEFT, false));
		rightItem.setEnabled(trackTreeViewer.moveSelected(TrackTreeViewer.MOVE_RIGHT, false));
	}
}
