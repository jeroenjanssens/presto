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
import java.util.HashMap;
import java.util.TreeMap;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Background implements Serializable {

	private static final long serialVersionUID = -358200020776330190L;

	private HashMap<Integer, BackgroundVessel> vessels = new HashMap<Integer, BackgroundVessel>(1000);
	private TreeMap<Long, OneSecondOfSamples> samples = new TreeMap<Long, OneSecondOfSamples>();
	
	public HashMap<Integer, BackgroundVessel> getVessels() {
		return vessels;
	}
	
	public TreeMap<Long, OneSecondOfSamples> getSamples() {
		return samples;
	}
	
	public void addSample(BackgroundVesselSample bgvs, long timestamp) {
		Long second = new Long(timestamp / 1000);
		
		if(samples.containsKey(second)) {
			samples.get(second).getVesselSamples().add(bgvs);
		} else {		
			OneSecondOfSamples one = new OneSecondOfSamples(second);
			one.getVesselSamples().add(bgvs);	
			samples.put(second, one);
		}	
	}
	
	public BackgroundVessel getVessel(int id) {
		Integer i = new Integer(id);
		
		if(vessels.containsKey(i)) {
			return vessels.get(i);
		} else {
			BackgroundVessel bgv = new BackgroundVessel();
			bgv.setIMONumber(id);
			vessels.put(i, bgv);
			return bgv;
		}
	}
	
}
