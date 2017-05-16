package edu.nd.dronology.core.flight_manager;

import java.util.List;

import edu.nd.dronology.core.util.Coordinates;

public interface IFlightDirector {
	
	public void returnHome(Coordinates home);
	
		
	public Coordinates flyToNextPoint();

	/**
	 * Set a series of waypoints.
	 * @param wayPoints
	 */
	void setWayPoints(List<Coordinates> wayPoints);

	/**
	 * Clear all waypoints
	 */
	void clearWayPoints();

	/**
	 * Creates a roundabout as a diversion
	 * @param roundAboutPoints
	 */
	void setRoundabout(List<Coordinates> roundAboutPoints);
	
	/** 
	 * Check if more waypoints exist
	 * @return boolean
	 */
	boolean hasMoreWayPoints();

	/**
	 * Specifies if flight is currently under a safety directive.
	 * @return isUnderSafetyDirectives
	 */
	boolean isUnderSafetyDirectives();

	/**
	 * Removes one wayPoint -- typically when a drone reaches a waypoint.
	 * @param wayPoint
	 */
	void clearCurrentWayPoint();

	/**
	 * Add a waypoint to the flight directive.
	 * @param wayPoint
	 */
	void addWayPoint(Coordinates wayPoint);

	void flyHome();

	boolean readyToLand();

	boolean readyToTakeOff();


}