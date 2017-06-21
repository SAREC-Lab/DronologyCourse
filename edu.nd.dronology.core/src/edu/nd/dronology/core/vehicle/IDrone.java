package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.Coordinate;

/**
 * iDrone interface
 * @author Jane Cleland-Huang
 * @version 0.01
 *
 */
public interface IDrone {
	
	public DroneStatus getDroneStatus();

	/**
	 * 
	 * @return latitude of drone
	 */
	public long getLatitude();

	/**
	 * 
	 * @return longitude of drone
	 */
	public long getLongitude();

	/**
	 * 
	 * @return altitude of drone
	 */
	public int getAltitude();
	
	/**
	 * Fly drone to target coordinates
	 * @param targetCoordinates
	 */
	public void flyTo(Coordinate targetCoordinates);

	/**
	 * 
	 * @return current coordinates
	 */
	public Coordinate getCoordinates();

	/**
	 * 
	 * @return unique name of drone
	 */
	public String getDroneName();

	/**
	 * Land the drone.  Update status.
	 * @throws FlightZoneException 
	 */
	void land() throws FlightZoneException;
	
	/**
	 * Takeoff.  Update status.
	 * @throws FlightZoneException 
	 */
	void takeOff(int altitude) throws FlightZoneException;

	/**
	 * Sets drones coordinates
	 * @param lat latitude
	 * @param lon Longitude
	 * @param alt Altitude
	 */
	public void setCoordinates(long lat, long lon, int alt);
	
	public double getBatteryStatus();

	public boolean move(int i);

	public void setVoltageCheckPoint();

	public boolean isDestinationReached(int distanceMovedPerTimeStep);

	void setBaseCoordinates(Coordinate basePosition);

	public Coordinate getBaseCoordinates();
	
	public void setGroundSpeed(double speed);
	
	public void setVelocity(double x, double y, double z);

}
