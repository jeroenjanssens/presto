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

package com.jeroenjanssens.presto.sailing;

import com.jeroenjanssens.presto.sailing.ais.AISParameter;
import com.jeroenjanssens.presto.sailing.ais.AISState;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class CSVProtocol implements IProtocol {

	private double constraintHeading(double heading) {
		while(heading > 359) heading -= 360;
		while(heading < 0) heading += 360;
		return heading;
	}
	
	public String getHeader() {
		String s = "";
		
		s += "Date and time, ";
		s += AISParameter.IMO.getTextName() + ", ";
		s += AISParameter.LATITUDE.getTextName() + ", ";
		s += AISParameter.LONGITUDE.getTextName() + ", ";
		s += AISParameter.SHIP_TYPE.getTextName() + ", ";
		s += AISParameter.COURSE_OVER_GROUND.getTextName() + ", ";
		s += AISParameter.SPEED_OVER_GROUND.getTextName() + ", ";
		s += AISParameter.RATE_OF_TURN.getTextName() + "";
		
		s += "\n";
		return s;
	}
	
	public String constructMesssage(AISState aisMessage) {
		String s = "";
		
		s += aisMessage.getTime() + ",";
		s += aisMessage.getSafe(AISParameter.IMO) + ",";
		s += aisMessage.getSafe(AISParameter.LATITUDE) + ",";
		s += aisMessage.getSafe(AISParameter.LONGITUDE) + ",";
		s += aisMessage.getSafe(AISParameter.SHIP_TYPE) + ",";
		s += constraintHeading((Double)aisMessage.getSafe(AISParameter.COURSE_OVER_GROUND)) + ",";
		s += aisMessage.getSafe(AISParameter.SPEED_OVER_GROUND) + ",";
		s += aisMessage.getSafe(AISParameter.RATE_OF_TURN) + "";
		
		s += "\n";
		return s;
	}
}
