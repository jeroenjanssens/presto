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

import java.util.HashMap;
import java.util.Map;

import com.jeroenjanssens.presto.sailing.ais.AISParameter;
import com.jeroenjanssens.presto.sailing.ais.AISState;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class AISProtocol implements IProtocol {
	/* Generates a file with NMEA strings of AIS data, such as: 
	 * !AIVDM,1,1,,A,13u?etPv2;0n:dDPwUM1U1Cb069D,0*24
	 *
	 * We actually put something like "^M^M rawtostampedMsg^L 48^UT 1180690080.706^EOH^" before 
	 * to fit our tool.
	 */
	private double constraintHeading(double heading) {
		while(heading > 359) heading -= 360;
		while(heading < 0) heading += 360;
		return heading;
	}
	
	/* Works only with up to 31 bits (for speed reasons) */
	public static int bits2Int(String bits, boolean signed) {
		int temp = 0;

		// TODO: maybe Integer.valueOf(bits, 2) is faster?
		if (signed && (bits.charAt(0) == '1')) {
			// System.out.println(bits);
			temp = -(1 << (bits.length() - 1));
			bits = bits.substring(1);
		}

		int exp = bits.length() - 1;
		for (int i = 0; i < bits.length(); i++) {
			if (bits.charAt(i) == '1')
				temp += 1 << exp;
			exp--;
		}

		return temp;
	}
	
	/* Convert an unsigned integer to binary of maximum length
	 * Should work equally well with signed integer on 2-complement.
	 * Works only with up to 32 bits (for speed reasons) */
	public static String int2Bits(int n, int length/*, boolean signed*/) {
		//String bin = Integer.toBinaryString(n + (1 << 31)); // TODO: we could add 2^31 and never have the if
		String bin = Integer.toBinaryString(n); // TODO: we could add 2^31 and never have the if
		if (bin.length() >= length)
			return bin.substring(bin.length() - length, bin.length()); // remove the highest chars
		else {
			// add chars
			String pad = "0000000000000000000000000000000000";
			return pad.substring(0, length - bin.length()) + bin;
		}
	}

	
	/* Convert NMEA message into a string of bits.
	 * The output string is just the size needed to contain the input string.
	 */
	public static String NMEA2Bits(String message) {
		int temp = 0;
		String bits = new String();

		for (int i = 0; i < message.length(); i++) {
			// get char and convert to int
			temp = message.charAt(i);

			// subtract 48 (110000) to convert back to 6 bit
			temp = temp - 48;

			// if still higher than 40 subtract another 8, since we skip a bit
			// in the ASCII table (GOD knows why...)
			if (temp > 40) {
				temp = temp - 8;
			}

			// add 64 (1000000) to easily add leading zeros, remove the 7th bit
			// again by taking the substring
			bits += Integer.toBinaryString(temp | (1 << 6)).substring(1,7);
		}
		return bits;
	}
	
	
	/* Convert a string for NMEA message into a string of bits.
	 * The output string is just the size needed to contain the input string.
	 * char 32-63 -> direct mapping
	 * char 64-95 -> 0-31
	 */
	public static String String2Bits(String message) {
		int temp = 0;
		String bits = new String();

		for (int i = 0; i < message.length(); i++) {
			// get char and convert to int
			temp = message.charAt(i);
			if ((temp < 32) || (temp > 95))
				temp = '@'; // illegal char

			// subtract 48 (110000) to convert back to 6 bit
			if (temp > 63)
			    temp -= 64;

			// add 64 (1000000) to easily add leading zeros, remove the 7th bit
			// again by taking the substring
			bits += Integer.toBinaryString(temp | (1 << 6)).substring(1,7);
		}
		return bits;
	}
	
	/* Convert String for NMEA message into a string of bits.
	 * length is in bits, must be multiple of 6
	 * The size of output string is exactly length specified, filled with " ".
	 */
	public static String String2Bits(String message, int length) {
		String m = message;
		int l = length / 6;
		
		if ((length % 6) != 0) {
			// TODO exception?
			System.err.println("NMEA binary must be a multiple of 6, and the length is " + length + ".");
		}
		if (m.length() >= l)
			m = m.substring(m.length() - l, m.length()); // remove the highest chars
		else {
			// add chars
			for (; m.length() < l;)
				m = " " + m;
		}
		
		String bits = String2Bits(m);
		if (bits.length() != length) {
			// TODO exception?
			System.err.println("Wrong length of " + bits.length() + ".");
		}
		return bits;
	}
	
	/* Encode a string of 0 or 1 into an NMEA string (6 bits per char) */
	private String bits2NMEA(String bits) {
		int chr;
		if ((bits.length() % 6) != 0) {
			// TODO exception?
			System.err.println("NMEA binary must be a multiple of 6, and the length is " + bits.length() + ".");
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < bits.length(); i += 6) {
			chr = bits2Int(bits.substring(i, i + 6), false);
			// Never use ascii btw 88 t/m 95
			if (chr >= 40) {
				chr += 8;
			}
			chr += 48;

			result.append((char) chr);
		}

		return result.toString();
	}

	/* Encode a NMEA message of type 5 (aka static) 
	 * cf http://www.bosunsmate.org/ais/message5.php
	 * http://gpsd.berlios.de/AIVDM.html
	 */
	private String encodeMessageStatic(AISState aisMessage, int mmsi) {
		// For ease, we first put everything in a big String full of 0 or 1
		// Then we convert it to a binary value encoded as a string
		String bits = "";
		// Message type (6 bits)
		bits += int2Bits(5, 6);
		// repeat indicator (2 bits)
		bits += int2Bits(0, 2);
		// MMSI (30 bits)
		bits += int2Bits(mmsi, 30);
		// AIS version (2 bits)
		bits += int2Bits((Integer)aisMessage.getSafe(AISParameter.AIS_VERSION), 2);
		// IMO number (30 bits)
		bits += int2Bits((Integer)aisMessage.getSafe(AISParameter.IMO), 30);
		// Call sign (42 bit = 7 char)
		// All @ = not available = "@@@@@@@"
		bits += String2Bits((String)aisMessage.getSafe(AISParameter.CALLSIGN), 42);
		// Name (120 bits = 20 char)
		// All @ = not available = "@@@@@@@@@@@@@@@@@@@@"
		bits += String2Bits((String)aisMessage.getSafe(AISParameter.NAME), 120);
		// Ship type (8 bits)
		bits += int2Bits((Integer)aisMessage.getSafe(AISParameter.SHIP_TYPE), 8);
		// Dimension (30 bits) = 9 + 9 + 6 + 6 (meters)
		bits += int2Bits(((Double)aisMessage.getSafe(AISParameter.LENGTH_A)).intValue(), 9);
		bits += int2Bits(((Double)aisMessage.getSafe(AISParameter.LENGTH_B)).intValue(), 9);
		bits += int2Bits(((Double)aisMessage.getSafe(AISParameter.WIDTH_A)).intValue(), 6);
		bits += int2Bits(((Double)aisMessage.getSafe(AISParameter.WIDTH_B)).intValue(), 6);
		// Type of EPF device (4 bits)
		// 0 = undefined
		bits += int2Bits(0, 4);
		// ETA (20 bit) MMDDHHMM in UTC = 5 * 4
		// TODO getSafe need to be fixed
//		Date eta = ((CDateTime)aisMessage.getSafe(AISParameter.ETA)).getSelection();
//		bits += int2Bits(eta.getMonth(), 5);
//		bits += int2Bits(eta.getDay(), 5);
//		bits += int2Bits(eta.getHours(), 5);
//		bits += int2Bits(eta.getMinutes(), 5);
		bits += int2Bits(0, 20);
		// Draught (8 bit)
		bits += int2Bits((int)(((Double)aisMessage.getSafe(AISParameter.DRAUGHT)) * 1.0), 8);
		// Destination (120 bits = 20 char)
		// All @ = not available = "@@@@@@@@@@@@@@@@@@@@"
		bits += String2Bits((String)aisMessage.getSafe(AISParameter.DESTINATION), 120);
		// DTE (1 bit)
		bits += int2Bits((Boolean)aisMessage.getSafe(AISParameter.DTE)?1:0, 1);
		// Spare (1 bit)
		bits += int2Bits(0, 1);
		// Not included (2 bits)
		bits += int2Bits(0, 2);
		//System.err.println("NMEA is " + bits.length() + ", and it should be 424 + 2");
		return bits2NMEA(bits);
	}

	/* Encode a NMEA message of type 3 (aka dynamic) (it could also be 1 or 2)
	 * for more info see http://www.bosunsmate.org/ais/message3.php
	 * http://www.navcen.uscg.gov/enav/ais/AIS_Messages_B.htm
	 */
	private String encodeMessageDynamic(AISState aisMessage, int mmsi) {
		// For ease, we first put everything in a big String full of 0 or 1
		// Then we convert it to a binary value encoded as a string
		String bits = "";
		// Message type (6 bits)
		bits += int2Bits(3, 6);
		// repeat indicator (2 bits)
		bits += int2Bits(0, 2);	
		// MMSI (30 bits)
		bits += int2Bits(mmsi, 30);
		// Nav status (4 bits)
		bits += int2Bits((Integer)aisMessage.getSafe(AISParameter.NAV_STATUS), 4);
		// Rate of turn (8 bits)
		Double rot = (Double)aisMessage.getSafe(AISParameter.RATE_OF_TURN); // TODO what's the unit of this in com.jeroenjanssens.presto?
		int rotCoded = (int)(4.733 * Math.sqrt(rot)); // with rot in deg/min 
		// + 127 = turning right at 720 degrees per minute or higher; - 127 = turning left at 720 degrees per minute or higher. 
		bits += int2Bits(rotCoded, 8);
		// Speed over ground (10 bits)
		int sog = (int)(((Double)aisMessage.getSafe(AISParameter.SPEED_OVER_GROUND)) * 10);
		if (sog > 1022) // blocks at 102.2 k/h
			sog = 1022;
		bits += int2Bits(sog, 10);
		// Position accuracy (1 bit)
		bits += int2Bits((Boolean)aisMessage.getSafe(AISParameter.POSITION_ACCURACY)?1:0, 1);
		// Longitude (28 bits, signed)
		bits += int2Bits((int)((Double)aisMessage.getSafe(AISParameter.LONGITUDE) * 600000.0), 28); // TODO it fails
		// Latitude (27 bits, signed)
		bits += int2Bits((int)((Double)aisMessage.getSafe(AISParameter.LATITUDE) * 600000.0), 27); // TODO it fails
		// Course over ground (12 bits)
		bits += int2Bits((int)(constraintHeading((Double)aisMessage.getSafe(AISParameter.COURSE_OVER_GROUND)) * 10), 12);
		// true heading (9 bits)
		// 511 = not available
		bits += int2Bits((int)((Double)aisMessage.getSafe(AISParameter.TRUE_HEADING) * 1.0), 9);
		// Stamp UTC second when the report was generated  (6 bits)
		bits += int2Bits((int) ((aisMessage.getTime() / 1000) % 60), 6);
		// Reserved (4 bits)
		bits += int2Bits(0, 4);
		// Spare (1 bit)
		bits += int2Bits(0, 1);
		// RAIM (1 bit)
		bits += int2Bits((Boolean)aisMessage.getSafe(AISParameter.RAIMFLAG)?1:0, 1);
		// Comm state (19 bits)
		// A constant
		bits += "1100000000000000110";
		//System.err.println("NMEA is " + bits.length() + ", and it should be 168");
		return bits2NMEA(bits);
	}

	/* Compute the 2 character checksum of the nmea
	 * An NMEA checksum is a 2 digit hex number calculated as the XOR of
	 * bytes between (but not including) the dollar sign (or !) and asterisk.
	 */
	private String nmeaChecksum(String nmea) {
		int checksum = 0;
		String inner = nmea.substring(nmea.indexOf("!") + 1, nmea.indexOf("*"));
		for (byte c : inner.getBytes()) {
			checksum ^= c;
		}
		return Integer.toHexString(checksum & 0xff | 0x100).toUpperCase().substring(1); // Add 0x100 to force a format XX
	}

	/*
	 * Construct the full line for a AIS static message (= 2 messages)
	 */
	public String constructStaticMesssage(AISState aisMessage, int mmsi) {
		String s = "";
		String nmea1 = "";
		String nmea2 = "";
		// Pre Header (special for our case)
		// It contains some special info, but we care only about the time!
		s += "^M^M rawtostampedMsg^L 113";
		//Time (number of second from UTC, with 1000th of second separated by a "."
		String time = Long.toString(aisMessage.getTime());
		s += "^UT " + time.substring(0, time.length() - 3) + "." + time.substring(time.length() - 3);
		// End
		s += "^EOH^";

		// The encoded AIS data (the meat)
		String payload = encodeMessageStatic(aisMessage, mmsi);
		
		// First part (60 chars)
		// Header
		nmea1 += "!AIVDM";
		// Number of sentences
		nmea1 += ",2";
		// Sentence number
		nmea1 += ",1";
		// Sentence ID
		nmea1 += ",1";
		// Channel (A or B): we don't care
		nmea1 += ",A";
		// The encoded AIS data (the meat)
		nmea1 += "," + payload.substring(0, 60);
		// End of the data
		nmea1 += ",0*";
		// NMEA checksum
		nmea1 += nmeaChecksum(nmea1);
		s += nmea1 + "\n";

		// Second part (11 chars)
		// Header
		nmea2 += "!AIVDM";
		// Number of sentences
		nmea2 += ",2";
		// Sentence number
		nmea2 += ",2";
		// Sentence ID, the same ?!
		nmea2 += ",1";
		// Channel (A or B): we don't care
		nmea2 += ",A";
		// The encoded AIS data (the meat)
		nmea2 += "," + payload.substring(60);
		// End of the data, the last two bits are not used
		nmea2 += ",2*";
		// NMEA checksum
		nmea2 += nmeaChecksum(nmea2);
		s += nmea2 + "\n";

		return s;
	}
	
	/*
	 * Construct the full line for a AIS dynamic message
	 */
	public String constructDynamicMesssage(AISState aisMessage, int mmsi) {
		String s = "";
		String nmea = "";
		// Pre Header (special for our case)
		// It contains some special info, but we care only about the time!
		s += "^M^M rawtostampedMsg^L 48";
		//Time (number of second from UTC, with 1000th of second separated by a "."
		String time = Long.toString(aisMessage.getTime());
		s += "^UT " + time.substring(0, time.length() - 3) + "." + time.substring(time.length() - 3);
		// End
		s += "^EOH^";

		// Header
		nmea += "!AIVDM";
		// Number of sentences, only 1
		nmea += ",1";
		// Sentence number, only 1
		nmea += ",1";
		// Sentence ID, nothing
		nmea += ",";
		// Channel (A or B): we don't care
		nmea += ",A";
		// The encoded AIS data (the meat)
		nmea += "," + encodeMessageDynamic(aisMessage, mmsi);
		// End of the data
		nmea += ",0*";
		// NMEA checksum
		nmea += nmeaChecksum(nmea);

		s += nmea + "\n";
		return s;
	}

	
	/*
	 * Generate the messages in NMEA - AIS style
	 * It also respect (more or less) the standard concerning the frequency of Static and dynamic info messages. 
	 */
	public String constructMesssage(AISState aisMessage) {
		String s = "";
		long date = aisMessage.getTime(); // in ms
		Ship ship = getShip(aisMessage);
		
		// Dynamic message : frequency depends on the speed, but in general every 7s is ok
		if ((ship.lastDynamic + 7000) < date) {
			s += constructDynamicMesssage(aisMessage, ship.MMSI);
			ship.lastDynamic = date;
		}
		// Static message : every 3m
		if ((ship.lastStatic + 3 * 60 * 1000) < date) {
			s += constructStaticMesssage(aisMessage, ship.MMSI);
			ship.lastStatic = date;
		}
		return s;
	}

	public String getHeader() {
		// No header for NMEA
		return null;
	}

	class Ship {
		public int MMSI;
		public int IMO;
		public long lastStatic = 0;
		public long lastDynamic = 0;
	}
	
	Map<Integer, Ship> listShips = new HashMap<Integer, Ship>(); // IMO/Ship
	/*
	 * Return a ship object corresponding to the AIS message
	 * If not ship object exists, a new one is created
	 */
	private Ship getShip(AISState aisMessage) {
		int imo = (Integer)aisMessage.getSafe(AISParameter.IMO);
		Ship ret = listShips.get(imo);
		
		// create a new ship if not yet existing
		if (ret == null) {
			ret = new Ship();
			// Generate a possible MMSI, out of the IMO so it is different for each ship
			ret.MMSI = 200000000 + imo;
			ret.IMO = imo;
			listShips.put(imo, ret);
		}
		return ret;
	}
	
}
