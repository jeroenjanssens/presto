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

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public enum AISNavigationStatus {
	UNDER_WAY_ENGINE	(0, "Under Way Using Engine"),
    AT_ANCHOR			(1, "At Anchor"),
    NOT_UNDER_COMMAND	(2, "Not Under Command"),
    RESTRICTED_MAN		(3, "Restricted Manoeuvrability"),
    CONSTR_DRAUGHT		(4, "Constrained By Her Draught"),
    MOORED				(5, "Moored"),
    AGROUND				(6, "Aground"),
    ENGAGED_FISHING		(7, "Engaged In Fishing"),
    UNDER_WAY_SAILING	(8, "Under Way Sailing"),
    RESERVED_1			(9, "Reserved 1"),
    RESERVED_2			(10, "Reserved 2"),
    RESERVED_3			(11, "Reserved 3"),
    RESERVED_4			(12, "Reserved 4"),
    RESERVED_5			(13, "Reserved 5"),
    RESERVED_6			(15, "Reserved 6"),
    NOT_DEFINED			(16, "Not Defined");
	
	private final int id;
	private final String text;
	
	AISNavigationStatus(int id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
}
