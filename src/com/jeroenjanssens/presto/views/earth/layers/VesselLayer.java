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

package com.jeroenjanssens.presto.views.earth.layers;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;

import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.views.earth.EarthView;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class VesselLayer extends RenderableLayer implements LockedLayer {

	private EarthView earthView;
	private long time;
	
	public VesselLayer(EarthView earthView) {
		this.earthView = earthView;
	}

	@Override
	public void doPick(DrawContext dc, java.awt.Point pickPoint) {

	}

	@Override
	public void doRender(DrawContext dc) {
		
		if(earthView.getCurrentScenarioEditor() == null) return;
		if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
		
		time = earthView.getCurrentScenarioEditor().getTimeLine().getCursorTime();
		
		ArrayList<Track> allTracks = earthView.getCurrentScenarioEditor().getScenario().getRootFolder().getAllTracks(false, dc.isPickingMode());
		
		int i = 0;
		for(Track track : allTracks) {
			track.getVessel().setVesselSize(earthView.getVesselSize());
			track.getVessel().render(dc, time);
			i++;
		}
	}
}
