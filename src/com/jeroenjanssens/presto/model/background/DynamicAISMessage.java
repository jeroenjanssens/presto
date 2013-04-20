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

public class DynamicAISMessage extends AISMessage {

    public static String[] NAV_STATUS = {"Under Way Using Engine",
                                         "At Anchor",
                                         "Not Under Command",
                                         "Restricted Manoeuvrability",
                                         "Constrained By Her Draught",
                                         "Moored",
                                         "Aground",
                                         "Engaged In Fishing",
                                         "Under Way Sailing",
                                         "Reserved",
                                         "Reserved",
                                         "Reserved",
                                         "Reserved",
                                         "Reserved",
                                         "Reserved",
                                         "Not Defined"};

    private byte navStatus;
    private int rateOfTurn;
    private int speedOverGround;
    private boolean positionAccuracy;
    public double longitude;
    public double latitude;
    private int courseOverGround;
    public int trueHeading;
    private byte timeStamp;
    private byte reservedForReg;
    private boolean spare;
    private boolean RAIMFlag;
    private int comState;


    public DynamicAISMessage(String message, long timestamp) {
        super(message, timestamp);
        parseBitString();
    }

    // UGLY as hell, but cba to think of a general method

    private void parseBitString() {
        String bits = getBitString();

        // 4 bits
        navStatus = (byte) bits2Int(bits.substring(38, 42), false);

        // 8 bits, needs special conversion formula
        rateOfTurn = bits2Int(bits.substring(42, 50), true);

        //rateOfTurn = 126;
        //System.out.println((rateOfTurn/(double) 127) * 720);
        rateOfTurn = (rateOfTurn > 0) ? (int) Math.round( (rateOfTurn * rateOfTurn) / (4.733 * 4.733) ) : -1 * (int) Math.round( (rateOfTurn * rateOfTurn) / (4.733 * 4.733) );


        // 10 bits
        speedOverGround = bits2Int(bits.substring(50, 60), false);

        // 1 bit
        positionAccuracy = (bits2Int(bits.substring(60, 61), false) == 1) ? true : false;

        // 28 bits, convert to degrees, 60 minutes in a degree, its given in 1/10000 min, so divide by 600000
        longitude = bits2Int(bits.substring(61, 89), true) / (double) 600000.0;

        // 27 bits
        latitude = bits2Int(bits.substring(89, 116), true) / (double) 600000.0;

        // 12 bits
        courseOverGround = bits2Int(bits.substring(116, 128), false);

        // 9 bits
        trueHeading = bits2Int(bits.substring(128, 137), false);

        // 6 bits
        timeStamp = (byte) bits2Int(bits.substring(137, 143), false);

        // 4 bits
        reservedForReg = (byte) bits2Int(bits.substring(143, 147), false);

        // 1 bit
        spare = (bits2Int(bits.substring(147, 148), false) == 1) ? true : false;

        // 1 bit
        RAIMFlag = (bits2Int(bits.substring(148, 149), false) == 1) ? true : false;

        // 19 bits
        comState = bits2Int(bits.substring(149, 168), false);
    }

    public void printAll() {
        super.printAll();
        System.out.print("Nav Status: "); System.out.println(NAV_STATUS[navStatus]);
        System.out.print("RoT: "); System.out.println(rateOfTurn);
        System.out.print("SoG "); System.out.println(speedOverGround);
        System.out.print("Position Accuracy: "); System.out.println(positionAccuracy);
        System.out.print("Longitude: "); System.out.println(longitude);
        System.out.print("Latitude: "); System.out.println(latitude);
        System.out.print("CoG: "); System.out.println(courseOverGround);
        System.out.print("True Heading: "); System.out.println(trueHeading);
        System.out.print("Time Stamp: "); System.out.println(timeStamp);
        System.out.print("Reg Reserved: "); System.out.println(reservedForReg);
        System.out.print("Spare: "); System.out.println(spare);
        System.out.print("RAIM-flag: "); System.out.println(RAIMFlag);
        System.out.print("Com State: "); System.out.println(comState);
    }

    public String fileString() {
    	
    	return super.fileString() + "," + navStatus + "," + rateOfTurn + "," + speedOverGround + "," + 
    		   positionAccuracy + "," + longitude + "," + latitude + "," + courseOverGround +
    		   "," + trueHeading + "," + timeStamp + "," + reservedForReg + "," + 
    		   spare + "," + RAIMFlag + "," + comState;
    	
    }
    
    
    
}
