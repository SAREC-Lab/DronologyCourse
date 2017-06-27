package edu.nd.dronology.core.flight;

import java.util.List;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;

public interface IFlightDirector {
	
	//public void returnHome(LlaCoordinate home);
	
		
	public LlaCoordinate flyToNextPoint();

//	/**
//	 * Set a series of waypoints.
//	 * @param wayPoints
//	 */
//	void setWayPoints(List<LlaCoordinate> wayPoints);

	/**
	 * Clear all waypoints
	 */
	void clearWayPoints();

	/**
	 * Creates a roundabout as a diversion
	 * @param roundAboutPoints
	 */
	void setRoundabout(List<LlaCoordinate> roundAboutPoints);
	
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
//	void addWayPoint(LlaCoordinate wayPoint);

	void flyHome();

	boolean readyToLand();

	boolean readyToTakeOff();


	void setWayPoints(List<Waypoint> wayPoints);


	void addWayPoint(Waypoint wayPoint);


	void returnHome(Waypoint home);



}