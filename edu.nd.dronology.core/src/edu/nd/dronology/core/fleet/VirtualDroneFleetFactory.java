package edu.nd.dronology.core.fleet;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

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
	public ManagedDrone initializeDrone(String droneID, String droneType, double latitude, double longitude, double altitude) throws DroneException {
		IDrone drone = new VirtualDrone(createDroneID(droneID));
		ManagedDrone managedDrone = new ManagedDrone(drone);
		LlaCoordinate currentPosition = new LlaCoordinate(latitude, longitude, altitude);
		drone.setBaseCoordinates(currentPosition);
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		managedDrone.start();
		DroneFleetManager.getInstance().addDrone(managedDrone);
		return managedDrone;
	}

}
