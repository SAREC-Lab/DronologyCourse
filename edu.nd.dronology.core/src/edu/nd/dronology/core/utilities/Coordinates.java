/**
 * Stores and manages a single coordinate using the Signed degrees format (DDD.dddd)
 * used by Google Maps.  For example:  University of Notre Dame Basilica has 
 * coordinates 41.707762 (latitude), -86.226880 (longitude).
 * This simulation uses relative altitude and so we initial set altitude = 0.
 * <p>
 * @author Jane Cleland-Huang
 * @date   12/18/2016
 * @version 0.1
 */

package edu.nd.dronology.core.utilities;

import java.io.Serializable;

public class Coordinates implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -959426816303585777L;
	private int altitude; 		// height
	private long latitude; 		// North/South
	private long longitude; 	// West/East
	
	/**
	 * @param lat Latitude in signed degrees format (DDD.dddd) Example: 41.707762
	 * @param lon Longitude in signed degrees format (DDD.ddd) Example: -86.226880
	 * @param alt Altitude in meters.  Default is 0.  Altitude is always relative to launch altitude.
	 */
	public Coordinates(long lat, long lon, int alt){
		setAltitude(alt);
		setLatitude(lat);
		setLongitude(lon);
	}
	
	/**
	 * 
	 * @param alt Altitude in meters.  Default is 0.  Altitude is always relative to launch altitude.
	 */
	public void setAltitude(int alt){
		altitude = alt;
	}
	
	/**
	 * 
	 * @param lat Latitude in signed degrees format (DDD.dddd) Example: 41.707762
	 */
	public void setLatitude(long lat){
		latitude = lat;
	}
	
	/**
	 * 
	 * @param lon Longitude in signed degrees format (DDD.ddd) Example: -86.226880
	 */
	public void setLongitude(long lon){
		longitude = lon;
	}
	
	/**
	 * 
	 * @return Altitude in meters
	 */
	public int getAltitude(){
		return altitude;
	}
	
	/**
	 * 
	 * @return Latitude in signed degrees
	 */
	public long getLatitude(){
		return latitude;
	}
	
	/**
	 * 
	 * @return Longitude in signed degrees
	 */
	public long getLongitude(){
		return longitude;
	}
	
	@Override
	public String toString() {
		return "Latitude: " + Long.toString(latitude) + ", Longitude: " + Long.toString(longitude) + 
				" Altitude: " + Integer.toString(altitude);
	}
	
	public String getShortString(){
		return "("+Long.toString(latitude) + "," + Long.toString(longitude) + "," + Integer.toString(altitude) + ")";
	}
	
	@Override
	public boolean equals(Object o){
		 // If the object is compared with itself then return true  
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Coordinate or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Coordinates)) {
            return false;
        }
         
        // typecast o to Coordinate so that we can compare data members 
        Coordinates coord = (Coordinates) o; 
        if(coord.altitude==this.altitude &&
        		coord.latitude== this.latitude &&
        		coord.longitude==this.longitude)
        	return true;
        else
			return false;		
	}
	
}
