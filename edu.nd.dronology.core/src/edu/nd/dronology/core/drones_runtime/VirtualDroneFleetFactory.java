package edu.nd.dronology.core.drones_runtime;

import edu.nd.dronology.core.fleet_manager.DroneFleetFactory;
import edu.nd.dronology.core.utilities.Coordinates;

public class VirtualDroneFleetFactory extends DroneFleetFactory {

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
	public ManagedDrone initializeDrone(String droneID, String droneType, long latitude, long longitude, int altitude) {
		IDrone drone = new VirtualDrone(createDroneID(droneID));
		ManagedDrone managedDrone = new ManagedDrone(drone);
		Coordinates currentPosition = new Coordinates(latitude, longitude, altitude);
		drone.setBaseCoordinates(currentPosition);
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		managedDrone.start();
		DroneFleet.getInstance().setAvailableDrone(managedDrone);
		return managedDrone;
	}

}
