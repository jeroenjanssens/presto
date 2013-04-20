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

import gov.nasa.worldwind.layers.RenderableLayer;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.jeroenjanssens.presto.views.earth.layers.TrackLayer;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class NavigationTool extends AbstractTool {

	private boolean navigating = false;
	
	public NavigationTool(EarthViewToolBar earthViewToolBar) {
		super(earthViewToolBar, "Navigation Tool (Alt+N)", "icons/navigate.png", EarthViewToolBar.TOOL_NAVIGATION);
	}

	@Override
	public Cursor getDefaultCursor() {
		return earthViewToolBar.getPrestoCursor().get(PrestoCursor.CURSOR_NAVIGATION_UP);
	}
	
	@Override
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		if(picking) {
			return false;
		} else {
			return (layer instanceof TrackLayer);
		}
	}
	
	/* 
	 * MouseListener
	 */
	
	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		navigating = false;
		e.consume();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		navigating = false;
		e.consume();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		navigating = true;
		updateCursor();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		navigating = false;
		updateCursor();
	}
	
	@Override
	public void updateCursor() {
		if(navigating) {
			setCursor(PrestoCursor.CURSOR_NAVIGATION_DOWN);
		} else {
			setCursor(PrestoCursor.CURSOR_NAVIGATION_UP);
		}
	}

	
	/* 
	 * MouseMotionListener
	 */
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	/* 
	 * MouseWheelListener
	 */
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	
	}

	/*
	 *	KeyListener
	 */
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}


}
