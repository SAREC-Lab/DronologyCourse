package edu.nd.dronology.core.air_traffic_control;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Safety manager is responsible for monitoring drone positions to ensure minimum safety distance is not violated
 * 
 * @author jane
 *
 */
public class DroneSeparationMonitor {
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSeparationMonitor.class);

	private List<ManagedDrone> drones = new ArrayList<>();
	private Long safetyZone; // Set arbitrarily for now.

	/**
	 * Construct the safety manager. SafetyZone size is hard coded at 10000 degree points.
	 */
	public DroneSeparationMonitor() {
		drones = new ArrayList<>();
		safetyZone = (long) 10000;
	}

	/**
	 * Attach a drone to the safety manager. Only attached drones are managed.
	 * 
	 * @param drone
	 */
	public void attachDrone(ManagedDrone drone) {
		drones.add(drone);
	}

	/**
	 * Detach a drone from the safety manager.
	 * 
	 * @param drone
	 */
	public void detachDrone(ManagedDrone drone) {
		if (drones.contains(drone))
			drones.remove(drone);
	}

	/**
	 * Computes the distance between two drones
	 * 
	 * @return distance remaining in degree points.
	 */
	public long getDistance(ManagedDrone drone1, ManagedDrone drone2) {
		long longDelta = Math.abs(drone1.getLongitude() - drone2.getLongitude());
		long latDelta = Math.abs(drone1.getLatitude() - drone2.getLatitude());
		return (long) Math.sqrt((Math.pow(longDelta, 2)) + (Math.pow(latDelta, 2)));

	}

	/**
	 * Checks if a drone has permission to take off. A drone may NOT take off if any other drone currently attached to the safety manager is in the vicinity.
	 * 
	 * @param managedDrone
	 *          The drone which is requesting permission to take off. This is checked against the complete list of drones.
	 * @return
	 */
	public boolean permittedToTakeOff(ManagedDrone managedDrone) {
		for (ManagedDrone drone2 : drones) {
			if (!managedDrone.equals(drone2) && drone2.getFlightModeState().isFlying()) {
				long dronDistance = getDistance(managedDrone, drone2);
				if (dronDistance < safetyZone * 1.5) {
					LOGGER.error("Safety Distance Violation - Drone not allowed to TakeOff! distance: " + dronDistance
							+ " safety zone: " + safetyZone + " => " + DistanceUtil.distance(managedDrone.getLatitude(),
									drone2.getLatitude(), managedDrone.getLongitude(), drone2.getLongitude(), 0, 0)
							+ "m");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Performs pairwise checks of safety violations between all drones currently attached to the safety manager.
	 */
	public void checkForViolations() {
		for (ManagedDrone drone : drones) {
			for (ManagedDrone drone2 : drones) {
				if (!drone.equals(drone2) && drone.getFlightModeState().isFlying() && drone2.getFlightModeState().isFlying()) {// !drone.isUnderSafetyDirectives()
					// &&
					// !drone2.isUnderSafetyDirectives()){
					if (getDistance(drone, drone2) < safetyZone) {
						// Do not remove even though not used right now.
						double angle1 = PointDelta.computeAngle(drone.getCoordinates(), drone.getTargetCoordinates());
						double angle2 = PointDelta.computeAngle(drone2.getCoordinates(), drone2.getTargetCoordinates());
						// new Roundabout(drone, drone2);
						// PLACEHOLDER FOR new avoidance strategy.
					}
				}
			}

		}
	}
}
