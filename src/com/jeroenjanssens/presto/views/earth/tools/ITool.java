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

package com.jeroenjanssens.presto.views.earth.tools;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.RenderableLayer;

import java.awt.Cursor;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public interface ITool extends MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	public String getName();
	public Action getAction();
	public ToolItem getToolItem();
	public int getToolState();
	public WorldWindowGLCanvas getWWD();
	public Cursor getDefaultCursor();
	public void addToToolBar(Display display, ToolBar toolBar);
	public void updateCursor();
	public boolean mayLayerRender(RenderableLayer layer, boolean picking);
	public void setFocus(boolean focus);
	public void disposeImage();
}
