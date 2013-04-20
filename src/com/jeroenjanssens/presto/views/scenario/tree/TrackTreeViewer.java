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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.TreeModel;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;


/**
 * Insert the type's description here.
 * @see ViewPart
 */

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackTreeViewer {
	protected TreeViewer treeViewer;
	protected TrackTreeViewerLabelProvider labelProvider;

	protected Action onlyBoardGamesAction, atLeatThreeItems;
	protected Action booksBoxesGamesAction, noArticleAction;
	protected Action addBookAction, removeAction;
	
	private ScenarioEditor scenarioEditor;

	private Runnable timer;

	private ArrayList<Object> previousTreeObjects = null;

	private Shell shell;
	
	private TrackTreeViewer thisTrackTreeViewer;
	
	public static final int MOVE_UP = 1;
	public static final int MOVE_DOWN = 2;
	public static final int MOVE_LEFT = 3;
	public static final int MOVE_RIGHT = 4;
	
	public TrackTreeViewer(final ScenarioEditor scenarioEditor, Composite parent) {
		this.scenarioEditor = scenarioEditor;
		this.thisTrackTreeViewer = this;
		shell = parent.getShell();
		treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);

		previousTreeObjects = new ArrayList<Object>();
		treeViewer.setContentProvider(new TrackTreeViewerContentProvider());
		labelProvider = new TrackTreeViewerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		/*
		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(treeViewer.getTree()) });
		treeViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				return "Column " + property + " => " + element.toString();
			}

			public void modify(Object element, String property, Object value) {
				
			}
		}
		);
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(treeViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
			
				return event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
						|| event.keyCode == SWT.F2
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		
		TreeViewerEditor.create(treeViewer, actSupport, ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK);
		
		
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setMoveable(false);
		column.getColumn().setText("Column 1");
		column.setLabelProvider(new TrackTreeViewerCellLabelProvider());
		*/
		
		
		treeViewer.setUseHashlookup(true);

		// layout the tree viewer below the text field
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.widthHint = 250;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);

		// Create menu, toolbars, filters, sorters.
		
		createActions();
		createMenus();
		createToolbar();

		treeViewer.getTree().setLinesVisible(true);
		treeViewer.setInput(scenarioEditor.getScenario().getRootFolder());
		treeViewer.expandAll();
		
		Listener l = new Listener() {
		     public void handleEvent(Event event) {
		    	 scenarioEditor.getTimeLine().doRedraw();
		     }
		};
		treeViewer.getTree().addListener(SWT.Selection, l);

		treeViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				if(treeViewer.getTree().getItem(new Point(1, event.y)) == null) {
		    		 if(treeViewer.getTree().getItem(new Point(20, event.y)) == null) {
		    			 treeViewer.setSelection(null);
		    			 scenarioEditor.getTimeLine().doRedraw();
		    		 }
		    	 }
				
				if(event.button == 3) {
					TreeModel tm = thisTrackTreeViewer.getItem(event.x, event.y);
					if(tm instanceof Track) {
						scenarioEditor.getEarthView().getMenuManager().getTrackMenu().showMenu((Track) tm, treeViewer.getTree().toDisplay(event.x, event.y));
					}
				}
			}
		});
		
		timer = new Runnable() {
			public void run() {
				if(treeViewer.getTree().isDisposed()) return;
				ArrayList<Object> treeObjects = new ArrayList<Object>();
				
				for(int y = 1; y < treeViewer.getTree().getBounds().height; y+=treeViewer.getTree().getItemHeight()) {
					Object o = treeViewer.getTree().getItem(new Point(1, y));
					if(o != null) {
						treeObjects.add(o);
					} else {
						o = treeViewer.getTree().getItem(new Point(20, y));
						if(o != null) {
							treeObjects.add(o);
						} else {
							break;
						}
					}
				}

				if(previousTreeObjects.size() != treeObjects.size()) {
					previousTreeObjects = treeObjects;
					scenarioEditor.getTimeLine().doRedraw(previousTreeObjects);
				} else {
					for(int i = 0; i < previousTreeObjects.size(); i++) {
						if(!previousTreeObjects.get(i).equals(treeObjects.get(i))) {
							previousTreeObjects = treeObjects;
							scenarioEditor.getTimeLine().doRedraw(previousTreeObjects);
							break;
						}
					}
				}

				Display.getDefault().timerExec(100, timer);
			}
		};
		Display.getDefault().timerExec(100, timer);
	}

	public TreeModel getItem(int x, int y) {
		TreeItem item = null;
		item = treeViewer.getTree().getItem(new Point(x, y));
		if(item == null) return null;
		return (TreeModel) item.getData();
	}

	protected void createActions() {
		addBookAction = new Action("Add Book") {
			@Override
			public void run() {
				addNewFolder();
			}			
		};
		addBookAction.setToolTipText("Add a New Book");
		//addBookAction.setImageDescriptor(Activator.getImageDescriptor("icons/newBook.gif"));

		removeAction = new Action("Delete") {
			@Override
			public void run() {
				removeSelected();
			}			
		};
		removeAction.setToolTipText("Delete");
		removeAction.setImageDescriptor(Activator.getImageDescriptor("icons/remove.gif"));		
	}

	/** Add a new folder to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	public void addNewFolder() {
		Folder receivingFolder;
		if (treeViewer.getSelection().isEmpty()) {
			receivingFolder = scenarioEditor.getScenario().getRootFolder();
		} else {
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			TreeModel selectedDomainObject = (TreeModel) selection.getFirstElement();
			if (!(selectedDomainObject instanceof Folder)) {
				receivingFolder = selectedDomainObject.getParent();
			} else {
				receivingFolder = (Folder) selectedDomainObject;
			}
		}
		receivingFolder.addFolder(new Folder("New Folder"));
		treeViewer.refresh();
	}
	
	public void addNewTrack() {
		Folder receivingFolder;
		if (treeViewer.getSelection().isEmpty()) {
			receivingFolder = scenarioEditor.getScenario().getRootFolder();
		} else {
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			TreeModel selectedDomainObject = (TreeModel) selection.getFirstElement();
			if (!(selectedDomainObject instanceof Folder)) {
				receivingFolder = selectedDomainObject.getParent();
			} else {
				receivingFolder = (Folder) selectedDomainObject;
			}
		}
		
		Track t = new Track("New Track");
		t.setTime(scenarioEditor.getTimeLine().getCursorTime());
		receivingFolder.addTrack(t);
		
		double lat = scenarioEditor.getEarthView().getWWD().getView().getCurrentEyePosition().getLatitude().degrees;
		double lon = scenarioEditor.getEarthView().getWWD().getView().getCurrentEyePosition().getLongitude().degrees;
		double ev = scenarioEditor.getEarthView().getWWD().getView().getCurrentEyePosition().getElevation();
		//System.out.println("zoom: " + ev);
		double multiplier = 2;  
		t.addWaypoint(new Waypoint(t, Math.random() + lat - (multiplier/2), Math.random() + lon - (multiplier/2), 20, 0));
		t.addWaypoint(new Waypoint(t, Math.random() + lat - (multiplier/2), Math.random() + lon - (multiplier/2), 20, 0));
		t.update(this.scenarioEditor.getEarthView());
		treeViewer.refresh();
		scenarioEditor.getEarthView().getWWD().redraw();
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	public void removeSelected() {
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		/* Tell the tree to not redraw until we finish
		 * removing all the selected children. */
		treeViewer.getTree().setRedraw(false);
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			TreeModel treeModel = (TreeModel) iterator.next();
			Folder parent = treeModel.getParent();
			parent.remove(treeModel);
		}
		treeViewer.getTree().setRedraw(true);
		treeViewer.refresh();
		
		for(Track t : scenarioEditor.getScenario().getRootFolder().getAllTracks(true, true)) {
			System.out.println(t.getName());
		}
		scenarioEditor.getTracksTreeToolBar().updateButtons();
		scenarioEditor.getEarthView().getPropertiesView().updateTrackSelection(null);
	}
	
	public void renameSelected() {
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		TreeModel treeModel = (TreeModel) selection.getFirstElement();
		RenameDialog rd = new RenameDialog(shell, SWT.NULL);
		String name = rd.open(treeModel);
		if(name != null) {
			treeModel.setName(name);
		}
		treeViewer.refresh();
		scenarioEditor.getEarthView().getPropertiesView().getTrackComposite().restore();
	}	
		
	protected void createMenus() {
		/*
		IMenuManager rootMenuManager = parent.getgetViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
		 */
	}


	protected void fillMenu(IMenuManager rootMenuManager) {
		IMenuManager filterSubmenu = new MenuManager("Filters");
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(onlyBoardGamesAction);
		filterSubmenu.add(atLeatThreeItems);

		IMenuManager sortSubmenu = new MenuManager("Sort By");
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(booksBoxesGamesAction);
		sortSubmenu.add(noArticleAction);
	}

	protected void createToolbar() {
		
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void dispose() {
		Display.getDefault().timerExec(-1, timer);
	}

	public boolean moveSelected(int direction, boolean perform) {
		
		if (treeViewer.getSelection().isEmpty()) {
			return false;
		}
		
		ArrayList<TreeModel> items = new ArrayList<TreeModel>();
		
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			TreeModel treeModel = (TreeModel) iterator.next();
			items.add(treeModel);
		}
		
		//get lowest level
		int lowestLevel = 1000000;
		for(TreeModel tm : items) {
			int level = tm.getLevel();
			if(level < lowestLevel) lowestLevel = level;
 		}
		//remove items that are on a higher level
		for(TreeModel tm : items) {
			if(tm.getLevel() > lowestLevel) return false;
		}
		
		//get lowest index of item
		int lowestIndex = 10000000;
		Folder folder  = items.get(0).getParent();
		for(TreeModel tm : items) {
			int index = folder.getChildren().indexOf(tm);
			if(index < lowestIndex) lowestIndex = index;
		}
		
		if(direction == TrackTreeViewer.MOVE_UP) {
			int newIndex = lowestIndex - 1;
			if(newIndex > -1) {
				if(perform) {
					treeViewer.getTree().setRedraw(false);
					folder.getChildren().removeAll(items);
					folder.getChildren().addAll(newIndex, items);
					treeViewer.getTree().setRedraw(true);
					treeViewer.refresh();
					scenarioEditor.getTracksTreeToolBar().updateButtons();
					forceUpdateTimeLine();
				} 
				return true;
			}
		} else if(direction == TrackTreeViewer.MOVE_DOWN) {
			int newIndex = lowestIndex + 1;
			if(newIndex <= (folder.getChildren().size() - items.size())) {
				if(perform) {
					treeViewer.getTree().setRedraw(false);
					folder.getChildren().removeAll(items);
					folder.getChildren().addAll(newIndex, items);
					treeViewer.getTree().setRedraw(true);
					treeViewer.refresh();
					scenarioEditor.getTracksTreeToolBar().updateButtons();
					forceUpdateTimeLine();
				} 
				return true;
			}
			
		} else if(direction == TrackTreeViewer.MOVE_LEFT) {
			if(lowestLevel > 1) {
				if(perform) {
					Folder newParent = folder.getParent();
					treeViewer.getTree().setRedraw(false);
					folder.getChildren().removeAll(items);
					newParent.getChildren().addAll(items);
					for(TreeModel tm : items) {
						tm.setParent(newParent);
					}
					treeViewer.getTree().setRedraw(true);
					treeViewer.refresh();
					scenarioEditor.getTracksTreeToolBar().updateButtons();
					forceUpdateTimeLine();
				}
				return true;
			}
		} else if(direction == TrackTreeViewer.MOVE_RIGHT) {
			//find closest folder to put the item into.
			Folder closestFolder = null;
			int distanceClosestFolder = 10000000;
			for(TreeModel tm : folder.getChildren()) {
				if(items.contains(tm)) continue;
				if(tm instanceof Folder) {
					int distance = Math.abs(folder.getChildren().indexOf(tm) - lowestIndex);
					if(distance < distanceClosestFolder) {
						distanceClosestFolder = distance;
						closestFolder = (Folder) tm;
					}
				}
			}
			
			if(closestFolder != null) {
				if(perform) {
					treeViewer.getTree().setRedraw(false);
					folder.getChildren().removeAll(items);
					closestFolder.getChildren().addAll(items);
					for(TreeModel tm : items) {
						tm.setParent(closestFolder);
					}
					treeViewer.getTree().setRedraw(true);
					treeViewer.refresh();
					scenarioEditor.getTracksTreeToolBar().updateButtons();
					forceUpdateTimeLine();
				}
				return true;
			}
		}
		
		return false;
	}
	
	public void forceUpdateTimeLine() {
		ArrayList<Object> treeObjects = new ArrayList<Object>();
		
		for(int y = 1; y < treeViewer.getTree().getBounds().height; y+=treeViewer.getTree().getItemHeight()) {
			Object o = treeViewer.getTree().getItem(new Point(1, y));
			if(o != null) {
				treeObjects.add(o);
			} else {
				o = treeViewer.getTree().getItem(new Point(20, y));
				if(o != null) {
					treeObjects.add(o);
				} else {
					break;
				}
			}
		}

		
		previousTreeObjects = treeObjects;
		scenarioEditor.getTimeLine().doRedraw(previousTreeObjects);
	}

	public boolean mayRename() {
		if (treeViewer.getSelection().isEmpty()) {
			return false;
		}
		
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		int i = 0;
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			TreeModel treeModel = (TreeModel) iterator.next();
			i++;
		}
		
		return (i == 1);
	}

	public boolean mayDelete() {
		if (treeViewer.getSelection().isEmpty()) {
			return false;
		}
		return true;
	}
	
	
}
