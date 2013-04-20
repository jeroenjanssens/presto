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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class AISToBackgroundConverter {

	private Background background = new Background();

	private ArrayList<String> directories = new ArrayList<String>();

	public void addDirectory(String dir) {
		directories.add(dir);
	}

	public void convert(String outputFile) {

		//parse each file in each directory
		for(String dir : directories) {
			File folder = new File(dir);
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
	
				if (listOfFiles[i].isFile()) {
					System.out.println("Processing file " + listOfFiles[i].getName());
					parseFile(dir + "\\" + listOfFiles[i].getName());
					System.out.println("Number of Seconds: " + background.getSamples().size());
					System.out.println("Number of Vessels: " + background.getVessels().size());
				}
			}
		}

		//write object to file

		FileOutputStream fos = null;
		GZIPOutputStream gos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(outputFile);
			gos = new GZIPOutputStream(fos);
			out = new ObjectOutputStream(gos);
			out.writeObject(background);
			out.flush();
			gos.finish();
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

	}

	private void parseFile(String fileName) {
		BufferedReader in = null;
		String line = new String();

		int sc = 0, ve = 0, cs = 0, ta = 0, ps = 0;

		try {
			in = new BufferedReader(new FileReader(fileName));
			String[] splits;
			String message = "";
			String date = "";
			String temp = "";


			line = in.readLine();


			while(line != null) {
				splits = line.split("!AIVDM");
				if(splits.length > 1) {
					temp = splits[0];

					if(splits[1].startsWith(",1")) {
						splits = splits[1].split(",");
						message = splits[5];
					}

					else if(splits[1].startsWith(",2")) {
						splits = splits[1].split(",");
						message = splits[5];
						splits = in.readLine().split("!AIVDM");
						splits = splits[1].split(",");
						message = message.concat(splits[5]);
					}

					splits = temp.split("\\^UT "); 
					if(splits.length > 1) {
						splits = splits[1].split("\\^EOH");
						splits = splits[0].split("\\.");
						date = splits[0];
						date = date.concat("000");
					}                               

					//AISState test2;

					if(AISMessage.getMessageType(message) == AISMessage.STATIC_AND_VOYAGE_REPORT) {
						StaticAISMessage test3 = new StaticAISMessage(message, Long.parseLong(date));

					} else if (AISMessage.getMessageType(message) == AISMessage.POSITION_REPORT_1 
							|| AISMessage.getMessageType(message) == AISMessage.POSITION_REPORT_2
							|| AISMessage.getMessageType(message) == AISMessage.POSITION_REPORT_3) {

						DynamicAISMessage test2 = new DynamicAISMessage(message, Long.parseLong(date));
						
						BackgroundVessel v = background.getVessel(test2.getUserID());
						BackgroundVesselSample bgvs = new BackgroundVesselSample(v, test2.latitude, test2.longitude, test2.trueHeading);
						background.addSample(bgvs, test2.getTimestamp());

					} 

				}

				line = in.readLine();
			}    

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(fileName);
			System.out.println(line);
		} 
	}


	public static void main(String[] args) {
		AISToBackgroundConverter abc = new AISToBackgroundConverter();
		abc.addDirectory("C:\\Documents and Settings\\Jeroen\\My Documents\\PhD\\Poseidon\\SFRD\\data\\raw_ais\\20070607");
		abc.convert("C:\\2007-06-07.bgv");
	}
}
