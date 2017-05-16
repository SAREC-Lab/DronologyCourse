package edu.nd.dronology.core.fleet_manager;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinates;
import edu.nd.dronology.core.vehicle.ManagedDrone;

/**
 * Abstract factory class for drone fleet factory
 * 
 * @author Jane
 *
 */
public abstract class AbstractDroneFleetFactory {
//	private final List<ManagedDrone> drones = new ArrayList<>();

	public AbstractDroneFleetFactory() {
	}

	protected String createDroneID(String droneID) {
		return droneID;
	}

	/**
	 * Returns list of drones
	 * 
	 * @return array list of iDrones
	 * @throws DroneException 
	 */
//	public List<ManagedDrone> getDrones() {
//		return drones;
//	}

	@Deprecated
	abstract public ManagedDrone initializeDrone(String DroneID, String DroneType, long latitude, long longitude,
			int altitude) throws DroneException;

	public void initializeDrone(String id, String type, Coordinates initialLocation) throws DroneException {
		initializeDrone(id, type, initialLocation.getLatitude(), initialLocation.getLongitude(),
				initialLocation.getAltitude());

	}

}
