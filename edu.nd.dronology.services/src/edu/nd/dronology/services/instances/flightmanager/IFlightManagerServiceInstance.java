package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;

public interface IFlightManagerServiceInstance extends IServiceInstance {

	public FlightInfo getFlightDetails();

	public void planFlight(String uavid, String planName, List<Waypoint> waypoints) throws Exception;

	public void planFlight(String planName, List<Waypoint> waypoints) throws Exception;

	public void returnToHome(String uavid) throws Exception;

	public void pauseFlight(String uavid) throws Exception;

	public FlightInfo getFlightInfo(String uavId);

	public void planFlight(String uavid, String planName, LlaCoordinate coordinates, List<LlaCoordinate> flight);

}
