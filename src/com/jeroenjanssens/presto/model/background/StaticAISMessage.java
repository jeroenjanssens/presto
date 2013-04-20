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


public class StaticAISMessage extends AISMessage {

    public static String[] SHIP_TYPE = {"",
                                        "Reserved",
                                        "Wing In Ground",
                                        "Vessel",
                                        "High Speed Craft",
                                        "Special Craft",
                                        "Passenger Ship",
                                        "Cargo Ship",
                                        "Tanker",
                                        "Other type"};

    public static String[] SHIP_TYPE_0 = {"All ships of this type",
                                        "Carrying DG, HS, or MP IMO hazard or pollutant category A",
                                        "Carrying DG, HS, or MP IMO hazard or pollutant category B",
                                        "Carrying DG, HS, or MP IMO hazard or pollutant category C",
                                        "Carrying DG, HS, or MP IMO hazard or pollutant category D",
                                        "Reserved",
                                        "Reserved",
                                        "Reserved",
                                        "Reserved",
                                        "No additional information"};

    public static String[] SHIP_TYPE_3 = {"Fishing",
                                        "Towing",
                                        "Towing and length of the tow exceeds 200m or breadth exceeds 25m",
                                        "Engaged in dredging or underwater operations",
                                        "Engaged in diving operations",
                                        "Engaged in military operations",
                                        "Sailing",
                                        "Pleasure Craft",
                                        "Reserved",
                                        "Reserved"};

    public static String[] SHIP_TYPE_5 = {"Pilot Vessel",
                                        "Search and Rescue Vessel",
                                        "Tug",
                                        "Port Tender",
                                        "Vessel with anti-pollution facilities or equipment",
                                        "Law Enforcement Vessel",
                                        "Local Vessel",
                                        "Local Vessel",
                                        "Medical Transports (as in 1949 Geneva Conv)",
                                        "Ship according to Resolution No 18"};

    private byte AISVersionIndicator;
    private int IMONumber;
    private String callSign;
    private String name;
    private int typeOfShip;
    private int lengthA, lengthB, widthA, widthB;
    private byte typeOfEPFDevice;
    private String ETA;
    //private Date ETA;
    private int draught;
    private String destination;
    private boolean DTE;
    private boolean spare;


    public StaticAISMessage(String message, long timestamp) {
        super(message, timestamp);
        parseBitString();
    }

    // UGLY as hell, but cba to think of a general method

