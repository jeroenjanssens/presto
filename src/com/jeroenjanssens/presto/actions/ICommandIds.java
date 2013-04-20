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

package com.jeroenjanssens.presto.actions;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public interface ICommandIds {

	public static final String CMD_NEW = "com.jeroenjanssens.presto.actions.newscenario";
    public static final String CMD_OPEN = "com.jeroenjanssens.presto.actions.open";
    public static final String CMD_OPEN_MESSAGE = "com.jeroenjanssens.presto.actions.openmessage";
    
    public static final String CMD_NAVIGATION = "com.jeroenjanssens.presto.actions.navigation";
    public static final String CMD_TRACKSELECTION = "com.jeroenjanssens.presto.actions.trackselection";
    public static final String CMD_VERTEXSELECTION = "com.jeroenjanssens.presto.actions.vertexselection";
    public static final String CMD_DRAWTRACK = "com.jeroenjanssens.presto.actions.drawtrack";
    public static final String CMD_INSERTVERTEX = "com.jeroenjanssens.presto.actions.insertvertex";
    public static final String CMD_REMOVEVERTEX = "com.jeroenjanssens.presto.actions.removevertex";
    //public static final String CMD_FREETRANSFORM = "com.jeroenjanssens.presto.actions.freetransform";
    
    public static final String CMD_RESETEARTHVIEW = "com.jeroenjanssens.presto.actions.resetearthview";
    public static final String CMD_EXPORT = "com.jeroenjanssens.presto.actions.export";
    public static final String CMD_LOADBACKGROUND = "com.jeroenjanssens.presto.actions.loadbackground";
    
    public static final String CMD_CREATE = "com.jeroenjanssens.presto.actions.create";
}
