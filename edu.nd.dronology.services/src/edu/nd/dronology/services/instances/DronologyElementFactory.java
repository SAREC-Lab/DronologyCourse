package edu.nd.dronology.services.instances;

import edu.nd.dronology.core.flight_manager.FlightPlan;
import edu.nd.dronology.services.instances.flightpath.FlightPath;
import edu.nd.dronology.services.instances.flightpath.IFlightPath;

public class DronologyElementFactory {

	public static IFlightPath createNewFlightPath() {
		return new FlightPath();
	}

}
