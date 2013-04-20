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

package com.jeroenjanssens.presto.model.background;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class LoadBackgroundJob extends Job {

	private BackgroundManager bgm;
	private String fileName;
	
	public LoadBackgroundJob(BackgroundManager bgm, String fileName) {
		super("Loading Background " + fileName);
		this.bgm = bgm;
		this.fileName = fileName;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		//System.out.println("Start loading Background Vessels");
		
		Background background = null;
		FileInputStream fis = null;
		GZIPInputStream gis = null;
		ObjectInputStream in = null;
		
		try {
			fis = new FileInputStream(fileName);
			gis = new GZIPInputStream(fis);
			in = new ObjectInputStream(gis);
			background = (Background)in.readObject();
			in.close();
		}
		
		catch(IOException ex) {
			ex.printStackTrace();
			MessageDialog.openError(bgm.getBackgroundLayer().getEarthView().getSite().getShell(), "Error", "An error occured while loading the background.");
		}
		
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
			MessageDialog.openError(bgm.getBackgroundLayer().getEarthView().getSite().getShell(), "Error", "An error occured while loading the background.");
		}
		
		if(background != null) {
			//System.out.println("Number of Seconds: " + background.getSamples().size());
			//System.out.println("Number of Vessels: " + background.getVessels().size());

			bgm.setBackground(background);
			bgm.setLoaded(true);
			bgm.getBackgroundLayer().setBackgroundLoaded(true);
			return Status.OK_STATUS;
		} else {
			MessageDialog.openError(bgm.getBackgroundLayer().getEarthView().getSite().getShell(), "Error", "An error occured while loading the background.");
			bgm.setLoaded(false);
			return Status.CANCEL_STATUS;
		}
	}
}
