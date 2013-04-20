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

package com.jeroenjanssens.presto.views.earth;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.examples.ClickAndGoSelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.view.FlyToOrbitViewStateIterator;
import gov.nasa.worldwind.view.OrbitView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.TreeModel;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.views.earth.layers.BackgroundLayer;
import com.jeroenjanssens.presto.views.earth.layers.DateTimeLayer;
import com.jeroenjanssens.presto.views.earth.layers.LockedLayer;
import com.jeroenjanssens.presto.views.earth.layers.ToolLayer;
import com.jeroenjanssens.presto.views.earth.layers.TrackLayer;
import com.jeroenjanssens.presto.views.earth.layers.VesselLayer;
import com.jeroenjanssens.presto.views.earth.layers.WaypointLayer;
import com.jeroenjanssens.presto.views.earth.tools.EarthViewToolBar;
import com.jeroenjanssens.presto.views.earth.tools.ITool;
import com.jeroenjanssens.presto.views.earth.tools.PrestoInputHandler;
import com.jeroenjanssens.presto.views.menu.MenuManager;
import com.jeroenjanssens.presto.views.properties.GeneralPropertiesView;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class EarthView extends ViewPart implements ISelectionListener, IPartListener {

	public static final String ID = "com.jeroenjanssens.presto.views.earthview";
	public static final int LAYER_TRACKS = 12;
	public static final int LAYER_WAYPOINTS = 13;

	private WorldWindowGLCanvas wwd = Activator.getDefault().getWorldWindowGLCanvas();	
	private EarthViewToolBar earthViewToolBar;

	private StatusLineContributionItem statusItemLatLon;
	private Display display;

	private ScenarioEditor currentScenarioEditor;
	private ArrayList<Track> currentSelectedTracks;
	private CopyOnWriteArrayList<Waypoint> currentSelectedWaypoints;

	private LayerList layerList;
	private ToolLayer toolLayer;
	private PrestoInputHandler prestoInputHandler;

	private BackgroundLayer backgroundLayer = new BackgroundLayer(this);

	private GeneralPropertiesView propertiesView;

	private EarthView thisEarthView = this;
	private MenuManager menuManager;

	private double vesselSize;

	public double getVesselSize() {
		return vesselSize;
	}

	private javax.swing.Timer locationTimer = new javax.swing.Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			if (display.isDisposed()) return;
			EarthView.this.display.asyncExec(new Runnable () {
				public void run () {
					ITool tool = EarthView.this.getEarthViewToolBar().getCurrentTool();
					if(tool == null) return;
					//tool.updateCursor();
					String message = "Off Globe";
					Position position = wwd.getCurrentPosition();
					if (position != null) {
						message = String.format("Lat: %7.4f\u00B0, Lon: %7.4f\u00B0", position.getLatitude().getDegrees(), position.getLongitude().getDegrees());
					}
					EarthView.this.statusItemLatLon.setText(message);
				}
			});
		}
	});



	public EarthView() {


		try {
			System.setProperty("sun.awt.noerasebackground","true");
		} catch (NoSuchMethodError error) {}


		toolLayer = new ToolLayer(this);
		prestoInputHandler = new PrestoInputHandler();
		prestoInputHandler.setEarthView(this);
		initWorldWindLayerModel();
		currentSelectedTracks = new ArrayList<Track>();
		currentSelectedWaypoints = new CopyOnWriteArrayList<Waypoint>();
	}

	@Override
	public void createPartControl(Composite parent) {
		currentScenarioEditor = null;
		this.getSite().getPage().addPartListener(this);

		//General Layout
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;

		//ToolBar
		earthViewToolBar = new EarthViewToolBar(this, parent);
		//earthViewToolBar.getToolBar().setEnabled(false);
		//AWT_SWT Earth
		Composite top = new Composite(parent, SWT.EMBEDDED | SWT.BORDER);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		java.awt.Frame worldFrame = SWT_AWT.new_Frame(top);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());

		layerList = wwd.getModel().getLayers();
		
		layerList.add(new TrackLayer(this));
		layerList.add(new WaypointLayer(this));
		layerList.add(new VesselLayer(this));
		layerList.add(new DateTimeLayer(this));
		layerList.add(backgroundLayer);
		//layerList.add(new DemonstrateGapLayer());
		layerList.add(toolLayer);

		panel.add(wwd, BorderLayout.CENTER);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		worldFrame.add(panel);

		//StatusBar
		statusItemLatLon = new StatusLineContributionItem("LoggedInStatus");
		statusItemLatLon.setText("Off Globe");
		this.getStatusLineManager().add(statusItemLatLon);

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {

			}
		});

		display = this.getSite().getWorkbenchWindow().getShell().getDisplay();
		locationTimer.start();

		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		for(Layer l : layerList) {
			if(!(l instanceof LockedLayer)) {
				LayerToggleAction lta = new LayerToggleAction(l);
				mgr.add(lta);
			}
		}

		this.getSite().getPage().addSelectionListener(this);

		// Setup a select listener for the worldmap click-and-go feature
		this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWWD(), WorldMapLayer.class));
		earthViewToolBar.selectTool(EarthViewToolBar.TOOL_NAVIGATION);

		reset();
		findPropertiesView();

		menuManager = new MenuManager(this);

		
	}

	public Display getDisplay() {
		return display;
	}

	private void initWorldWindLayerModel() {
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		m.setGlobe(new PrestoEarth());
		wwd.setModel(m);
		//System.out.println("Model: " + wwd.getModel().getClass().getCanonicalName());
		//System.out.println("Globe: " + wwd.getModel().getGlobe().getClass().getCanonicalName());
		wwd.setInputHandler(prestoInputHandler);
	}

	@Override
	public void setFocus() {
		wwd.requestFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
	}


	public IStatusLineManager getStatusLineManager() {
		return this.getViewSite().getActionBars().getStatusLineManager();
	}

	public WorldWindowGLCanvas getWWD() {
		return this.wwd;
	}

	public void reset() {
		OrbitView view = (OrbitView)wwd.getView();
		Globe globe = wwd.getModel().getGlobe();
		//view.applyStateIterator(FlyToOrbitViewStateIterator.createPanToIterator(view, globe, Position.fromDegrees(54.0, 4.0, 0.0), view.getHeading(), view.getPitch(), view.getZoom()));
		//view.applyStateIterator(FlyToOrbitViewStateIterator.createPanToIterator(globe, new Position(view.getCurrentEyePosition().getLatLon(), 0.0), Position.fromDegrees(54.0, 4.0, 0.0), view.getHeading(), Angle.ZERO, view.getPitch(), Angle.ZERO, view.getZoom(), 1000000, 2000, false));

		view.applyStateIterator(FlyToOrbitViewStateIterator.createPanToIterator(view, globe, Position.fromDegrees(53.0, 4.0, 0.0), Angle.ZERO, Angle.ZERO, 1000000));
		//ScheduledOrbitViewStateIterator

		//pitch, zoom, heading 
		//centerposition
		//eyeposition


		//.createCenterHeadingPitchZoomIterator
	}
	
	public void flyTo(Position position) {
		OrbitView view = (OrbitView)wwd.getView();
		Globe globe = wwd.getModel().getGlobe();
		view.applyStateIterator(FlyToOrbitViewStateIterator.createPanToIterator(view, globe, position, Angle.ZERO, Angle.ZERO, 100000));
	}

	public Globe getGlobe() {
		return this.getWWD().getModel().getGlobe();
	}


	public void setScenario(Scenario scenario) {

	}

	public ScenarioEditor getCurrentScenarioEditor() {
		return this.currentScenarioEditor;
	}

	public ArrayList<Track> getCurrentSelectedTracks() {
		return this.currentSelectedTracks;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;

		currentSelectedTracks.clear();
		currentSelectedWaypoints.clear();
		for(Object o : ssel.toList()) {
			if(o instanceof Track) {
				addTrackToSelection((Track) o);
			} else if(o instanceof Folder) {
				addFolderToSelection((Folder) o);
			}
		}

		getPropertiesView().updateTrackSelection(earthViewToolBar.getEarthView().getCurrentSelectedTracks());
		this.getCurrentScenarioEditor().getTracksTreeToolBar().updateButtons();
		wwd.redraw();
	}

	private void addTrackToSelection(Track t) {
		if(!currentSelectedTracks.contains(t)) {
			currentSelectedTracks.add(t); 
		}
	}

	private void addFolderToSelection(Folder f) {
		ArrayList<TreeModel> children = f.getChildren();

		for(TreeModel c : children) {
			if(c instanceof Track) {
				addTrackToSelection((Track) c);
			} else if(c instanceof Folder) {
				addFolderToSelection((Folder) c);
			}
		}
	}

	public void partActivated(IWorkbenchPart part) {
		//System.out.println("Activated " + part.getTitle());

		if(part instanceof ScenarioEditor) {
			currentSelectedWaypoints.clear();
			currentScenarioEditor = (ScenarioEditor) part;
			currentScenarioEditor.registerEarthView(this);
			Activator.setTitle("Presto - " + currentScenarioEditor.getScenario().getName());

			/*
			System.out.println("---Display:----");
			for(Track t : currentScenarioEditor.getScenario().getRootFolder().getAllTracks(true, false)) {
				System.out.println(t.getName());
			}
			System.out.println("--------------");
			 */
			wwd.redraw();


			propertiesView.updateScenarioSelection(currentScenarioEditor.getScenario());
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		//System.out.println("Brought To Top " + part.getTitle());
	}

	public void partClosed(IWorkbenchPart part) {
		//System.out.println("Closed " + part.getTitle());
		if(part instanceof ScenarioEditor) {
			if(part.equals(currentScenarioEditor)) {
				Activator.setTitle("Presto");
				currentScenarioEditor = null;
				propertiesView.updateScenarioSelection(null);
			}
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		//System.out.println("Deactivated " + part.getTitle());
		if(part instanceof EarthView) {
			getEarthViewToolBar().setSpaceBarDown(false);
		}
	}

	public void partOpened(IWorkbenchPart part) {
		//System.out.println("Opened " + part.getTitle());
	}

	public Layer getLayer(int layerIndex) {
		return layerList.get(layerIndex);
	}

	public PrestoInputHandler getPrestoInputHandler() {
		return this.prestoInputHandler;
	}

	public EarthViewToolBar getEarthViewToolBar() {
		return earthViewToolBar;
	}

	public ToolLayer getToolLayer() {
		return toolLayer;
	}

	public CopyOnWriteArrayList<Waypoint> getCurrentSelectedWaypoints() {
		return currentSelectedWaypoints;
	}

	public void setCurrentSelectedWaypoints(CopyOnWriteArrayList<Waypoint> currentSelectedWaypoints) {
		this.currentSelectedWaypoints = currentSelectedWaypoints;
	}

	private void findPropertiesView() {
		IViewReference[] views = this.getViewSite().getWorkbenchWindow().getActivePage().getViewReferences();
		for(int i=0; i<views.length; i++){
			//System.out.println("View:" + views[i].getId());
			if("com.jeroenjanssens.presto.views.properties.scenariopropertiesview".equals(views[i].getId())){
				propertiesView = (GeneralPropertiesView) views[i].getView(true);
				propertiesView.registerEarthView(thisEarthView);
				break;
			}
		}
	}

	public GeneralPropertiesView getPropertiesView() {
		return propertiesView;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public void setCurrentSelectedTracks(ArrayList<Track> selectedTracks) {
		this.currentSelectedTracks = selectedTracks;
	}

	public void setVesselSize(double zoom) {
		this.vesselSize = zoom;
	}

	public BackgroundLayer getBackgroundLayer() {
		return this.backgroundLayer;
	}
}