package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;

public interface IFlightManagerServiceInstance extends IServiceInstance {


	public FlightInfo getFlightDetails();

	void planFlight(String planName, LlaCoordinate coordinates, List<LlaCoordinate> flight);

	public void planFlight(String uavid, String planName, LlaCoordinate coordinates, List<LlaCoordinate> flight);

}
