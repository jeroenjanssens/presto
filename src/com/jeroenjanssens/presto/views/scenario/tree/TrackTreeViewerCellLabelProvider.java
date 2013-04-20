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

package com.jeroenjanssens.presto.views.scenario.tree;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.jeroenjanssens.presto.model.AnimatedLayer;
import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.views.scenario.timeline.TimeLine;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TrackTreeViewerCellLabelProvider extends CellLabelProvider {	
	//private Map imageCache = new HashMap(11);
	
	public Image getImage(Object element) {
		if (element instanceof Folder) {
			return TimeLine.getImage(TimeLine.IMAGE_FOLDER);
		} else if (element instanceof Track) {
			return TimeLine.getImage(TimeLine.IMAGE_TRACK);
		} else if (element instanceof AnimatedLayer) {
			return TimeLine.getImage(TimeLine.IMAGE_DOT);
		} else {
			throw unknownElement(element);
		}
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof Folder) {
			if(((Folder)element).getName() == null) {
				return "Folder";
			} else {
				return ((Folder)element).getName();
			}
		} else if (element instanceof Track) {
			return ((Track)element).getTitle();
		} else if (element instanceof AnimatedLayer) {
			return ((AnimatedLayer)element).getTitle();
		} else {
			throw unknownElement(element);
		}
	}

	@Override
	public void dispose() {
		/*
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
		*/
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		
	}

}
