package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.util.Coordinates;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;

public interface IFlightManagerServiceInstance extends IServiceInstance {

	public void planFlight(Coordinates coordinates, List<Coordinates> flight);

	public FlightInfo getFlightDetails();

}
