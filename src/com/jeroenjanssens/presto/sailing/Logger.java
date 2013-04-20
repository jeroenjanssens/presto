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

package com.jeroenjanssens.presto.sailing;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.sailing.ais.AISState;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Logger implements IRunnableWithProgress {

	ArrayList<AISState> receivedAISMessages = new ArrayList<AISState>();

	Scenario scenario;
	String fileName;
	long startTime;
	long endTime;
	boolean onlyVisibleTracks = false;
	IProtocol protocol = null;

	private IProgressMonitor monitor;

	public Logger(Scenario scenario, String fileName, long startTime, long endTime, boolean onlyVisibleTracks, IProtocol protocol) {
		this.scenario = scenario;
		this.fileName = fileName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.onlyVisibleTracks = onlyVisibleTracks;
		this.protocol = protocol;

		//System.out.println("startTime: " + startTime);
		//System.out.println("endTime: " + endTime);
	}

	public void start() {
		//This is where all the logging occurs

		//log each track seperately (considering the boolean onlyVisibleTracks)
		for(Track track : scenario.getRootFolder().getAllTracks(onlyVisibleTracks, false)) {
			if(monitor.isCanceled()) break;
			//System.out.println("Sailing track " + track.getName());
			monitor.subTask("Sailing track " + track.getName());
			ArrayList<AISState> messages = track.getVessel().getSailor().sail(monitor);
			if((messages != null) && !messages.isEmpty()) {
				receivedAISMessages.addAll(messages);
			}
		}

		monitor.subTask("Sorting AIS messages");
		//sort all the messages	
		Collections.sort(receivedAISMessages);
		monitor.worked(1000);
	}



	public void writeToFile() {
		//create new file
		FileWriter fw;
		try {
			fw = new FileWriter(fileName, false);
			
			monitor.subTask("Writing to file");
			
			String headerMessage = protocol.getHeader();
			if(headerMessage != null) {
				fw.write(headerMessage);
			}
			
			for(AISState aisMessage : receivedAISMessages) {
				if(monitor.isCanceled()) break;
				monitor.worked(1);
				if(((startTime == 0) || (aisMessage.getTime() >= startTime)) && ((endTime == 0) || (aisMessage.getTime() <= endTime))) {
					String stringMessage = protocol.constructMesssage(aisMessage);
					if(stringMessage != null) {	
						//write stringMessage to file
						//System.out.println("stringMessage: " + stringMessage);
						fw.write(stringMessage);
					}
				}
			}

			//close file
			fw.flush();
			fw.close();
		} catch (IOException e) {
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Error Exporting Scenario", "An error occured while writing the scenario to file.");
			monitor.setCanceled(true);
		}

	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		this.monitor = monitor;

		int work = 0;

		for(Track track : scenario.getRootFolder().getAllTracks(onlyVisibleTracks, false)) {
			long startTime = track.getTime();
			long endTime = track.getLastWaypoint().getTime();
			work += 2 * ((endTime - startTime) / 1000);
		}

		work += 1000;

		monitor.beginTask("Exporting scenario", work);
		start();

		if(!monitor.isCanceled()) {
			writeToFile();
		}
		monitor.done();
		if (monitor.isCanceled()) throw new InterruptedException("Exporting scenario was cancelled");
	}



}