    private void parseBitString() {
        String bits = getBitString();

        // 2 bits
        AISVersionIndicator = (byte) bits2Int(bits.substring(38, 40), false);

        // 30 bits
        IMONumber = bits2Int(bits.substring(40, 70), false);

        // 42 bits
        callSign = parse6BitASCII(bits.substring(70, 112));

        // 120 bits
        name = parse6BitASCII(bits.substring(112, 232));

        // 8 bits
        typeOfShip = bits2Int(bits.substring(232, 240), false);

        // 30 bits
        // Overall Dimensions, encoded in 4 parts, thus one can decode the reference position
        // lengthA, lengthB, widthA, widthB
        lengthA = bits2Int(bits.substring(240, 249), false);
        lengthB = bits2Int(bits.substring(249, 258), false);
        widthA = bits2Int(bits.substring(258, 264), false);
        widthB = bits2Int(bits.substring(264, 270), false);

        // 4 bits
        typeOfEPFDevice = (byte) bits2Int(bits.substring(270, 274), false);

        // 20 bits MMDDHHMM UTC, Ugly with concat(), but wth...
        String date = Integer.toString(bits2Int(bits.substring(274, 278), false));
        date = date.concat("/");
        date = date.concat(Integer.toString(bits2Int(bits.substring(278, 283), false)));
        date = date.concat(" ");
        date = date.concat(Integer.toString(bits2Int(bits.substring(283, 288), false)));
        date = date.concat(":");
        String t = Integer.toString(bits2Int(bits.substring(288, 294), false));
        t = (t.length() == 1) ? "0".concat(t) : t ;
        date = date.concat(t);
        ETA = date.concat(" UTC");


        /*DateFormat df = DateFormat.getDateInstance();
        try {
            ETA = df.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // ETA =

        // 8 bits
        draught = bits2Int(bits.substring(294, 302), false);

        // 120 bits
        destination = parse6BitASCII(bits.substring(302, 422));

        // 1 bit
        DTE = (bits2Int(bits.substring(422, 423), false) == 1) ? true : false;

        // 1 bit
        spare = (bits2Int(bits.substring(423, 424), false) == 1) ? true : false;


    }

    public void printAll() {
        super.printAll();
        System.out.print("AIS version: "); System.out.println(AISVersionIndicator);
        System.out.print("IMO Number: "); System.out.println(IMONumber);
        System.out.print("Call Sign: "); System.out.println(callSign);
        System.out.print("Name: "); System.out.println(name);

        System.out.print("Ship Type: ");
            System.out.print(parseShipType(typeOfShip) + " - ");
            System.out.println(typeOfShip);
        System.out.print("Length: "); System.out.println(lengthA + lengthB);
        System.out.print("Width: "); System.out.println(widthA + widthB);

        System.out.print("Type of EPF Device: "); System.out.println(typeOfEPFDevice);


        System.out.print("ETA: ");
            //System.out.println(DateFormat.getDateInstance().format(ETA));
            System.out.println(ETA);
        System.out.print("draught: "); System.out.println(draught);
        System.out.print("Destination: "); System.out.println(destination);
        System.out.print("DTE: "); System.out.println(DTE);
        System.out.print("Spare: "); System.out.println(spare);

    }
    
    public String fileString() {    	
    	return super.fileString() + "," + AISVersionIndicator + "," + IMONumber + "," + callSign + "," + 
    		   name + "," + typeOfShip + "," + lengthA + "," + lengthB +
    		   "," + widthA + "," + widthB + "," + typeOfEPFDevice + "," + 
    		   ETA + "," + draught + "," + destination + "," + DTE + "," + spare;
    	
    }
    
    public String trainString() {
    	int length = lengthA + lengthB;
    	int width = widthA + widthB;
    	
    	// Represent default/unknown as "?"
    	String l = (length == 0) 	 ? "?" 	: Integer.toString(length);
    	String w = (width == 0) 	 ? "?" 	: Integer.toString(width);
    	String d = (draught == 0) 	 ? "?" 	: Integer.toString(draught);
    	String t = (typeOfShip < 10) ? "?" 	: "\""+parseShipType(typeOfShip)+"\"";
    		
    		// SHIP_TYPE[Character.getNumericValue((Integer.toString(typeOfShip)).charAt(0))];
    	
    	return "" + l + "," + w + "," + d + "," + t; 
    }

    public static String parseShipType(int type) {
        if (type > 9) {
            int digit1 = Character.getNumericValue(((Integer.toString(type)).charAt(0)));
            int digit2 = Character.getNumericValue(((Integer.toString(type)).charAt(1)));

            if ( digit1 == 3) {
                return SHIP_TYPE[digit1] + " - " + SHIP_TYPE_3[digit2];
            }
            if ( digit1 == 5) {
                return SHIP_TYPE[digit1] + " - " + SHIP_TYPE_5[digit2];
            } else {
                return SHIP_TYPE[digit1] + " - " + SHIP_TYPE_0[digit2];
            }
        } else {
            return "";
        }
    }
    
    public static String shortType(int type) {
    	if (type > 9) {
    		if (type >= 20 && type <= 29) {
    			return "WIG";
    		}
    		else if(type == 30) {
    			return "Fisher";
    		}
    		else if(type >= 31 && type <= 39){
    			return "SpecialA";
    		}
    		else if(type >= 40 && type <= 49){
    			return "HSC";
    		}
    		else if(type >= 50 && type <= 59){
    			return "SpecialB";
    		}
    		else if(type >= 60 && type <= 69){
    			return "PassengerShip";
    		}
    		else if(type >= 70 && type <= 79){
    			return "CargoShip";
    		}
    		else if(type >= 80 && type <= 89){
    			return "Tanker";
    		}
    		else {
    			return "Other";
    		}		
    	}
    	return "Undefined";
    }

    public int getTypeOfShip() {
        return typeOfShip;
    }

}
