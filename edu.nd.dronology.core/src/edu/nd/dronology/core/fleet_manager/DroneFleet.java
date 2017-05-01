package edu.nd.dronology.core.fleet_manager;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Holds a fleet of virtual or physical drones.
 * 
 * @author Jane
 *
 */
public class DroneFleet {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneFleet.class);
	private static volatile DroneFleet INSTANCE = null;

	private List<ManagedDrone> availableDrones;
	private List<ManagedDrone> busyDrones;

	public static DroneFleet getInstance() {

		if (INSTANCE == null) {
			synchronized (DroneFleet.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneFleet();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Specifies whether virtual or physical drones will be created according to the previously specified runtime drone type. (See RuntimeDroneTypes.java)
	 */
	protected DroneFleet() {
		if (RuntimeDroneTypes.getInstance().isSimulation())
			availableDrones = VirtualDroneFleetFactory.getInstance().getDrones();
		else
			availableDrones = PhysicalDroneFleetFactory.getInstance().getDrones();

		busyDrones = new ArrayList<>();
	}

	/**
	 * Checks for an available drone from the fleet.
	 * 
	 * @return true if drone is available, false if it is not.
	 */
	public boolean hasAvailableDrone() {
		LOGGER.info("Drones available: " + availableDrones.size());
		return availableDrones.size() > 0;

	}

	/**
	 * Returns the next available drone. Currently uses FIFO to recycle drones.
	 * 
	 * @return
	 */
	public ManagedDrone getAvailableDrone() {
		if (!availableDrones.isEmpty()) {
			ManagedDrone drone = availableDrones.get(0);
			availableDrones.remove(drone);
			busyDrones.add(drone);
			return drone;
		} else
			return null;
	}

	public void setAvailableDrone(ManagedDrone managedDrone) {
		availableDrones.add(managedDrone);
	}

	/**
	 * When a drone completes a mission, returns it to the pool of available drones.
	 * 
	 * @param drone
	 */
	public void returnDroneToAvailablePool(ManagedDrone drone) {
		if (busyDrones.contains(drone)) {
			busyDrones.remove(drone);
			availableDrones.add(drone);
		}
	}

}
