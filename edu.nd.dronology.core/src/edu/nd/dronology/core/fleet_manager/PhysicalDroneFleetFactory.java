package edu.nd.dronology.core.fleet_manager;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;

/**
 * Creates a fleet of physical drones.
 * Not yet implemented.  
 * @author Jane
 *
 */
public class PhysicalDroneFleetFactory extends DroneFleetFactory{

	protected PhysicalDroneFleetFactory() {		
	}
	
	private static PhysicalDroneFleetFactory instance = null;
	
	public static PhysicalDroneFleetFactory getInstance() {
		if(instance == null) {
			instance = new PhysicalDroneFleetFactory();
		}
		return instance;
	}
	
	@Override
	public ManagedDrone initializeDrone(String DroneID, String DroneType, long latitude, long longitude,
			int altitude) {
	
		return null;
	}
}