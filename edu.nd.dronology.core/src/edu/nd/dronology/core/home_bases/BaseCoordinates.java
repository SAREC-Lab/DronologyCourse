
package edu.nd.dronology.core.home_bases;

import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.ZoneBounds;

/**
 * Each new virtual drone is assigned a unique base..
 * We will line them up across northernmost edge of the flying field.
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class BaseCoordinates {
		
	private int NumberOfBases = 10;
	private int currentBaseNumber = 1;
	private static BaseCoordinates instance = null;
	protected BaseCoordinates() {}
	
	/** 
	 * Singleton class require calling getInstance()
	 * @return instance of BaseCoordinates class
	 */
	public static BaseCoordinates getInstance() {
	   if(instance == null) {
	      instance = new BaseCoordinates();
	   }
	   return instance;
	}
	
	/**
	 * Return the next unused base.
	 * @return
	 */
	public Coordinates getNextBase(){
		ZoneBounds zoneBounds = ZoneBounds.getInstance();
		long lat = zoneBounds.getNorthLatitude()-zoneBounds.getYRange()/20; // will break in other world quadrants
		long lon = zoneBounds.getWestLongitude()-zoneBounds.getXRange()/20;
		
		long xRange = zoneBounds.getXRange();
		long lonDelta = (xRange/NumberOfBases)*currentBaseNumber++;
		long temp = lon+lonDelta;
		System.out.println("Base coords: " + lat + " " + temp);
		return new Coordinates(lat, lon+lonDelta, 0);
	}	
}
