package edu.nd.dronology.core.simplesimulator;

import edu.nd.dronology.core.drones_runtime.internal.VirtualDrone;
import edu.nd.dronology.core.util.Coordinates;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Computes the current position of a virtual drone as it moves during flight. Serves as a lightweight SITL for a drone.
 * 
 * @author Jane Cleland-Huang
 * @version 0.1
 */
public class FlightSimulator {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightSimulator.class);

	private Coordinates currentPosition;
	private Coordinates targetPosition;
	private double theta;
	private Long previousDistance = 0L;

	private VirtualDrone drone;

	/**
	 * Creates a flight simulator object for a single virtual drone
	 * 
	 * @param virtualDrone
	 */
	public FlightSimulator(VirtualDrone drone) {
		this.drone = drone;
	}

	/**
	 * Sets current flight path from current position to a targeted position
	 * 
	 * @param currentPos
	 *          Coordinates of current position
	 * @param targetPos
	 *          Coordinates of target position
	 */
	public void setFlightPath(Coordinates currentPos, Coordinates targetPos) {
		this.currentPosition = currentPos;
		this.targetPosition = targetPos;
		previousDistance = getRemainingDistance();
	}

	/**
	 * Computes the distance between current position and target position
	 * 
	 * @return distance remaining in degree points.
	 */
	public long getRemainingDistance() {
		return (long) Math.sqrt((Math.pow(computeLongitudeDelta(), 2)) + Math.pow(computeLatitudeDelta(), 2));
	}

	/**
	 * Computes the delta between the drones current latitude and its target latitude.
	 * 
	 * @return
	 */
	private long computeLatitudeDelta() {
		return currentPosition.getLatitude() - targetPosition.getLatitude();
	}

	/**
	 * Computes the delta between the drones current longitude and its target longitude
	 * 
	 * @return
	 */
	private long computeLongitudeDelta() {
		return currentPosition.getLongitude() - targetPosition.getLongitude();
	}

	/**
	 * Computes the angle at which a drone is flying with respect to the vertical
	 */
	private void computeAngle() {
		double height = computeLatitudeDelta(); // opposite
		// double width = (computeLongitudeDelta());
		double hypotenuse = getRemainingDistance();
		double sinTheta = height / hypotenuse;
		theta = Math.asin(sinTheta) * 180 / Math.PI;
	}

	/**
	 * Computes the position of the drone following one step. Checks if destination has been reached.
	 * 
	 * @param step
	 *          : Distance in degree points to move per iteration
	 * @return isStillMoving?
	 */
	public boolean move(long step) {
		// First determine which relative quadrant the target is in -- in relation to current position at the origin of X,Y axes

		computeAngle();
		long heightIncrement = Math.abs((long) (Math.sin(theta) * step));
		long widthIncrement = Math.abs((long) (Math.cos(theta) * step));

		// Latitude delta
		if (currentPosition.getLatitude() < targetPosition.getLatitude()) {
			currentPosition.setLatitude(currentPosition.getLatitude() + heightIncrement); // Drone is south of Target
		} else {
			currentPosition.setLatitude(currentPosition.getLatitude() - heightIncrement); // Drone is North (or same) as target
		}
		// Longitude delta
		if (currentPosition.getLongitude() < targetPosition.getLongitude()) {
			currentPosition.setLongitude(currentPosition.getLongitude() + widthIncrement); // Drone is to the left/west of target
		} else {
			currentPosition.setLongitude(currentPosition.getLongitude() - widthIncrement); // Drone is to the right/east of target
		}
		// double distanceMoved = Math.sqrt(Math.pow(heightIncrement,2)+Math.pow(widthIncrement,2));

		if (previousDistance <= getRemainingDistance() && getRemainingDistance() < 200) {
			previousDistance = getRemainingDistance();
			LOGGER.info(drone.getDroneName() + " ==> Waypoint reached");
			return false;
		} else {
			previousDistance = getRemainingDistance();
			return true;
		}

	}

	/**
	 * Checks if a drone has reached its target destination.
	 * 
	 * @param distanceMovedPerTimeStep
	 *          Checks location with respect to target position.
	 * @return true if target position is reached.
	 */
	public boolean isDestinationReached(long distanceMovedPerTimeStep) {
		long latDistance = Math.abs(currentPosition.getLatitude() - targetPosition.getLatitude());
		long lonDistance = Math.abs(currentPosition.getLongitude() - targetPosition.getLongitude());
		return lonDistance <= distanceMovedPerTimeStep && latDistance <= distanceMovedPerTimeStep;
	}

}
