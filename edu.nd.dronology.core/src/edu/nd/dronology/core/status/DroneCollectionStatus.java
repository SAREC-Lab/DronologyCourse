package edu.nd.dronology.core.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.util.Immutables;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;
import edu.nd.dronology.core.vehicle.proxy.UAVProxy;

// Singleton class
public class DroneCollectionStatus {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneCollectionStatus.class);

	private Map<String, UAVProxy> drones;
	private static volatile DroneCollectionStatus INSTANCE = null;

	protected DroneCollectionStatus() {
		drones = new HashMap<>();
	}

	public static DroneCollectionStatus getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneCollectionStatus.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneCollectionStatus();
				}
			}
		}
		return INSTANCE;
	}

	public void testStatus() {
		LOGGER.info("Print current drone dump");
		for (UAVProxy droneStatus : drones.values()) {
			LOGGER.info(droneStatus.toString());
		}
	}

	public Map<String, UAVProxy> getDrones() {
		return Immutables.hashMapCopy(drones);
	}

	public void addDrone(UAVProxy drone) {
		drones.put(drone.getID(), drone);
	}

	public void removeDrone(String droneID) {
		if (drones.containsKey(droneID)) {
			drones.remove(droneID);
		}
	}

	public void removeDrone(UAVProxy drone) {
		if (drones.containsKey(drone.getID())) {
			drones.remove(drone.getID());
		}
	}

	public UAVProxy getDrone(String droneID) {
		if (drones.containsKey(droneID)) {
			return drones.get(droneID);
		}
		return null;
	}
}
