package edu.nd.dronology.core.flight.internal;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
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
	private LlaCoordinate targetPosition = null;
	private List<Waypoint> wayPoints = new ArrayList<>();
	private List<LlaCoordinate> roundaboutPath = new ArrayList<>();

	@Override
	public LlaCoordinate flyToNextPoint() {
		// targetCoordinates = flightDirector.flyToNextPoint();// Case: Drone is
		// under safety directives and on a roundabout.

		if (onRoundabout()) {
			targetPosition = flyRoundAbout();
		} else {
			targetPosition = flyToNextWayPoint();
			// System.out.println(drone.getCoordinates().toString() + " to " +
			// targetPosition.toString());
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
	 * @see
	 * controller.movement.iFlightDirector#setWayPoints(java.util.ArrayList)
	 */
	@Override
	public void setWayPoints(List<Waypoint> wayPoints) {
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

	private LlaCoordinate flyToNextWayPoint() {
		// LOGGER.info("Flying to next waypoint");
		if (!wayPoints.isEmpty()) {
			LlaCoordinate nextWayPoint = wayPoints.get(0).getCoordinate(); // Always
																			// get
																			// the
																			// top
																			// one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points
			return nextWayPoint;
		}
		return null;
	}

	private LlaCoordinate flyRoundAbout() {
		if (!wayPoints.isEmpty()) {
			LlaCoordinate nextWayPoint = roundaboutPath.get(0); // Always get
																// the top one
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
				Waypoint wp = wayPoints.remove(0);
				wp.reached(true);
			}
			if (wayPoints.isEmpty()) {
				try {
					drone.getFlightModeState().setModeToInAir();
				} catch (FlightZoneException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void addWayPoint(Waypoint wayPoint) {
		wayPoints.add(wayPoint);
	}

	// NEED TO SPLIT THIS INTO A DIFFERENT INTERFACE!!! Roundabout Interface.
	@Override
	public void setRoundabout(List<LlaCoordinate> roundAboutPoints) {
		if (!isUnderSafetyDirectives()) {
			safetyDiversion = true;
			roundaboutPath = roundAboutPoints;
			// Start the roundabout
			LlaCoordinate nextWayPoint = roundaboutPath.get(0); // Always get
																// the top one
			drone.flyTo(nextWayPoint); // @TD: Altitude not included in points

		}
	}

	@Override
	public void flyHome() {
		drone.flyTo(drone.getBaseCoordinates());

	}

	@Override
	public void returnHome(Waypoint home) {
		addWayPoint(home);
		ArrayList<Waypoint> tempWayPoints = new ArrayList<>(wayPoints);

		for (Waypoint wayPoint : tempWayPoints) {
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
