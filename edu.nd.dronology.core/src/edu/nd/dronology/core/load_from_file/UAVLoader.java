package edu.nd.dronology.core.load_from_file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.home_bases.BaseManager;

/**
 * Loads flightplan from xml file.  See documentation.
 * @author Jane
 */
public class UAVLoader {
	
	/**
	 * Loads flightplan from filepath
	 * @param flightZoneMgr
	 * @param filename
	 */
   public UAVLoader(FlightZoneManager flightZoneMgr, String filename){
      try {	
         File inputFile = new File(filename);
   	  		System.out.println("Loading file: " + filename);
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         UAVHandler userhandler = new UAVHandler(flightZoneMgr);
         saxParser.parse(inputFile, userhandler);     
      } catch (Exception e) {
         e.printStackTrace();
      }
      SaveLastFileName(filename);
   } 
   
   /**
    * Loads flightplan using the previously used xml file.
    * @param flightZoneMgr
    */
   public UAVLoader(BaseManager baseManager){
	  String filename = "C:\\Users\\Jane\\Dropbox\\DroneCode\\Drones.xml";  // Make it more flexible later.
	  System.out.println("Loading file: " + filename);
	  try {	
	     File inputFile = new File(filename);
	     SAXParserFactory factory = SAXParserFactory.newInstance();
	     SAXParser saxParser = factory.newSAXParser();
	     UAVHandler userhandler = new UAVHandler(baseManager);
	     saxParser.parse(inputFile, userhandler);     
	  } catch (Exception e) {
	     e.printStackTrace();
	  }
   }   