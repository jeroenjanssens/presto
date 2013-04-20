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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.model.ScenarioDescription;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.TrackTools;
import com.jeroenjanssens.presto.sailing.ais.AISNavigationStatus;
import com.jeroenjanssens.presto.sailing.ais.AISParameter;
import com.jeroenjanssens.presto.sailing.ais.AISState;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ScenarioManager {

	public static String NEW_SCENARIO = "$new" + System.getProperty("file.separator");

	private ArrayList<Scenario> scenarios;
	public static final String[] FILTER_NAMES = {"Presto Scenario Files (*.psml)", "All Files (*.*)"};
	public static final String[] FILTER_EXTS = {"*.psml", "*.*"};

	private int newCounter = 1;

	public ScenarioManager() {
		this.scenarios = new ArrayList<Scenario>();
	}

	public Scenario getScenario(String name) {
		for(Scenario s : scenarios) {
			if(s.isSame(name)) {
				return s;
			}
		}
		return null;
	}

	public Scenario createNewScenario() {
		Scenario newScenario = new Scenario(ScenarioManager.NEW_SCENARIO + "Untitled " + newCounter + ".psml");
		scenarios.add(newScenario);
		newCounter++;
		return newScenario;
	}

	public Scenario readScenarioFromFile(String fileName) {
		//create a new scenario
		//create model from xml
		//add to arraylist
		//return object
		Scenario scenario = new Scenario(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File xmlFile = new File(fileName);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			Element rootElement = document.getDocumentElement();
			System.out.println("Root element: " + rootElement.getTagName());

			NodeList rootChildren = rootElement.getChildNodes();

			for(int i = 0; i < rootChildren.getLength(); i++) {

				Node c = rootChildren.item(i);

				if(c.getNodeName().equals("folder")) {
					Folder rootFolder = parseFolder(c);
					if(rootFolder != null) {
						//System.out.println(rootFolder.toXML(1));

						scenario.setRootFolder(rootFolder);
					} else {
						return null;
					}
				} else if(c.getNodeName().equals("description")) {
					NodeList descriptionChildren = c.getChildNodes();
					for(int j = 0; j < descriptionChildren.getLength(); j++) {
						Node dc = descriptionChildren.item(j);
						if(dc.getNodeName().equals("name")) scenario.getDescription().setName(dc.getChildNodes().item(0).getNodeValue().trim());
						if(dc.getNodeName().equals("author")) scenario.getDescription().setAuthor(dc.getChildNodes().item(0).getNodeValue().trim());
						if(dc.getNodeName().equals("difficulty")) scenario.getDescription().setDifficulty(Integer.parseInt(dc.getChildNodes().item(0).getNodeValue())); 
						if(dc.getNodeName().equals("created")) scenario.getDescription().setCreated(Long.parseLong(dc.getChildNodes().item(0).getNodeValue()));
						if(dc.getNodeName().equals("text"))  scenario.getDescription().setDescriptionText(dc.getChildNodes().item(0).getNodeValue());			
					}
				}
			}

			scenarios.add(scenario);
			return scenario;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			InputSource inputSource  = new InputSource(new FileInputStream(new File(fileName)));


			//String name = xPath.evaluate("/scenario/name", inputSource);

			NodeList tracks = (NodeList) xPath.evaluate("/scenario/tracksfolder/track", inputSource, XPathConstants.NODESET);

			for(int i = 0; i < tracks.getLength(); i++) {
				System.out.println("I: " + i);
				System.out.println("TRACK: " + xPath.evaluate("/scenario/tracksfolder/track[" + (i+1) + "]/name", inputSource));
			}


			//System.out.println("XPath: " + name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */


		return null;
	}

	private ScenarioDescription parseScenarioDescription(Node c) {
		
		
		return null;
	}

	private Folder parseFolder(Node folderNode) {
		//System.out.println("parseFolder");
		String name = folderNode.getAttributes().getNamedItem("name").getNodeValue();
		//System.out.println("new Folder(" + name + ")");
		Folder folder = new Folder(name);	
		Color color = TrackTools.hexToColor(folderNode.getAttributes().getNamedItem("color").getNodeValue());
		boolean visible = Boolean.valueOf(folderNode.getAttributes().getNamedItem("visible").getNodeValue()).booleanValue();
		boolean locked = Boolean.valueOf(folderNode.getAttributes().getNamedItem("locked").getNodeValue()).booleanValue();
		folder.setColor(color);
		folder.setVisible(visible);
		folder.setLocked(locked);

		NodeList childNodes = folderNode.getChildNodes();

		for(int i = 0; i < childNodes.getLength(); i++) {
			Node c = childNodes.item(i);
			if(c.getNodeName().equals("folder")) {
				Folder newFolder = parseFolder(c);
				if(newFolder != null) {
					folder.addFolder(newFolder);
				}
			} else if(c.getNodeName().equals("track")) {
				Track newTrack = parseTrack(c);
				if(newTrack != null) {
					folder.addTrack(newTrack);
				}
			}

		}

		return folder;
	}



	private Track parseTrack(Node trackNode) {
		//System.out.println("parseTrack");
		String name = trackNode.getAttributes().getNamedItem("name").getNodeValue();
		//System.out.println("new Track(" + name + ")");
		Track track = new Track(name);	

		Color color = TrackTools.hexToColor(trackNode.getAttributes().getNamedItem("color").getNodeValue());
		boolean visible = Boolean.valueOf(trackNode.getAttributes().getNamedItem("visible").getNodeValue()).booleanValue();
		boolean locked = Boolean.valueOf(trackNode.getAttributes().getNamedItem("locked").getNodeValue()).booleanValue();
		long time = Long.valueOf(trackNode.getAttributes().getNamedItem("time").getNodeValue()).longValue();

		track.setColor(color);
		track.setVisible(visible);
		track.setLocked(locked);
		track.setTime(time);


		AISState defaultAISValues = new AISState();

		NodeList childNodes = trackNode.getChildNodes();

		for(int i = 0; i < childNodes.getLength(); i++) {
			Node c = childNodes.item(i);
			//System.out.println("node: " + c.getNodeName());
			if(c.getNodeName().equals("waypoints")) {

				NodeList waypointNodes = c.getChildNodes();
				for(int w = 0; w < waypointNodes.getLength(); w++) {
					Node waypointNode = waypointNodes.item(w);
					//System.out.println("node: " + waypointNode.getNodeName());
					if(waypointNode.getNodeName().equals("waypoint")) {
						//System.out.println("lat+lon" + waypointNode.getAttributes().getNamedItem("latitude").getNodeValue().toString());
						double lat = Double.valueOf(waypointNode.getAttributes().getNamedItem("latitude").getNodeValue()).doubleValue();
						double lon = Double.valueOf(waypointNode.getAttributes().getNamedItem("longitude").getNodeValue()).doubleValue();
						double speed = Double.valueOf(waypointNode.getAttributes().getNamedItem("speed").getNodeValue()).doubleValue();
						double angle = Double.valueOf(waypointNode.getAttributes().getNamedItem("angle").getNodeValue()).doubleValue();

						//System.out.println("new Waypoint(track, " + lat + " , " +  lon + ");");
						Waypoint newWaypoint = new Waypoint(track, lat, lon, speed, angle);

						track.addWaypoint(newWaypoint);
					}
				}
			} else if(c.getNodeName().equals("actions")) {

			} else if(c.getNodeName().equals("aisvalues")) {

				NodeList aisValuesNodes = c.getChildNodes();
				for(int w = 0; w < aisValuesNodes.getLength(); w++) {
					Node aisValueNode = aisValuesNodes.item(w);
					if(aisValueNode.getNodeName().equals("aisvalue")) {

						String parameter = aisValueNode.getAttributes().getNamedItem("parameter").getNodeValue();
						AISParameter aisParameter = AISParameter.fromString(parameter);
						if(aisParameter != null) {
							String value = aisValueNode.getAttributes().getNamedItem("value").getNodeValue();
							Object newValue = null;

							//System.out.println("parameter: " + parameter + ", value: " + value + ", type: " + aisParameter.getType() + "::" + aisParameter.getTextName());
							
							try {

								if(aisParameter.getType().equals(String.class)) {
									newValue = value;
								} else if(aisParameter.getType().equals(Boolean.class)) {
									newValue = Boolean.parseBoolean(value);//((Integer.parseInt(value) == 0) ? false : true);
								} else if(aisParameter.getType().equals(Double.class)) {
									newValue = Double.parseDouble(value);
								} else if(aisParameter.getType().equals(Integer.class)) {
									newValue = Integer.parseInt(value);
								} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
									newValue = Integer.parseInt(value);
								} else if(aisParameter.getType().equals(CDateTime.class)) {
									newValue = Long.parseLong(value);
								} else {
									newValue = value;
								}

								if(newValue != null) {
									defaultAISValues.put(aisParameter, newValue);
								}

							} catch(Exception e) {
								System.out.println("ERROR: " + e.getMessage());
							}
						}
					}
				}
			}
		}
		track.setDefaultAISValues(defaultAISValues);
		track.update(null);
		return track;
	}

	public boolean saveScenario(Scenario scenario) {
		if(scenario.getFileName().startsWith(ScenarioManager.NEW_SCENARIO)) {
			return saveAsScenario(scenario);
		} else {
			return writeScenarioToFile(scenario, scenario.getFileName());
		}
	}

	public boolean saveAsScenario(Scenario scenario) {
		FileDialog fd = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		fd.setText("Save Scenario As");
		fd.setFilterPath("C:/");
		fd.setFileName(scenario.getName());
		fd.setFilterNames(FILTER_NAMES);
		fd.setFilterExtensions(FILTER_EXTS);
		String selected = fd.open();
		if(selected == null) {
			return false;
		}
		
		File f = new File(selected);
		if(f.exists()) {
			if(!MessageDialog.openQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Question", "The file '" + f.getAbsolutePath()  + "' already exists. Are you sure you want to overwrite the existing file?")) {
				return false;
			}
		}
		return writeScenarioToFile(scenario, selected);
	}

	private boolean writeScenarioToFile(Scenario scenario, String fileName) {
		if(!fileName.endsWith(".psml")) fileName += ".psml";
		//System.out.println("Writing Scenario to " + fileName);

		try {
			FileWriter fw = new FileWriter(fileName, false);
			fw.write(scenario.toXML());
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		scenario.setFileName(fileName);
		
		//System.out.println("Done Writing Scenario to " + fileName);
		return true;
	}

	public void closeScenario(Scenario scenario) {
		if(scenarios.contains(scenario)) {
			scenario.getRootFolder().disposePropertyImage();
			scenarios.remove(scenario);
		}
	}
}
