package edu.nd.dronology.core.drones_runtime;

import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.drone_status.DroneStatus;
import edu.nd.dronology.core.flight_manager.SoloDirector;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import view.DroneImage;


/**
 * iDrone interface
 * @author Jane Cleland-Huang
 * @version 0.01
 *
 */
public interface iDrone {
	
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
	public void flyTo(Coordinates targetCoordinates);

	/**
	 * 
	 * @return current coordinates
	 */
	public Coordinates getCoordinates();

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

	public boolean isDestinationReached(int i);

}
