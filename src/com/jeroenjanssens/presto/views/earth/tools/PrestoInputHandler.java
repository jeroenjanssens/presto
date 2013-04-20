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

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.OrbitViewInputBroker;
import gov.nasa.worldwind.event.DragSelectEvent;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.util.Logging;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.EventListenerList;

import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class PrestoInputHandler extends WWObjectImpl implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, FocusListener, InputHandler {
	private EarthView earthView = null;
	private WorldWindow wwd = null;
	private final EventListenerList eventListeners = new EventListenerList();
	private java.awt.Point mousePoint = new java.awt.Point();
	private PickedObjectList hoverObjects;
	private PickedObjectList objectsAtButtonPress;
	private boolean isHovering = false;
	private boolean isDragging = false;
	private javax.swing.Timer hoverTimer = new javax.swing.Timer(600, new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					if (PrestoInputHandler.this.pickMatches(PrestoInputHandler.this.hoverObjects)) {
						PrestoInputHandler.this.isHovering = true;
						PrestoInputHandler.this.callSelectListeners(new SelectEvent(
										PrestoInputHandler.this.wwd,
										SelectEvent.HOVER, mousePoint,
										PrestoInputHandler.this.hoverObjects));
						PrestoInputHandler.this.hoverTimer.stop();
					}
				}
			});
	
	public void setEarthView(EarthView earthView) {
		this.earthView = earthView;
	}
	
	// Delegate handler for View.
	private final OrbitViewInputBroker viewInputBroker = new OrbitViewInputBroker();
	private SelectListener selectListener;

	public void addMouseListener(MouseListener listener) {
		this.eventListeners.add(MouseListener.class, listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		this.eventListeners.add(MouseMotionListener.class, listener);
	}

	public void addMouseWheelListener(MouseWheelListener listener) {
		this.eventListeners.add(MouseWheelListener.class, listener);
	}

	public void addKeyListener(KeyListener listener) {
		this.eventListeners.add(KeyListener.class, listener);
	}
	
	public void addSelectListener(SelectListener listener) {
		this.eventListeners.add(SelectListener.class, listener);
	}

	protected void callMouseClickedListeners(MouseEvent event) {
		for (MouseListener listener : this.eventListeners
				.getListeners(MouseListener.class)) {
			listener.mouseClicked(event);
		}
	}

	protected void callMouseDraggedListeners(MouseEvent event) {
		for (MouseMotionListener listener : this.eventListeners
				.getListeners(MouseMotionListener.class)) {
			listener.mouseDragged(event);
		}
	}

	protected void callMouseMovedListeners(MouseEvent event) {
		for (MouseMotionListener listener : this.eventListeners.getListeners(MouseMotionListener.class)) {
			listener.mouseMoved(event);
		}
	}

	protected void callMousePressedListeners(MouseEvent event) {
		for (MouseListener listener : this.eventListeners
				.getListeners(MouseListener.class)) {
			listener.mousePressed(event);
		}
	}

	protected void callMouseReleasedListeners(MouseEvent event) {
		for (MouseListener listener : this.eventListeners
				.getListeners(MouseListener.class)) {
			listener.mouseReleased(event);
		}
	}
	
	protected void callMouseWheelMovedListeners(MouseWheelEvent event) {
		for (MouseWheelListener listener : this.eventListeners.getListeners(MouseWheelListener.class)) {
			listener.mouseWheelMoved(event);
		}
	}

	protected void callKeyPressedListeners(KeyEvent event) {
		for (KeyListener listener : this.eventListeners.getListeners(KeyListener.class)) {
			listener.keyPressed(event);
		}
	}
	
	protected void callKeyReleasedListeners(KeyEvent event) {
		for (KeyListener listener : this.eventListeners.getListeners(KeyListener.class)) {
			listener.keyReleased(event);
		}
	}
	
	protected void callKeyTypedListeners(KeyEvent event) {
		for (KeyListener listener : this.eventListeners.getListeners(KeyListener.class)) {
			listener.keyTyped(event);
		}
	}
	
	protected void callSelectListeners(SelectEvent event) {
		for (SelectListener listener : this.eventListeners
				.getListeners(SelectListener.class)) {
			listener.selected(event);
		}
	}

	protected void cancelDrag() {
		if (this.isDragging) {
			this.callSelectListeners(new DragSelectEvent(this.wwd,
					SelectEvent.DRAG_END, null, this.objectsAtButtonPress,
					this.mousePoint));
		}

		this.isDragging = false;
	}

	private void cancelHover() {
		if (this.isHovering) {
			this.callSelectListeners(new SelectEvent(this.wwd,
					SelectEvent.HOVER, this.mousePoint, null));
		}

		this.isHovering = false;
		this.hoverObjects = null;
		this.hoverTimer.stop();
	}

	public void clear() {
		if (this.hoverObjects != null)
			this.hoverObjects.clear();
		this.hoverObjects = null;

		if (this.objectsAtButtonPress != null)
			this.objectsAtButtonPress.clear();
		this.objectsAtButtonPress = null;
	}

	protected void doHover(boolean reset) {
		return;
		/*
		PickedObjectList pickedObjects = this.wwd.getObjectsAtCurrentPosition();
		
		for(PickedObject p : pickedObjects) {
			if(p.getObject() instanceof Waypoint) {
				System.out.println("Hovering: waypoint "  + ((Waypoint)p.getObject()).getId() + " of Track " + ((Waypoint)p.getObject()).getTrack().getName());
			} else {
				System.out.println(p.getObject().getClass().getCanonicalName());
			}
		}
		
		if (!(this.isPickListEmpty(this.hoverObjects) || this.isPickListEmpty(pickedObjects))) {
			PickedObject hover = this.hoverObjects.getTopPickedObject();
			PickedObject last = pickedObjects.getTopPickedObject();

			Object oh = hover == null ? null
					: hover.getObject() != null ? hover.getObject() : hover
							.getParentLayer() != null ? hover.getParentLayer()
							: null;
			Object ol = last == null ? null : last.getObject() != null ? last
					.getObject() : last.getParentLayer() != null ? last
					.getParentLayer() : null;
			if (oh != null && ol != null && oh.equals(ol)) {
				return; // object picked is the hover object. don't do anything
				// but wait for the timer to expire.
			}
		}

		this.cancelHover();

		if (!reset) {
			return;
		}

		if ((pickedObjects != null) && (pickedObjects.getTopObject() != null)
				&& pickedObjects.getTopPickedObject().isTerrain()) {
			return;
		}

		this.hoverObjects = pickedObjects;
		this.hoverTimer.restart();
		*/
	}

	public void focusGained(FocusEvent focusEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (focusEvent == null) {
			return;
		}

		this.viewInputBroker.focusGained(focusEvent);
	}

	public void focusLost(FocusEvent focusEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (focusEvent == null) {
			return;
		}

		this.viewInputBroker.focusLost(focusEvent);
	}

	public WorldWindow getEventSource() {
		return this.wwd;
	}

	public int getHoverDelay() {
		return this.hoverTimer.getDelay();
	}

	protected PickedObjectList getHoverObjects() {
		return hoverObjects;
	}

	protected Point getMousePoint() {
		return mousePoint;
	}

	protected PickedObjectList getObjectsAtButtonPress() {
		return objectsAtButtonPress;
	}

	protected OrbitViewInputBroker getViewInputBroker() {
		return viewInputBroker;
	}

	protected WorldWindow getWorldWindow() {
		return wwd;
	}

	protected boolean isDragging() {
		return isDragging;
	}

	protected boolean isHovering() {
		return isHovering;
	}

	public boolean isLockViewHeading() {
		return this.viewInputBroker.isLockHeading();
	}

	protected boolean isPickListEmpty(PickedObjectList pickList) {
		return pickList == null || pickList.size() < 1;
	}

	public boolean isSmoothViewChanges() {
		return this.viewInputBroker.isSmoothViewChanges();
	}

	public void keyPressed(KeyEvent keyEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (keyEvent == null) {
			return;
		}
		
		if(keyEvent.getKeyChar() == ' ') {
			//System.out.println("PRESSED SPACEBAR IN PRESTOINPUTHANDLER");
		}
		
		
		this.callKeyPressedListeners(keyEvent);
		if (!keyEvent.isConsumed()) {
			this.viewInputBroker.keyPressed(keyEvent);
		}
		
	}

	public void keyReleased(KeyEvent keyEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (keyEvent == null) {
			return;
		}
		
		if(keyEvent.getKeyChar() == ' ') {
			//System.out.println("RELEASED SPACEBAR IN PRESTOINPUTHANDLER");
		}

		this.callKeyReleasedListeners(keyEvent);
		if (!keyEvent.isConsumed()) {
			this.viewInputBroker.keyReleased(keyEvent);
		}
	
	}

	public void keyTyped(KeyEvent keyEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (keyEvent == null) {
			return;
		}

		this.callKeyTypedListeners(keyEvent);
		if (!keyEvent.isConsumed()) {
			this.viewInputBroker.keyTyped(keyEvent);
		}
	}

	public void mouseClicked(final MouseEvent mouseEvent) {
		//System.out.println("Mouse clicked");
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (this.wwd.getView() == null) {
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		PickedObjectList pickedObjects = this.wwd.getObjectsAtCurrentPosition();

		this.callMouseClickedListeners(mouseEvent);

		if (pickedObjects != null && pickedObjects.getTopPickedObject() != null
				&& !pickedObjects.getTopPickedObject().isTerrain()) {
			// Something is under the cursor, so it's deemed "selected".
			if (MouseEvent.BUTTON1 == mouseEvent.getButton()) {
				if (mouseEvent.getClickCount() % 2 == 1) {
					this.callSelectListeners(new SelectEvent(this.wwd,
							SelectEvent.LEFT_CLICK, mouseEvent, pickedObjects));
				} else {
					this.callSelectListeners(new SelectEvent(this.wwd,
							SelectEvent.LEFT_DOUBLE_CLICK, mouseEvent,
							pickedObjects));
				}
			} else if (MouseEvent.BUTTON3 == mouseEvent.getButton()) {
				this.callSelectListeners(new SelectEvent(this.wwd, SelectEvent.RIGHT_CLICK, mouseEvent, pickedObjects));
			}

			this.wwd.getView().firePropertyChange(AVKey.VIEW, null, this.wwd.getView());
		} else {
			if (!mouseEvent.isConsumed()) {
				//this.viewInputBroker.mouseClicked(mouseEvent);
			}
		}
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		if (this.wwd == null) {
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		Point prevMousePoint = this.mousePoint;
		this.mousePoint = mouseEvent.getPoint();
		this.callMouseDraggedListeners(mouseEvent);

		if (InputEvent.BUTTON1_DOWN_MASK == mouseEvent.getModifiersEx()) {
			PickedObjectList pickedObjects = this.objectsAtButtonPress;
			if (this.isDragging || (pickedObjects != null && pickedObjects.getTopPickedObject() != null && !pickedObjects.getTopPickedObject().isTerrain())) {
				this.isDragging = true;
				this.callSelectListeners(new DragSelectEvent(this.wwd,
						SelectEvent.DRAG, mouseEvent, pickedObjects,
						prevMousePoint));
			}
		}

		if (!this.isDragging) {
			if (!mouseEvent.isConsumed()) {
				this.viewInputBroker.mouseDragged(mouseEvent);
			}
		}

		// Redraw to update the current position and selection.
		if (this.wwd.getSceneController() != null) {
			this.wwd.getSceneController().setPickPoint(mouseEvent.getPoint());
			this.wwd.redraw();
		}
	}

	public void mouseEntered(MouseEvent mouseEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		this.viewInputBroker.mouseEntered(mouseEvent);
		this.cancelHover();
		this.cancelDrag();
	}

	public void mouseExited(MouseEvent mouseEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		this.viewInputBroker.mouseExited(mouseEvent);
		this.cancelHover();
		this.cancelDrag();
	}

	public void mouseMoved(MouseEvent mouseEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		this.mousePoint = mouseEvent.getPoint();
		this.callMouseMovedListeners(mouseEvent);

		if (!mouseEvent.isConsumed()) {
			this.viewInputBroker.mouseMoved(mouseEvent);
		}

		// Redraw to update the current position and selection.
		if (this.wwd.getSceneController() != null) {
			this.wwd.getSceneController().setPickPoint(mouseEvent.getPoint());
			this.wwd.redraw();
			
			PickedObjectList pickedObjects = this.wwd.getObjectsAtCurrentPosition();
			if(pickedObjects != null) {
				/*
				for(PickedObject p : pickedObjects) {
				}
				*/
			}
		}
	}

	public void mousePressed(MouseEvent mouseEvent) {
		//System.out.println("Mouse pressed");
		if (this.wwd == null) {
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		this.mousePoint = mouseEvent.getPoint();
		this.cancelHover();
		this.cancelDrag();

		this.objectsAtButtonPress = this.wwd.getObjectsAtCurrentPosition();

		this.callMousePressedListeners(mouseEvent);

		if (this.objectsAtButtonPress != null && objectsAtButtonPress.getTopPickedObject() != null && !this.objectsAtButtonPress.getTopPickedObject().isTerrain()) {
			// Something is under the cursor, so it's deemed "selected".
			if (MouseEvent.BUTTON1 == mouseEvent.getButton()) {
				this.callSelectListeners(new SelectEvent(this.wwd,
						SelectEvent.LEFT_PRESS, mouseEvent,
						this.objectsAtButtonPress));
			} else if (MouseEvent.BUTTON3 == mouseEvent.getButton()) {
				this.callSelectListeners(new SelectEvent(this.wwd,
						SelectEvent.RIGHT_PRESS, mouseEvent,
						this.objectsAtButtonPress));
			}

			// TODO: Why is this event fired?
			this.wwd.getView().firePropertyChange(AVKey.VIEW, null,
					this.wwd.getView());
		} else {
			if (!mouseEvent.isConsumed()) {
				this.viewInputBroker.mousePressed(mouseEvent);
			}
		}
	}

	public void mouseReleased(MouseEvent mouseEvent) {
		//System.out.println("Mouse released");
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}

		if (mouseEvent == null) {
			return;
		}

		this.mousePoint = mouseEvent.getPoint();
		this.callMouseReleasedListeners(mouseEvent);
		if (!mouseEvent.isConsumed()) {
			this.viewInputBroker.mouseReleased(mouseEvent);
		}
		this.doHover(true);
		this.cancelDrag();
	}

	public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
		if (this.wwd == null) // include this test to ensure any derived
		// implementation performs it
		{
			return;
		}
		
		this.callMouseWheelMovedListeners(mouseWheelEvent);
		
		
		if (mouseWheelEvent == null) {
			return;
		}
		if (!mouseWheelEvent.isConsumed()) {
			if((mouseWheelEvent.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK)) == InputEvent.CTRL_DOWN_MASK) {
				//System.out.println("Shift Time!");
				
				if(earthView.getCurrentScenarioEditor() == null) return;
				if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
				earthView.getCurrentScenarioEditor().getTimeLine().shiftTime(mouseWheelEvent.getWheelRotation());
				
				
				mouseWheelEvent.consume();
			}
			
		}
		if (!mouseWheelEvent.isConsumed()) {
			this.viewInputBroker.mouseWheelMoved(mouseWheelEvent);
		}
	}

	protected boolean pickMatches(PickedObjectList pickedObjects) {
		if (this.isPickListEmpty(this.wwd.getObjectsAtCurrentPosition())
				|| this.isPickListEmpty(pickedObjects)) {
			return false;
		}

		PickedObject lastTop = this.wwd.getObjectsAtCurrentPosition()
				.getTopPickedObject();

		if (null != lastTop && lastTop.isTerrain()) {
			return false;
		}

		PickedObject newTop = pickedObjects.getTopPickedObject();
		// noinspection SimplifiableIfStatement
		if (lastTop == null || newTop == null || lastTop.getObject() == null
				|| newTop.getObject() == null) {
			return false;
		}

		return lastTop.getObject().equals(newTop.getObject());
	}

	public void removeHoverSelectListener() {
		hoverTimer.stop();
		hoverTimer = null;
		this.wwd.removeSelectListener(selectListener);
	}

	public void removeMouseListener(MouseListener listener) {
		this.eventListeners.remove(MouseListener.class, listener);
	}

	public void removeMouseMotionListener(MouseMotionListener listener) {
		this.eventListeners.remove(MouseMotionListener.class, listener);
	}

	public void removeMouseWheelListener(MouseWheelListener listener) {
		this.eventListeners.remove(MouseWheelListener.class, listener);
	}

	public void removeKeyListener(KeyListener listener) {
		this.eventListeners.remove(KeyListener.class, listener);
	}
	
	public void removeSelectListener(SelectListener listener) {
		this.eventListeners.remove(SelectListener.class, listener);
	}

	protected void setDragging(boolean dragging) {
		isDragging = dragging;
	}

	public void setEventSource(WorldWindow newWorldWindow) {
		if (newWorldWindow != null && !(newWorldWindow instanceof Component)) {
			String message = Logging
					.getMessage("Awt.AWTInputHandler.EventSourceNotAComponent");
			Logging.logger().finer(message);
			throw new IllegalArgumentException(message);
		}

		if (newWorldWindow == this.wwd) {
			return;
		}

		if (this.wwd != null) {
			Component c = (Component) this.wwd;
			c.removeKeyListener(this);
			c.removeMouseMotionListener(this);
			c.removeMouseListener(this);
			c.removeMouseWheelListener(this);
			c.removeFocusListener(this);
		}

		this.wwd = newWorldWindow;
		this.viewInputBroker.setWorldWindow(this.wwd);

		if (this.wwd == null) {
			return;
		}

		Component c = (java.awt.Component) this.wwd;
		c.addKeyListener(this);
		c.addMouseMotionListener(this);
		c.addMouseListener(this);
		c.addMouseWheelListener(this);
		c.addFocusListener(this);

		/*
		selectListener = new SelectListener() {
			public void selected(SelectEvent event) {
				if (event.getEventAction().equals(SelectEvent.ROLLOVER)) {
					doHover(true);
				}
			}
		};
		this.wwd.addSelectListener(selectListener);
		*/
	}

	public void setHoverDelay(int delay) {
		this.hoverTimer.setDelay(delay);
	}

	protected void setHovering(boolean hovering) {
		isHovering = hovering;
	}

	protected void setHoverObjects(PickedObjectList hoverObjects) {
		this.hoverObjects = hoverObjects;
	}

	public void setLockViewHeading(boolean lockHeading) {
		this.viewInputBroker.setLockHeading(lockHeading);
	}

	protected void setMousePoint(Point mousePoint) {
		this.mousePoint = mousePoint;
	}

	protected void setObjectsAtButtonPress(PickedObjectList objectsAtButtonPress) {
		this.objectsAtButtonPress = objectsAtButtonPress;
	}

	public void setSmoothViewChanges(boolean smoothViewChanges) {
		this.viewInputBroker.setSmoothViewChanges(smoothViewChanges);
	}
}
