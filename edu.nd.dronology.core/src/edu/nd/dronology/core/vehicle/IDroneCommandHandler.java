package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;

public interface IDroneCommandHandler {
	
	
	public void sendCommand(IDroneCommand command) throws DroneException;

	public Coordinate getLocation(int droneID);

	public double getBatteryVoltage(int droneID);

	public int getNewDroneID() throws Exception ;
	
	/**
	 * Generic interface for querying for drone attributes such as battery status, location...
	 * @param droneid The id of the drone
	 * @param key - The key of the attribute
	 * @return a drone attribute with the given key - null if this attribute does not exist.
	 */
	IDroneAttribute getAttribute(String droneid, String key);

}
