package edu.nd.dronology.core.vehicle.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Singleton class that keeps track of all {@link UAVProxy} instances that have been created.
 */
public class UAVProxyManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVProxyManager.class);

	private Map<String, UAVProxy> drones;
	private static volatile UAVProxyManager INSTANCE = null;

	protected UAVProxyManager() {
		drones = new HashMap<>();
	}

	public static UAVProxyManager getInstance() {
		if (INSTANCE == null) {
			synchronized (UAVProxyManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVProxyManager();
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

	@Deprecated
	public Map<String, UAVProxy> getDrones() {
		return Collections.unmodifiableMap(drones);
	}

	public Collection<IUAVProxy> getActiveUAVs() {
		return Collections.unmodifiableCollection(new ArrayList(drones.values()));
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
