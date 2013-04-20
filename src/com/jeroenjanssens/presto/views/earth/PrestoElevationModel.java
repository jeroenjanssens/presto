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

package com.jeroenjanssens.presto.views.earth;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class PrestoElevationModel implements ElevationModel {

	@Override
	public Double getBestElevation(Angle latitude, Angle longitude) {
		return 0.0;
	}

	@Override
	public Elevations getBestElevations(Sector sector) {
		return new PrestoElevations();
	}

	@Override
	public double getElevation(Angle latitude, Angle longitude) {
		return 0.0;
	}

	@Override
	public Double getElevationAtResolution(Angle latitude, Angle longitude,
			int resolution) {
		return 0.0;
	}

	@Override
	public Elevations getElevations(Sector sector, int resolution) {
		return new PrestoElevations();
	}

	@Override
	public Elevations getElevationsAtResolution(Sector sector, int resolution) {
		return new PrestoElevations();
	}

	@Override
	public double getMaxElevation() {
		return 0.0;
	}

	@Override
	public double[] getMinAndMaxElevations(Sector sector) {
		return new double[] {0,0};
	}

	@Override
	public double getMinElevation() {
		return 0;
	}

	@Override
	public int getTargetResolution(Globe globe, double size) {
		return 0;
	}

	@Override
	public int getTargetResolution(DrawContext dc, Sector sector, int density) {
		return 0;
	}


	public int getTileCountAtResolution(Sector sector, int resolution) {
		return 0;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(boolean enable) {
	
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
	}

	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		
	}

	@Override
	public AVList clearList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVList copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Entry<String, Object>> getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeKey(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValues(AVList avList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}
	
	 private static class PrestoElevations implements ElevationModel.Elevations {

		@Override
		public double getElevation(double latRadians, double lonRadians) {
			return 0;
		}

		@Override
		public short[] getExtremes() {
			return new short[] {0,0};
		}

		@Override
		public int getResolution() {
			return 0;
		}

		@Override
		public Sector getSector() {
			return null;
		}

		@Override
		public boolean hasElevations() {
			return false;
		}

	 }
}
