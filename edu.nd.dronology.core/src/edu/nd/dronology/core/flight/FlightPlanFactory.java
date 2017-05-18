package edu.nd.dronology.core.flight;

import java.util.List;

import edu.nd.dronology.core.flight.internal.FlightPlan;
import edu.nd.dronology.core.util.Coordinate;

public class FlightPlanFactory {

	public static IFlightPlan create(Coordinate start, List<Coordinate> wayPoints) {
		return new FlightPlan(start, wayPoints);
	}

}
