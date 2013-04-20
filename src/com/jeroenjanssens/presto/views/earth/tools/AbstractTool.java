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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.views.earth.layers.ToolLayer;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public abstract class AbstractTool implements ITool  {
	
	protected EarthViewToolBar earthViewToolBar;
	protected String name;
	protected String image;
	protected int toolState;
	
	protected Action action;
	protected ToolItem toolItem;
	
	protected ITool thisTool = this;
	protected ToolLayer toolLayer;
	
	protected boolean downShift;
	protected boolean downCtrl;
	protected boolean downAlt;
	protected boolean downLeft;
	protected boolean downRight;
	
	protected Image icon;
	
	public AbstractTool(EarthViewToolBar earthViewToolBar, String name, String image, int toolState) {
		this.earthViewToolBar = earthViewToolBar;
		this.name = name;
		this.image = image;
		this.toolState = toolState;
		this.toolLayer = earthViewToolBar.getEarthView().getToolLayer();
	}
	
	public void setFocus(boolean focus) {
		
	}
	
	public String getName() {
		return name;
	}
	
	public Action getAction() {
		return action;
	}
	
	public ToolItem getToolItem() {
		return toolItem;
	}
	
	public int getToolState() {
		return toolState;
	}
	
	public Cursor getDefaultCursor() {
		return earthViewToolBar.getPrestoCursor().get(PrestoCursor.CURSOR_DEFAULT);
	}
	
	protected void setCursor(int cursor) {
		this.getWWD().setCursor(earthViewToolBar.getPrestoCursor().get(cursor));
	}
	
	public WorldWindowGLCanvas getWWD() {
		return earthViewToolBar.getEarthView().getWWD();
	}
	
	public PrestoInputHandler getPrestoInputHandler() {
		return earthViewToolBar.getEarthView().getPrestoInputHandler();
	}
		
	public void addToToolBar(Display display, ToolBar toolBar) {
		toolItem = new ToolItem(toolBar, SWT.CHECK);
		icon = Activator.getImageDescriptor(this.image).createImage(display);
		toolItem.setImage(icon);
		toolItem.setToolTipText(this.name);
		
		toolItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				earthViewToolBar.selectTool(thisTool.getToolState());
			}
		});
	}
	
	public boolean mayLayerRender(RenderableLayer layer, boolean picking) {
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}

	protected void setDowns(MouseEvent e) {
		downShift = down(e, InputEvent.SHIFT_DOWN_MASK);
		downCtrl = down(e, InputEvent.CTRL_DOWN_MASK);
		downAlt = down(e, InputEvent.ALT_DOWN_MASK);
		downLeft = down(e, InputEvent.BUTTON1_DOWN_MASK);
		downRight = down(e, InputEvent.BUTTON3_DOWN_MASK);
	}
	
	private boolean down(MouseEvent e, int onmask) {
		return ((e.getModifiersEx() & (onmask)) == onmask);
	}
	
	public void updateCursor() {
		if(updateCursorSpaceBar()) return;
		setCursor(PrestoCursor.CURSOR_DEFAULT);
	}
	
	protected boolean updateCursorSpaceBar() {
		if(earthViewToolBar.isSpaceBarDown()) {
			setCursor(PrestoCursor.CURSOR_NAVIGATION_UP);
			return true;
		}
		return false;
	}
	
	protected void update(MouseEvent e) {
		update(e, true, true, true, true);
	}
	
	protected void update(MouseEvent e, boolean consume, boolean redraw, boolean cursor, boolean timeline) {
		if(e != null) {
			if(consume) e.consume();
		}
		if(redraw) getWWD().redraw();
		if(cursor) updateCursor();
		if(timeline) {
			if(earthViewToolBar.getEarthView().getCurrentScenarioEditor() != null) {
				earthViewToolBar.getEarthView().getCurrentScenarioEditor().getTimeLine().doRedraw();
			}
		}
	}
	
	/* 
	 * MouseListener
	 */
	
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	public void mouseEntered(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	public void mouseExited(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	public void mousePressed(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	
	/* 
	 * MouseMotionListener
	 */
	
	public void mouseDragged(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	/* 
	 * MouseWheelListener
	 */
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(earthViewToolBar.isSpaceBarDown()) return;
		e.consume();
	}

	/*
	 *	KeyListener
	 */
	
	public void keyPressed(KeyEvent e) {
		//System.out.println("KeyPressed in " + name);
		if(e.getKeyCode() == 32) {
			earthViewToolBar.setSpaceBarDown(true);
			setFocus(false);
			updateCursor();
		}
		e.consume();
	}

	public void keyReleased(KeyEvent e) {
		//System.out.println("KeyReleased in " + name);
		if(e.getKeyCode() == 32) {
			earthViewToolBar.setSpaceBarDown(false);
			setFocus(true);
			updateCursor();
		}
		e.consume();
	}

	public void keyTyped(KeyEvent e) {
		e.consume();
	}
	
	public void setScenarioChanged() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				earthViewToolBar.getEarthView().getCurrentScenarioEditor().setChanged(true);
			}
		});
		
	}
	
	public void disposeImage() {
		icon.dispose();
	}
	
	
}
