package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.fleet.PhysicalDroneFleetFactory;
import edu.nd.dronology.core.status.DroneCollectionStatus;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.Coordinate;
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

	
	private Coordinate basePosition; // In current version drones always return to base at the end of their flights.
	protected Coordinate currentPosition;
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
	public void setCoordinates(long lat, long lon, int alt) { // For physical drone this must be set by reading position
		currentPosition = new Coordinate(lat, lon, alt);
		droneStatus.updateCoordinates(lat, lon, alt);
	}

	@Override
	public long getLatitude() {
		return currentPosition.getLatitude();
	}

	@Override
	public long getLongitude() {
		return currentPosition.getLongitude();
	}

	@Override
	public int getAltitude() {
		return currentPosition.getAltitude();
	}

	@Override
	public Coordinate getCoordinates() {
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
	public void setBaseCoordinates(Coordinate basePosition) {
		
		this.basePosition = new Coordinate(basePosition.getLatitude(), basePosition.getLongitude(),
				basePosition.getAltitude());
		LOGGER.info("Base Coordinate of Drone '"+droneName+" set to '"+ basePosition.toString());
	}

	/**
	 * Get unique base coordinates for the drone
	 * 
	 * @return base coordinates
	 */
	@Override
	public Coordinate getBaseCoordinates() {
		return basePosition;
	}

}
