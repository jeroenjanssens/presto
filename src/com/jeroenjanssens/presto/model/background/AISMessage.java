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

import java.util.Date;

public class AISMessage {
    public static byte POSITION_REPORT_1 = 1;
    public static byte POSITION_REPORT_2 = 2;
    public static byte POSITION_REPORT_3 = 3;
    public static byte STATIC_AND_VOYAGE_REPORT = 5;
    
    public static long REFERENCE_TIMESTAMP = 1180648800000L;

    private String bitString;
    private byte messageID;
    private byte repeatIndicator;
    private int userID;
    private Date date;

    private long timestamp;
    
    public long getTimestamp() {
		return timestamp;
	}

	public AISMessage() {}

    public AISMessage(String message, long timestamp) {
    	this.timestamp = timestamp;
        bitString = parseMessage(message);
        date = new Date(timestamp);
        parseStartFields();
    }

    // Convert ASCII message into a string of bits.
    public static String parseMessage(String message) {
        int temp = 0;
        String bits = new String();

        for (int i = 0; i < message.length(); i++) {
            // get char and convert to int
            temp = (int) message.charAt(i);

            // subtract 48 (110000) to convert back to 6 bit
            temp = temp - 48;

            // if still higher than 40 subtract another 8, since we skip a bit in the ASCII table (GOD knows why...)
            if(temp > 40) {
                temp = temp - 8;
            }

            // add 64 (1000000) to easily add leading zeros, remove the 7th bit again by taking the substring
            bits = bits.concat(Integer.toBinaryString(temp + 64).substring(1));
        }
        return bits;
    }

    // Ugly, but what the hell
    private void parseStartFields() {
        // MessageID, first 6 bits
        messageID = (byte) bits2Int(bitString.substring(0, 6), false);
        // next 2 bits
        repeatIndicator = (byte) bits2Int(bitString.substring(6, 8), false);
        // MMSI 30 bits
        userID = bits2Int(bitString.substring(8, 38), false);

        //System.out.println(messageID);
        //System.out.println(repeatIndicator);
        //System.out.println(userID);
    }

    public static int bits2Int(String bits, boolean signed) {
        int temp = 0;

        if (signed && Character.getNumericValue(bits.charAt(0)) == 1) {
            // System.out.println(bits);
            temp = -1 * (int) Math.pow(2, (bits.length()-1));
            bits = bits.substring(1);
        }

        for (int i = 0; i < bits.length(); i++) {
            temp += Character.getNumericValue(bits.charAt(i)) * (Math.pow(2, ((bits.length() - i) - 1)));
            //System.out.print(bits.charAt(i) + "--"); System.out.println(Math.pow(2, ((bits.length() - i) - 1)));
        }

        return temp;
    }

    public static String parse6BitASCII(String bits) {
        int chr;
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < bits.length(); i += 6) {
            chr = bits2Int(bits.substring(i, i+6), false);

            // 6-ascii doesn't correspond to normal ascii below char 32, so we shift to the part it corresponds to.
            if(chr < 32) {
                chr += 64;
            }

            // Remove comma's they are gonna f*k the shipfiles up...
            if((char) chr == ',') {
            	// underscore (_)
            	chr = 95;
            	
            }
            result.append((char)chr);
        }

        return result.toString();
    }


    // Return the messageID from the ASCII string
    public static byte getMessageType(String message) {
        return (byte) Character.getNumericValue(message.charAt(0));
    }

    public String getBitString() {
        return bitString;
    }

    public byte getMessageID() {
        return messageID;
    }

    public byte getRepeatIndicator() {
        return repeatIndicator;
    }

    public int getUserID() {
        return userID;
    }

    public Date getDate() {
        return date;
    }

    public void printAll() {

    }
    
    public String fileString() {
    	return "" + date.getTime() + "," + messageID + "," + repeatIndicator + "," + userID;	
    }

}
