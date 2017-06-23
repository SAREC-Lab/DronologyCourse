package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;

public interface IFlightManagerServiceInstance extends IServiceInstance {


	public FlightInfo getFlightDetails();

	void planFlight(String planName, Coordinate coordinates, List<Coordinate> flight);

}
