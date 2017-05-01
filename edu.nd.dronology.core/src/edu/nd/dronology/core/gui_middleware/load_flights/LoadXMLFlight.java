package edu.nd.dronology.core.gui_middleware.load_flights;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Loads flightplan from xml file.  See documentation.
 * @author Jane
 */
public class LoadXMLFlight {
	
	private WayPointCollection wayPointCollection = null;
	
	public WayPointCollection getAllWayPoints(){
		return wayPointCollection;
	}
	
	/**
	 * Loads flightplan from filepath
	 * @param flightZoneMgr
	 * @param inputFlightsFileName
	 */
   public LoadXMLFlight(File inputFlightsFileName){
      try {	
         System.out.println("Loading file: " + inputFlightsFileName);
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLFlightPlanImportHandler userhandler = new XMLFlightPlanImportHandler();
         saxParser.parse(inputFlightsFileName, userhandler);   
         wayPointCollection = userhandler.getAllWayPoints();
      } catch (Exception e) {
         e.printStackTrace();
      }     
   } 

}

