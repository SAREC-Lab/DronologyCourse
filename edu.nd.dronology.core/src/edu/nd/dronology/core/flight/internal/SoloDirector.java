package edu.nd.dronology.core.flight.internal;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Directions for one flight containing multiple waypoints.
 * 
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class SoloDirector implements IFlightDirector {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SoloDirector.class);

	private ManagedDrone drone;
	private boolean safetyDiversion = false;
	private Coordinate targetPosition = null;
	private List<Coordinate> wayPoints = new ArrayList<>();
	private List<Coordinate> roundaboutPath = new ArrayList<>();

	@Override
	public Coordinate flyToNextPoint() {
		// targetCoordinates = flightDirector.flyToNextPoint();// Case: Drone is under safety directives and on a roundabout.

		if (onRoundabout()) {
			targetPosition = flyRoundAbout();
		} else {
			targetPosition = flyToNextWayPoint();
			// System.out.println(drone.getCoordinates().toString() + " to " + targetPosition.toString());
		}
		return targetPosition;
	}

	/**
	 * Constructor
	 * 
	 * @param managedDrone
	 */
	public SoloDirector(ManagedDrone managedDrone) {
		this.drone = managedDrone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.movement.iFlightDirector#setWayPoints(java.util.ArrayList)
	 */
	@Override
	public void setWayPoints(List<Coordinate> wayPoints) {
		this.wayPoints = new ArrayList<>(wayPoints);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.movement.iFlightDirector#clearWayPoints()
	 */
	@Override
	public void clearWayPoints() {
		wayPoints.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.movement.iFlightDirector#hasMoreWayPoints()
	 */
	@Override
	public boolean hasMoreWayPoints() {
		return !wayPoints.isEmpty();
	}

	private Coordinate flyToNextWayPoint() {
	//	LOGGER.info("Flying to next waypoint");
		if (!wayPoints.isEmpty()) {
			Coordinate nextWayPoint = wayPoints.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points
			return nextWayPoint;
		}
		return null;
	}

	private Coordinate flyRoundAbout() {
		if (!wayPoints.isEmpty()) {
			Coordinate nextWayPoint = roundaboutPath.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points
			return nextWayPoint;
		}
		return null;
	}

	public boolean onRoundabout() {

		return !roundaboutPath.isEmpty();

	}

	@Override
	public boolean isUnderSafetyDirectives() {
		return safetyDiversion;
	}

	@Override
	public void clearCurrentWayPoint() {
		if (isUnderSafetyDirectives()) {
			if (!roundaboutPath.isEmpty()) {
				roundaboutPath.remove(0);
				if (roundaboutPath.isEmpty()) {
					safetyDiversion = false;
				}
			}
		} else {
			if (!wayPoints.isEmpty()) {
				wayPoints.remove(0);
			}
		}
	}

	@Override
	public void addWayPoint(Coordinate wayPoint) {
		wayPoints.add(wayPoint);
	}

	// NEED TO SPLIT THIS INTO A DIFFERENT INTERFACE!!! Roundabout Interface.
	@Override
	public void setRoundabout(List<Coordinate> roundAboutPoints) {
		if (!isUnderSafetyDirectives()) {
			safetyDiversion = true;
			roundaboutPath = roundAboutPoints;
			// Start the roundabout
			Coordinate nextWayPoint = roundaboutPath.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points

		}
	}

	@Override
	public void flyHome() {
		drone.flyTo(drone.getBaseCoordinates());

	}

	@Override
	public void returnHome(Coordinate home) {
		addWayPoint(home);
		ArrayList<Coordinate> tempWayPoints = new ArrayList<>(wayPoints);

		for (Coordinate wayPoint : tempWayPoints) {
			if (!wayPoint.equals(home)) {
				wayPoints.remove(wayPoint);
			}
		}

		// Should only have one waypoint left and ready to go home!!
	}

	@Override
	public boolean readyToLand() {

		return onRoundabout() || hasMoreWayPoints();

		// if (!(onRoundabout() || hasMoreWayPoints())) {
		// return false;
		// } else {
		// return true;
		// }
	}

	@Override
	public boolean readyToTakeOff() {

		// return !onRoundabout() && hasMoreWayPoints();

		if (onRoundabout() || !hasMoreWayPoints()) {
			return false;
		} else {
			return true;
		}
	}

}
