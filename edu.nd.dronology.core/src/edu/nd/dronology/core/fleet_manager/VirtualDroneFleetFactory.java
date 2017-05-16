package edu.nd.dronology.core.fleet_manager;

import edu.nd.dronology.core.drones_runtime.internal.VirtualDrone;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinates;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class VirtualDroneFleetFactory extends AbstractDroneFleetFactory {

	protected VirtualDroneFleetFactory() {

	}

	private static volatile VirtualDroneFleetFactory INSTANCE = null;

	public static VirtualDroneFleetFactory getInstance() {
		if (INSTANCE == null) {
			synchronized (VirtualDroneFleetFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = new VirtualDroneFleetFactory();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public ManagedDrone initializeDrone(String droneID, String droneType, long latitude, long longitude, int altitude) throws DroneException {
		IDrone drone = new VirtualDrone(createDroneID(droneID));
		ManagedDrone managedDrone = new ManagedDrone(drone);
		Coordinates currentPosition = new Coordinates(latitude, longitude, altitude);
		drone.setBaseCoordinates(currentPosition);
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		managedDrone.start();
		DroneFleetManager.getInstance().addDrone(managedDrone);
		return managedDrone;
	}

}
