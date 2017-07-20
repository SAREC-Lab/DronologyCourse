package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;

/**
 * iDrone interface
 * 
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
	public double getLatitude();

	/**
	 * 
	 * @return longitude of drone
	 */
	public double getLongitude();

	/**
	 * 
	 * @return altitude of drone
	 */
	public double getAltitude();

	/**
	 * Fly drone to target coordinates
	 * 
	 * @param targetCoordinates
	 * @param speed
	 */
	public void flyTo(LlaCoordinate targetCoordinates, Double speed);

	/**
	 * 
	 * @return current coordinates
	 */
	public LlaCoordinate getCoordinates();

	/**
	 * 
	 * @return unique name of drone
	 */
	public String getDroneName();

	/**
	 * Land the drone. Update status.
	 * 
	 * @throws FlightZoneException
	 */
	void land() throws FlightZoneException;

	/**
	 * Takeoff. Update status.
	 * 
	 * @throws FlightZoneException
	 */
	void takeOff(double altitude) throws FlightZoneException;

	/**
	 * Sets drones coordinates
	 * 
	 * @param lat
	 *            latitude
	 * @param lon
	 *            Longitude
	 * @param alt
	 *            Altitude
	 */
	public void setCoordinates(double lat, double lon, double alt);

	public double getBatteryStatus();

	public boolean move(double i);

	public void setVoltageCheckPoint();

	public boolean isDestinationReached(int distanceMovedPerTimeStep);

	void setBaseCoordinates(LlaCoordinate basePosition);

	public LlaCoordinate getBaseCoordinates();

	public void setGroundSpeed(double speed);

	public void setVelocity(double x, double y, double z);

	void setCoordinates(LlaCoordinate coordinate);

}
