package edu.nd.dronology.core.gui_middleware;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Singleton class
public class DroneCollectionStatus {
	private Map<String, DroneStatus> drones;
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
		System.out.println("Print current drone dump");
		for (DroneStatus droneStatus : drones.values()) {
			System.out.println(droneStatus.toString());
		}
	}

	public Map<String, DroneStatus> getDrones() {
		return Collections.unmodifiableMap(drones);
	}

	public void addDrone(DroneStatus drone) {
		drones.put(drone.getID(), drone);
	}

	public void removeDrone(String droneID) {
		if (drones.containsKey(droneID))
			drones.remove(droneID);
	}

	public void removeDrone(DroneStatus drone) {
		if (drones.containsKey(drone.getID()))
			drones.remove(drone.getID());
	}

	public DroneStatus getDrone(String droneID) {
		if (drones.containsKey(droneID))
			return drones.get(droneID);
		else
			return null;
	}
}
