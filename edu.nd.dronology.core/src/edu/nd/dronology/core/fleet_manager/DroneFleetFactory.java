package edu.nd.dronology.core.fleet_manager;

import java.util.ArrayList;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.utilities.Coordinates;

/**
 * Abstract factory class for drone fleet factory
 * 
 * @author Jane
 *
 */
public abstract class DroneFleetFactory {
	private final ArrayList<ManagedDrone> drones = new ArrayList<>();

	public DroneFleetFactory() {
	}

	protected String createDroneID(String droneID) {
		return droneID;
	}

	/**
	 * Returns list of drones
	 * 
	 * @return array list of iDrones
	 */
	public ArrayList<ManagedDrone> getDrones() {
		return drones;
	}

	@Deprecated
	abstract public ManagedDrone initializeDrone(String DroneID, String DroneType, long latitude, long longitude,
			int altitude);

	public void initializeDrone(String id, String type, Coordinates initialLocation) {
		initializeDrone(id, type, initialLocation.getLatitude(), initialLocation.getLatitude(),
				initialLocation.getAltitude());

	}

}
