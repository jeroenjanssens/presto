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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import com.jeroenjanssens.presto.views.earth.layers.BackgroundLayer;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class BackgroundManager {

	private Background background = null;
	private boolean isLoaded = false;
	private BackgroundLayer backgroundLayer;

	public void setBackground(Background background) {
		this.background = background;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public BackgroundManager() {

	}

	public void loadBackground(final String fileName, final BackgroundLayer backgroundLayer) {
		this.background = null;
		this.backgroundLayer = backgroundLayer;
		LoadBackgroundJob loadBackgroundJob = new LoadBackgroundJob(this, fileName);
		loadBackgroundJob.setPriority(Job.LONG);
		loadBackgroundJob.addJobChangeListener(new IJobChangeListener() {


			public void aboutToRun(IJobChangeEvent event) {
			
			}

			public void awake(IJobChangeEvent event) {
			
			}

			public void done(IJobChangeEvent event) {
				backgroundLayer.getEarthView().getSite().getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(backgroundLayer.getEarthView().getSite().getShell(), "Background successfully loaded", "The background file '" + fileName + " ' has been successfully loaded");
					}
				});

			}

			public void running(IJobChangeEvent event) {
			
			}

			public void scheduled(IJobChangeEvent event) {
				
			}

			
			public void sleeping(IJobChangeEvent event) {
				
			}

		});
		loadBackgroundJob.schedule();
	}

	public BackgroundLayer getBackgroundLayer() {
		return backgroundLayer;
	}

	public Background getBackground() {
		return background;
	}

	public boolean isLoaded() {
		return isLoaded;
	}


}
