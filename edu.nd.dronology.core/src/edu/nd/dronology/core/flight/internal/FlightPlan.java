package edu.nd.dronology.core.flight.internal;

import java.util.Collections;
import java.util.List;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.flight.IFlightPlan;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.ManagedDrone;

/**
 * Stores flight information including its waypoints and current status.
 * 
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class FlightPlan implements IFlightPlan{
	private static int flightNumber = 0;
	private String flightID;

	private List<Coordinate> wayPoints;
	private Coordinate startLocation;
	private Coordinate endLocation;
	private Status status;
	private ManagedDrone drone = null;

	private long startTime = -1;
	private long endTime = -1;

	private enum Status {
		PLANNED, FLYING, COMPLETED;

		@Override
		public String toString() {
			return name().charAt(0) + name().substring(1).toLowerCase();
		}

	}

	/**
	 * Loads flight information and assigns a flight ID. ID's are generated automatically and are unique in each run of the simulation.
	 * 
	 * @param start
	 *          Starting coordinates
	 * @param wayPoints
	 */
	public FlightPlan(String planName, Coordinate start, List<Coordinate> wayPoints) {
		this.wayPoints = wayPoints;
		this.startLocation = start;
		if (wayPoints.size() > 0) {
			this.endLocation = wayPoints.get(wayPoints.size() - 1);
		} else {
			endLocation = startLocation;
		}
		this.flightID = "DF-" + Integer.toString(++flightNumber)+ " - "+planName;
		status = Status.PLANNED;
	}

	/**
	 * 
	 * @return flight ID
	 */
	@Override
	public String getFlightID() {
		return flightID;
	}

	/**
	 * 
	 * @return Starting Coordinates
	 */
	@Override
	public Coordinate getStartLocation() {
		return startLocation;
	}

	/**
	 * 
	 * @return Ending Coordinates
	 */
	@Override
	public Coordinate getEndLocation() {
		return endLocation;
	}

	/**
	 * Returns the drone assigned to the flight plan. Will return null if no drone is yet assigned.
	 * 
	 * @return iDrone
	 */
	@Override
	public ManagedDrone getAssignedDrone() {
		return drone;
	}

	@Override
	public void clearAssignedDrone() {
		drone = null;
	}

	/**
	 * 
	 * @param drone
	 * @return true if drone is currently flying, false otherwise.
	 * @throws FlightZoneException
	 */
	@Override 
	public boolean setStatusToFlying(ManagedDrone drone) throws FlightZoneException {
		if (status == Status.PLANNED) {
			status = Status.FLYING;
			startTime = System.currentTimeMillis();
			this.drone = drone;
			return true;
		} else
			throw new FlightZoneException("Only currently planned flights can have their status changed to flying");
	}

	/**
	 * Sets flightplan status to completed when called.
	 * 
	 * @return true
	 * @throws FlightZoneException
	 */
	@Override
	public boolean setStatusToCompleted() throws FlightZoneException {
		if (status == Status.FLYING) {
			status = Status.COMPLETED;
			endTime = System.currentTimeMillis();
			return true; // success (may add real check here later)
		} else
			throw new FlightZoneException("Only currently flying flights can have their status changed to completed");
	}

	/**
	 * Returns current flightplan status (Planned, Flying, Completed)
	 * 
	 * @return status
	 */
	public String getStatus() {

		return status.toString();

	}

	@Override
	public String toString() {
		return flightID + "\n" + getStartLocation() + " - " + getEndLocation() + "\n" + getStatus();
	}

	/**
	 * Returns way points
	 * 
	 * @return List<Coordinates>
	 */
	@Override
	public List<Coordinate> getWayPoints() {
		return Collections.unmodifiableList(wayPoints);
	}

	/**
	 * Returns total number of waypoints in flight plan
	 * 
	 * @return int
	 */
	@Override
	public int getNumberWayPoints() {
		return wayPoints.size();
	}

	/**
	 * Returns start time of flight.
	 * 
	 * @return date object
	 */
	@Override
	public long getStartTime() {
		return startTime;
	}

	/**
	 * REturns end time of flight.
	 * 
	 * @return date object
	 */
	@Override
	public long getEndTime() {
		return endTime;
	}
}
