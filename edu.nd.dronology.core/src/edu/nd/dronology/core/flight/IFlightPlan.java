package edu.nd.dronology.core.flight;

import java.util.List;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.ManagedDrone;
@Discuss(discuss="this interface is currently exposed - i.e. managed drone is exposed ")
public interface IFlightPlan {

	ManagedDrone getAssignedDrone();

	boolean setStatusToCompleted() throws FlightZoneException;

	Coordinate getStartLocation();
	
	Coordinate getEndLocation();

	void clearAssignedDrone();

	String getFlightID();

	boolean setStatusToFlying(ManagedDrone drone) throws FlightZoneException;

	List<Coordinate> getWayPoints();

	long getStartTime();

	long getEndTime();
	
	int getNumberWayPoints();

}
