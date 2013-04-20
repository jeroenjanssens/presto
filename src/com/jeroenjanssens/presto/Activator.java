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

package com.jeroenjanssens.presto;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.jeroenjanssens.presto.model.background.BackgroundManager;
import com.jeroenjanssens.presto.views.earth.tools.PrestoInputHandler;
import com.jeroenjanssens.presto.views.scenario.ScenarioManager;
import com.jeroenjanssens.presto.views.scenario.timeline.TimeLine;


/**
 * The activator class controls the plug-in life cycle
 */

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Presto";

	// The shared instance
	private static Activator plugin;
	static {
		Configuration.setValue(AVKey.INPUT_HANDLER_CLASS_NAME,
				PrestoInputHandler.class.getName());
	}

	private WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
	private ScenarioManager scenarioManager;
	private BackgroundManager backgroundManager;

	static File pluginFolder;

	private static IWorkbenchWindowConfigurer configurer;

	/**
	 * The constructor
	 */
	public Activator() {
		scenarioManager = new ScenarioManager();
		backgroundManager = new BackgroundManager();
		TimeLine.loadImages();
	}

	public ScenarioManager getScenarioManager() {
		return this.scenarioManager;
	}

	public BackgroundManager getBackgroundManager() {
		return this.backgroundManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static String getURL(String imageFilePath) {

		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, imageFilePath);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(imageFilePath);
			} catch (MalformedURLException e) {
				return null;
			}
		}

		if (fullPathString == null) {
			return null;
		}
		return fullPathString.getFile();
	}

	public static String getPluginFolder() {
		if (pluginFolder == null) {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/");
			try {
				url = Platform.resolve(url);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			pluginFolder = new File(url.getPath());
		}
		String p = pluginFolder.getAbsoluteFile().getPath();
		if (p.lastIndexOf("file:") > 4) {
			return p.substring(0, p.lastIndexOf("file:"));
		}
		return p;
	}

	// private final static WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();

	public WorldWindowGLCanvas getWorldWindowGLCanvas() {
		return wwd;
	}

	public static void registerConfigurer(
			IWorkbenchWindowConfigurer myconfigurer) {
		configurer = myconfigurer;
	}

	public static void setTitle(String title) {
		if (configurer != null) {
			configurer.setTitle(title);
		}
	}
}
