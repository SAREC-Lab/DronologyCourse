package edu.nd.dronology.core.fleet;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.core.vehicle.internal.PhysicalDrone;
import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

/**
 * Creates a fleet of physical drones. Not yet implemented.
 * 
 * @author Jane
 *
 */
public class PhysicalDroneFleetFactory extends AbstractDroneFleetFactory {

	private static volatile PhysicalDroneFleetFactory INSTANCE = null;
	private IDroneCommandHandler commandHandler;

	protected PhysicalDroneFleetFactory() {
	}

	public void setCommandHandler(IDroneCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

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
	public ManagedDrone initializeDrone(String droneID, String droneType, long latitude, long longitude, int altitude)
			throws DroneException {
		if(RuntimeDroneTypes.getInstance().getCommandHandler()==null){
			throw new DroneException("Physical Drone Command Handler not prperly initialized!");
		}
		
		IDrone drone = new PhysicalDrone(createDroneID(droneID), RuntimeDroneTypes.getInstance().getCommandHandler());
		ManagedDrone managedDrone = new ManagedDrone(drone);
		Coordinate currentPosition = new Coordinate(latitude, longitude, altitude);
		drone.setBaseCoordinates(currentPosition);
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		managedDrone.start();
		DroneFleetManager.getInstance().addDrone(managedDrone);
		return managedDrone;
	}
}