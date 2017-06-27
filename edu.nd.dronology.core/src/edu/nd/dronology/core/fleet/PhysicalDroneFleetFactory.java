package edu.nd.dronology.core.fleet;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.core.vehicle.internal.PhysicalDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Creates a fleet of physical drones. Not yet implemented.
 * 
 * @author Jane
 *
 */
public class PhysicalDroneFleetFactory extends AbstractDroneFleetFactory {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PhysicalDroneFleetFactory.class);

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
	@Discuss(discuss = "todo: fligh to altitude 10... workaround just for testing purposes... needs to be fixed..")
	public ManagedDrone initializeDrone(String droneID, String droneType, double latitude, double longitude,
			double altitude) throws DroneException {
		if (RuntimeDroneTypes.getInstance().getCommandHandler() == null) {
			throw new DroneException("Physical Drone Command Handler not prperly initialized!");
		}

		IDrone drone = new PhysicalDrone(createDroneID(droneID), RuntimeDroneTypes.getInstance().getCommandHandler());
		ManagedDrone managedDrone = new ManagedDrone(drone);

		LlaCoordinate currentPosition = new LlaCoordinate(latitude, longitude, 10);
		LOGGER.info("Drone initialized at: " + currentPosition.toString());

		drone.setBaseCoordinates(currentPosition);
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		managedDrone.start();
		DroneFleetManager.getInstance().addDrone(managedDrone);
		return managedDrone;
	}
}