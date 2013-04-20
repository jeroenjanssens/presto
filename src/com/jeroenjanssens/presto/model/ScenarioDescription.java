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

import java.util.Date;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class ScenarioDescription {

	private String name = "";
	private String author = "";
	private int difficulty = 5;
	private long created = new Date().getTime();
	private String descriptionText = "";
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String description) {
		this.descriptionText = description;
	}

	public ScenarioDescription() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String toXML() {
		String xml = "\t<description>\n";
		xml += "\t\t<name>" + this.name + " </name>\n";
		xml += "\t\t<author>" + this.author + " </author>\n";
		xml += "\t\t<difficulty>" + this.difficulty + "</difficulty>\n";
		xml += "\t\t<created>" + this.created + "</created>\n";
		xml += "\t\t<text><![CDATA[" + this.descriptionText + " ]]></text>\n";
		xml += "\t</description>\n";
		return xml;
	}
}
