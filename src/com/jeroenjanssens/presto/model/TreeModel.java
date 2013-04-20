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

package com.jeroenjanssens.presto.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.jeroenjanssens.presto.views.scenario.timeline.TimeLine;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public abstract class TreeModel {
	private Folder parent;
	protected String name;
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	private boolean isLocked;
	private boolean isVisible;
	
	private Image propertyImage;
	private GC gcPropertyImage;
	
	private Color color = null;
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Folder getParent() {
		return parent;
	}
	
	/* The receiver should visit the toVisit object and
	 * pass along the argument. */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);
	
	public String getName() {
		return name;
	}
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public void init() {
		isLocked = false;
		isVisible = true;
		//RGB rgb = new RGB((float)Math.random()*360 ,0.8f, 0.8f);
		//color = new Color(Display.getDefault(), rgb);
		color = new Color(Display.getDefault(), 255, 255, 255);
		
		propertyImage = new Image(Display.getDefault(), new Rectangle(0, 0, 76, 16));
		gcPropertyImage = new GC(propertyImage);
		updatePropertyImage();	
	}
	
	public Image getPropertyImage() {
		return propertyImage;
	}
	
	public void updatePropertyImage() {
		if(isLocked()) {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_LOCKED), 41, 0);
		} else if(isLockedBecauseParentIsLocked()) {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_LOCKED_PARENT), 41, 0);
		} else {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_DOT), 41, 0);
		}
		
		if(!isVisible()) {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_INVISIBLE), 22, 0);
		} else if(isInvisibleBecauseParentIsInvisible()) {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_INVISIBLE_PARENT), 22, 0);
		} else {
			gcPropertyImage.drawImage(TimeLine.getImage(TimeLine.IMAGE_DOT), 22, 0);
		}
		
		if(this instanceof Track) {
			gcPropertyImage.setBackground(color);
			gcPropertyImage.fillRectangle(61, 3, 9, 9);
			
			gcPropertyImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			gcPropertyImage.drawRectangle(61, 3, 9, 9);
		} 
		
		if(this instanceof Folder) {
			Folder folder = (Folder) this;
			if(folder.getChildren() == null) return;
			if(folder.getChildren().isEmpty()) return;
			
			for(TreeModel tm : folder.getChildren()) {
				tm.updatePropertyImage();
			}
		}
	}
	
	public void disposePropertyImage() {
		//System.out.println("disposePropertyImage() of " + this.getName());
		this.propertyImage.dispose();
		this.gcPropertyImage.dispose();
		
		if(this instanceof Folder) {
			Folder folder = (Folder) this;
			if(folder.getChildren() == null) return;
			if(folder.getChildren().isEmpty()) return;
			
			for(TreeModel tm : folder.getChildren()) {
				tm.disposePropertyImage();
			}
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		updatePropertyImage();
	}
	
	public TreeModel(String name) {
		this.name = name;
		init();
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}

	public String getTitle() {
		return name;
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public void setLocked(boolean locked) {
		isLocked = locked;
		updatePropertyImage();
	}

	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean visible) {
		isVisible = visible;
		updatePropertyImage();
	}

	public int getLevel() {
		if(this.getParent() == null) return 0;
		return (this.getParent().getLevel() + 1);
	}

	public void setParent(Folder parent) {
		this.parent = parent;
	}
	
	public boolean isLockedBecauseParentIsLocked() {
		if(parent == null) return false;
		if(parent.isLocked()) return true;
		return parent.isLockedBecauseParentIsLocked();
	}
	
	public boolean isInvisibleBecauseParentIsInvisible() {
		if(parent == null) return false;
		if(!parent.isVisible()) return true;
		return parent.isInvisibleBecauseParentIsInvisible();
	}
	
}
