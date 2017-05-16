package edu.nd.dronology.core.flight_manager;

import edu.nd.dronology.core.flight_manager.internal.SoloDirector;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class FlightDirectorFactory {

	public static IFlightDirector getFlightDirector(ManagedDrone managedDrone) {
		return new SoloDirector(managedDrone);
	}

}
