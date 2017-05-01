package edu.nd.dronology.core.flight_manager;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.utilities.Coordinates;
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
	private Coordinates targetPosition = null;
	private List<Coordinates> wayPoints = new ArrayList<>();
	private List<Coordinates> roundaboutPath = new ArrayList<>();

	@Override
	public Coordinates flyToNextPoint() {
		// targetCoordinates = flightDirector.flyToNextPoint();// Case: Drone is under safety directives and on a roundabout.

		if (onRoundabout())
			targetPosition = flyRoundAbout();
		else
			targetPosition = flyToNextWayPoint();
		// System.out.println(drone.getCoordinates().toString() + " to " + targetPosition.toString());

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
	public void setWayPoints(List<Coordinates> wayPoints) {
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

	private Coordinates flyToNextWayPoint() {
		// System.out.println("Trying to fly to next way point");
		if (!wayPoints.isEmpty()) {
			Coordinates nextWayPoint = wayPoints.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points
			return nextWayPoint;
		}
		return null;
	}

	private Coordinates flyRoundAbout() {
		if (!wayPoints.isEmpty()) {
			Coordinates nextWayPoint = roundaboutPath.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points
			return nextWayPoint;
		}
		return null;
	}

	public boolean onRoundabout() {
		if (roundaboutPath.isEmpty())
			return false;
		else
			return true;
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
				if (roundaboutPath.isEmpty())
					safetyDiversion = false;
			}
		} else {
			if (!wayPoints.isEmpty())
				wayPoints.remove(0);
		}
	}

	@Override
	public void addWayPoint(Coordinates wayPoint) {
		wayPoints.add(wayPoint);
	}

	// NEED TO SPLIT THIS INTO A DIFFERENT INTERFACE!!! Roundabout Interface.
	@Override
	public void setRoundabout(List<Coordinates> roundAboutPoints) {
		if (!isUnderSafetyDirectives()) {
			safetyDiversion = true;
			roundaboutPath = roundAboutPoints;
			// Start the roundabout
			Coordinates nextWayPoint = roundaboutPath.get(0); // Always get the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points

		}
	}

	@Override
	public void flyHome() {
		drone.flyTo(drone.getBaseCoordinates());

	}

	@Override
	public void returnHome(Coordinates home) {
		addWayPoint(home);
		ArrayList<Coordinates> tempWayPoints = new ArrayList<>(wayPoints);

		for (Coordinates wayPoint : tempWayPoints) {
			if (!wayPoint.equals(home)) {
				wayPoints.remove(wayPoint);
			}
		}

		// Should only have one waypoint left and ready to go home!!
	}

	@Override
	public boolean readyToLand() {
		if (!(onRoundabout() || hasMoreWayPoints())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean readyToTakeOff() {
		if (onRoundabout() || !hasMoreWayPoints()) {
			return false;
		} else {
			return true;
		}
	}

}
