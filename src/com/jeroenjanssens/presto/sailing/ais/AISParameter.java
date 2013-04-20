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

package com.jeroenjanssens.presto.sailing.ais;

import org.eclipse.nebula.widgets.cdatetime.CDateTime;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public enum AISParameter {
	//Static parameters:
	IMO 				("IMO Number", false, Integer.class),
	SHIP_TYPE 			("Ship Type", false, Integer.class),
	CALLSIGN			("Call Sign", false, String.class),
	NAME				("Name", false, String.class),
	AIS_VERSION			("AIS Version", false, Integer.class),
	LENGTH_A			("Length A", false, Double.class),
	LENGTH_B			("Length B", false, Double.class),
	WIDTH_A				("Width A", false, Double.class),
	WIDTH_B				("Width B", false, Double.class),
	ETA					("ETA", false, CDateTime.class),
	DRAUGHT				("Draught", false, Double.class),
	DTE					("DTE", false, Boolean.class),
	S_SPARE				("Static spare", false, Boolean.class),
	DESTINATION			("Destination", false, String.class),
	EPF_DEVICE			("EPF Device", false, String.class),
	
	//Dynamic parameters:
	LATITUDE			("Latitude", true, Double.class),
	LONGITUDE			("Longitude", true, Double.class),
	COURSE_OVER_GROUND	("Course Over Ground", true, Double.class),
	TRUE_HEADING		("True Heading", true, Double.class),
	SPEED_OVER_GROUND 	("Speed Over Ground", true, Double.class),
	RATE_OF_TURN		("Rate Of Turn", true, Double.class),
	NAV_STATUS			("Navigation Status", true, AISNavigationStatus.class),
	POSITION_ACCURACY 	("Position Accuracy", true, Boolean.class),
	RESERVED_FOR_REG	("Reserved", true, String.class),
	SPARE				("Dynamic Spare", true, String.class),
	RAIMFLAG			("RAIM Flag", true, Boolean.class),
	COM_STATE			("Communication State", true, String.class);
	
	private final String textName;
	private final boolean isDynamic;
	private final Class<?> type;
	
	AISParameter(String textName, boolean isDynamic, Class<?> type) {
		this.textName = textName;
		this.isDynamic = isDynamic;
		this.type = type;
	}
	
	public static AISParameter fromString(String text) {
		for(AISParameter aisParameter : AISParameter.values()) {
			if(aisParameter.getTextName().equals(text)) {
				return aisParameter;
			}
		}
		return null;
	}
	
	public String getTextName() {
		return textName;
	}
	
	public boolean isDynamic() {
		return isDynamic;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	
}
