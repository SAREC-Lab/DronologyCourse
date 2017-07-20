package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.status.DroneCollectionStatus;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Abstract Base class for both virtual and physical drones
 * 
 * 
 * @author Michael
 *
 */
public abstract class AbstractDrone implements IDrone {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractDrone.class);

	private LlaCoordinate basePosition; // In current version drones always return to base at the end of their flights.
	protected LlaCoordinate currentPosition;
	protected final String droneName;
	protected DroneStatus droneStatus; // PHY

	protected AbstractDrone(String drnName) {
		NullUtil.checkNull(drnName);
		this.droneName = drnName;
		currentPosition = null;
		droneStatus = new DroneStatus(drnName, 0, 0, 0, 0.0, 0.0); // Not initialized yet //PHYS
		DroneCollectionStatus.getInstance().addDrone(droneStatus); // PHYS
	}

	@Override
	public void setCoordinates(double lat, double lon, double alt) { // For physical drone this must be set by reading position
		currentPosition = new LlaCoordinate(lat, lon, alt);
		droneStatus.updateCoordinates(lat, lon, alt);
	}

	@Override
	public void setCoordinates(LlaCoordinate coordinate) { // For physical drone this must be set by reading position
		currentPosition = coordinate;
		droneStatus.updateCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(),
				currentPosition.getAltitude());
	}

	@Override
	public double getLatitude() {
		return currentPosition.getLatitude();
	}

	@Override
	public double getLongitude() {
		return currentPosition.getLongitude();
	}

	@Override
	public double getAltitude() {
		return currentPosition.getAltitude();
	}

	@Override
	public LlaCoordinate getCoordinates() {
		return currentPosition;
	}

	@Override
	public String getDroneName() {
		return droneName;
	}

	@Override
	public DroneStatus getDroneStatus() {
		return droneStatus;
	}

	/**
	 * Set base coordinates for the drone
	 * 
	 * @param basePosition
	 */
	@Override
	public void setBaseCoordinates(LlaCoordinate basePosition) {

		this.basePosition = new LlaCoordinate(basePosition.getLatitude(), basePosition.getLongitude(),
				basePosition.getAltitude());
		LOGGER.info("Base Coordinate of Drone '" + droneName + " set to '" + basePosition.toString());
	}

	/**
	 * Get unique base coordinates for the drone
	 * 
	 * @return base coordinates
	 */
	@Override
	public LlaCoordinate getBaseCoordinates() {
		return basePosition;
	}

	public void setVelocity(double velocity) {
		droneStatus.setVelocity(velocity);

	}

	public void updateBatteryLevel(double batteryLevel) {
		droneStatus.updateBatteryLevel(batteryLevel);
		
	}

}
