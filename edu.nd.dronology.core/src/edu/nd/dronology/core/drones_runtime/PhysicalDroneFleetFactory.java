package edu.nd.dronology.core.drones_runtime;

import edu.nd.dronology.core.fleet_manager.DroneFleetFactory;

/**
 * Creates a fleet of physical drones. Not yet implemented.
 * 
 * @author Jane
 *
 */
public class PhysicalDroneFleetFactory extends DroneFleetFactory {

	protected PhysicalDroneFleetFactory() {
	}

	private static volatile PhysicalDroneFleetFactory INSTANCE = null;

	public static PhysicalDroneFleetFactory getInstance() {
		if (INSTANCE == null) {
			synchronized (PhysicalDroneFleetFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = new PhysicalDroneFleetFactory();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public ManagedDrone initializeDrone(String droneID, String droneType, long latitude, long longitude, int altitude) {

		return null;
	}
}