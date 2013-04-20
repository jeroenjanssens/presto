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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.sailing.ais.AISNavigationStatus;
import com.jeroenjanssens.presto.sailing.ais.AISParameter;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackPropertiesComposite extends AbstractPropertiesComposite {

	private JiglooTrackComposite contentComposite = null;
	private ArrayList<Track> selectedTracks;
	public ArrayList<Track> getSelectedTracks() {
		return selectedTracks;
	}

	private GeneralPropertiesView propertiesView;

	public TrackPropertiesComposite(Composite parent, int style, GeneralPropertiesView propertiesView) {
		super(parent, style, propertiesView);
		this.propertiesView = propertiesView;
		this.title = "Track";

		this.setAlwaysShowScrollBars(true);
		this.setLayout(new GridLayout());

		GridData group1LData = new GridData();
		group1LData.verticalAlignment = GridData.FILL;
		group1LData.grabExcessHorizontalSpace = true;
		group1LData.grabExcessVerticalSpace = true;
		group1LData.horizontalAlignment = GridData.FILL;

		this.setLayoutData(group1LData);
		contentComposite = new JiglooTrackComposite(this, SWT.NONE);
		contentComposite.setLayoutData(group1LData);

		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		this.setContent(contentComposite);
		this.setSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setMinHeight(contentComposite.getBounds().height);
	}

	public GeneralPropertiesView getPropertiesView() {
		return propertiesView;
	}

	public void setSelectionTrack(Track track) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		tracks.add(track);
		propertiesView.updateTrackSelection(tracks);
	}

	public void setSelection(ArrayList<Track> tracks) {
		if(tracks == null) tracks = new ArrayList<Track>();
		selectedTracks = tracks;

		clearValues();

		if(selectedTracks.isEmpty()) {
			contentComposite.enable(false);
			updateSelectionText();
		} else if(selectedTracks.size() == 1) {
			contentComposite.enable(true);
			updateSelectionText();
			Track t = selectedTracks.get(0);

			loadName(t);
			loadColor(t);
			loadVisible(t);
			loadLocked(t);
			
			loadETS(t);
			loadETA(t);
			loadAISValues(tracks);
		} else {
			contentComposite.enable(true);
			updateSelectionText();
			boolean vNameSame = true;
			boolean vColorSame = true;
			boolean vVisibleSame = true;
			boolean vLockedSame = true;
			for(int i = 0; i < selectedTracks.size()-1; i++) {
				if(!selectedTracks.get(i).getName().equals(selectedTracks.get(i+1).getName())) vNameSame = false;
				if(!selectedTracks.get(i).getColor().equals(selectedTracks.get(i+1).getColor())) vColorSame = false;
				if(selectedTracks.get(i).isVisible() != selectedTracks.get(i+1).isVisible()) vVisibleSame = false;
				if(selectedTracks.get(i).isLocked() != selectedTracks.get(i+1).isLocked()) vLockedSame = false;
			}

			if(vNameSame) loadName(selectedTracks.get(0));
			if(vColorSame) loadColor(selectedTracks.get(0));
			if(vVisibleSame) loadVisible(selectedTracks.get(0));
			if(vLockedSame) loadLocked(selectedTracks.get(0));
			
			loadETS(selectedTracks.get(0));
			loadETA(selectedTracks.get(0));
			loadAISValues(tracks);
		}
		this.propertiesView.getEarthView().setCurrentSelectedTracks(selectedTracks);
		this.propertiesView.updateEarthView();
	}

	private void updateSelectionText() {
		
		contentComposite.vSelection.removeAll();
		contentComposite.vSelection.add("<no track>");
		contentComposite.vSelection.add("<all tracks>");
		contentComposite.vSelection.add("<all visible tracks>");
		contentComposite.vSelection.add("<all invisible tracks>");
		contentComposite.vSelection.add("<all unlocked tracks>");
		contentComposite.vSelection.add("<all locked tracks>");

        ArrayList<Track> allTracks = propertiesView.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder().getAllTracks(false, false);
		
		Collections.sort(allTracks);
		
		for(Track t : allTracks) {
			contentComposite.vSelection.add(t.getName());
		}
		
		if(selectedTracks.isEmpty()) {
			contentComposite.vSelection.setText("<no tracks selected>");	
		} else if(selectedTracks.size() == 1) {
			contentComposite.vSelection.select(allTracks.indexOf(selectedTracks.get(0))+6);
		} else {
			contentComposite.vSelection.setText("<" + selectedTracks.size() + " tracks selected>");
		}
	}

	public void restore() {
		setSelection(selectedTracks);
	}

	public void clearValues() {
		contentComposite.vName.setText("");
		//contentComposite.vColor.setBackground(new Color(propertiesView.getEarthView().getDisplay(), 255, 255, 255));
		contentComposite.vColor.setBackground(null);
		contentComposite.vColor.setText("");
		contentComposite.vVisible.setSelection(false);
		contentComposite.vLocked.setSelection(false);
		
		contentComposite.vETS.setSelection(null);
		contentComposite.vETA.setSelection(null);

		for(AISParameter p : AISParameter.values()) {
			contentComposite.AISControls.get(p).setValue(null);
		}
	}		

	private void loadName(Track t) {
		contentComposite.vName.setText(t.getName());
	}

	private void loadColor(Track t) {
		contentComposite.vColor.setBackground(t.getColor());
		contentComposite.vColor.setText(getColorString(t.getColor()));
	}
	
	private void loadVisible(Track t) {
		contentComposite.vVisible.setSelection(t.isVisible());
	}
	
	private void loadLocked(Track t) {
		contentComposite.vLocked.setSelection(t.isLocked());
	}

	private void loadETS(Track t) {
		contentComposite.vETS.setSelection(new Date(t.getTime()));
	}	

	private void loadETA(Track t) {
		contentComposite.vETA.setSelection(new Date(t.getLastWaypoint().getTime()));
	}

	public void loadAISValues(ArrayList<Track> tracks) {
		for(AISParameter p : AISParameter.values()) {
			loadAISValue(tracks, p);
		}
	}

	public void loadAISValue(ArrayList<Track> tracks, AISParameter aisParameter) {

		Object value = tracks.get(0).getDefaultAISValues().get(aisParameter);
		if(value == null) return;

		if(aisParameter.getType().equals(String.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (String)value);	
			String newValue = (String)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((String)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} else if(aisParameter.getType().equals(Boolean.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (Integer)value);
			Boolean newValue = (Boolean)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((Boolean)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} else if(aisParameter.getType().equals(Double.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (Double)value);
			Double newValue = (Double)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((Double)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} else if(aisParameter.getType().equals(Integer.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (Integer)value);
			Integer newValue = (Integer)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((Integer)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (Integer)value);
			Integer newValue = (Integer)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((Integer)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} else if(aisParameter.getType().equals(CDateTime.class)) {
			//System.out.println("Load AISVAlue " + aisParameter.getTextName() + " -> " + (Long)value);
			Long newValue = (Long)value;
			boolean same = true; 
			for(int i = 0; i < tracks.size(); i++) if(!((Long)tracks.get(i).getDefaultAISValues().get(aisParameter)).equals(newValue)) same = false;
			if(same) contentComposite.AISControls.get(aisParameter).setValue(newValue);
		} 
	}


	public void saveAISValue(AISParameter aisParameter, Object value) {
		if(selectedTracks == null) return;
		if(selectedTracks.isEmpty()) return;

		Object newValue = null;

		try {

			if(aisParameter.getType().equals(String.class)) {
				newValue = value;
			} else if(aisParameter.getType().equals(Boolean.class)) {
				newValue = value;
			} else if(aisParameter.getType().equals(Double.class)) {
				if(value != "") newValue = Double.parseDouble((String)value);
			} else if(aisParameter.getType().equals(Integer.class)) {
				if(value != "") newValue = Integer.parseInt((String)value);
			} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
				newValue = value;
			} else if(aisParameter.getType().equals(CDateTime.class)) {
				newValue = value;
			} else {
				newValue = value;
			}

			//if(newValue != null) {
				for(Track t : selectedTracks) {
					t.getDefaultAISValues().put(aisParameter, newValue);
				}
			//}

		} catch (NumberFormatException e) {
			//System.out.println("Illegal value! Not saving value.");
			if(aisParameter.getType().equals(Integer.class)) {
				MessageDialog.openError(this.getShell(), "Error", "Please enter an integer value (e.g., '0', '13').");
			} else {
				MessageDialog.openError(this.getShell(), "Error", "Please enter a real value (e.g., '0.001', '3.1415').");
			}
		} catch (Exception e2) {
			//System.out.println("Something else went wrong! Not saving value.");

		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();

	}

	public void saveName() {		
		if(selectedTracks == null) return;

		for(Track track : selectedTracks) {
			track.setName(contentComposite.vName.getText());
		}

		propertiesView.updateTrackTreeViewer();
		propertiesView.updateTimeLine();
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}

	public void saveColor() {		
		if(selectedTracks == null) return;

		Color newColor = contentComposite.vColor.getBackground();

		if(newColor != null) {
			for(Track track : selectedTracks) {
				track.setColor(newColor);
			}
		}
		propertiesView.updateTrackTreeViewer();
		propertiesView.updateTimeLine();
		propertiesView.updateEarthView();
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}
	
	public void saveVisible() {
		if(selectedTracks == null) return;
		
		for(Track track : selectedTracks) {
			track.setVisible(contentComposite.vVisible.getSelection());
		}
		propertiesView.updateTrackTreeViewer();
		propertiesView.updateTimeLine();
		propertiesView.updateEarthView();
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}
	
	public void saveLocked() {
		if(selectedTracks == null) return;
		
		for(Track track : selectedTracks) {
			track.setLocked(contentComposite.vLocked.getSelection());
		}
		propertiesView.updateTrackTreeViewer();
		propertiesView.updateTimeLine();
		propertiesView.updateEarthView();
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		restore();
	}

	public void saveETS() {
		if(selectedTracks == null) return;

		for(Track track : selectedTracks) {
			track.setTime(contentComposite.vETS.getSelection().getTime());
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}

	public void saveETA() {
		if(selectedTracks == null) return;

		long newEndTime = contentComposite.vETA.getSelection().getTime();

		for(Track track : selectedTracks) {
			long oldEndTime = track.getLastWaypoint().getTime();
			long difference = newEndTime - oldEndTime;
			long oldTrackTime = track.getTime();
			long newTrackTime = oldTrackTime + difference;
			track.setTime(newTrackTime);
		}
		propertiesView.getEarthView().getCurrentScenarioEditor().setChanged(true);
		updateAll();
	}


	private void updateTracks() {
		for(Track track : selectedTracks) {
			track.update(this.propertiesView.getEarthView());
		}
	}

	public void updateAll() {
		propertiesView.getEarthView().getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateTracks();
				propertiesView.getEarthView().getWWD().redraw();
				propertiesView.getEarthView().getCurrentScenarioEditor().getTimeLine().doRedraw();
			}
		});
	}

	public String getColorString(Color color) {
		if(color == null) return "";
		return "(R = " + color.getRed() + ", G = " + color.getGreen() + ", B = " + color.getBlue() + ")";
	}

	public void selectFromCombo() {

		/*
		contentComposite.vSelection.add("<no track>");
		contentComposite.vSelection.add("<all tracks>");
		contentComposite.vSelection.add("<all visible tracks>");
		contentComposite.vSelection.add("<all invisible tracks>");
		contentComposite.vSelection.add("<all unlocked tracks>");
		contentComposite.vSelection.add("<all locked tracks>");
		 */	

		int index = contentComposite.vSelection.getSelectionIndex();

		ArrayList<Track> allTracks = propertiesView.getEarthView().getCurrentScenarioEditor().getScenario().getRootFolder().getAllTracks(false, false);
        Collections.sort(allTracks);
		
        ArrayList<Track> sTracks = new ArrayList<Track>();
        
		switch(index) {
		case -1:
		case 0:
			this.propertiesView.updateTrackSelection(null);
			break;
		case 1:
			//System.out.println("Select all tracks");
			this.propertiesView.updateTrackSelection(allTracks);
			break;
		case 2:
			for(Track t : allTracks) if(t.isVisible()) sTracks.add(t);
			this.propertiesView.updateTrackSelection(sTracks);
			break;
		case 3:
			for(Track t : allTracks) if(!t.isVisible()) sTracks.add(t);
			this.propertiesView.updateTrackSelection(sTracks);
			break;
		case 4:
			for(Track t : allTracks) if(!t.isLocked()) sTracks.add(t);
			this.propertiesView.updateTrackSelection(sTracks);
			break;
		case 5:
			for(Track t : allTracks) if(t.isLocked()) sTracks.add(t);
			this.propertiesView.updateTrackSelection(sTracks);
			break;
		default:
		this.setSelectionTrack(allTracks.get(index - 6));
		break;
		}
	}
}
