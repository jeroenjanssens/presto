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

import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class OneSecondOfSamples implements Serializable {

	private static final long serialVersionUID = -5956524478576708071L;
	
	private ArrayList<BackgroundVesselSample> vesselSamples = new ArrayList<BackgroundVesselSample>();
	
	private Long timestamp;
	
	public OneSecondOfSamples(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}

	public ArrayList<BackgroundVesselSample> getVesselSamples() {
		return vesselSamples;
	}
}
