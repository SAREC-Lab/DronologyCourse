package edu.nd.dronology.core.flight_manager;

import java.util.List;

import edu.nd.dronology.core.flight_manager.internal.FlightPlan;
import edu.nd.dronology.core.util.Coordinates;

public class FlightPlanFactory {

	public static IFlightPlan create(Coordinates start, List<Coordinates> wayPoints) {
		return new FlightPlan(start, wayPoints);
	}

}
