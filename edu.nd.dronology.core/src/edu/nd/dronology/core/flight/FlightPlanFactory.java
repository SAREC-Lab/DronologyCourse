package edu.nd.dronology.core.flight;

import java.util.List;

import edu.nd.dronology.core.flight.internal.FlightPlan;
import edu.nd.dronology.core.util.LlaCoordinate;

public class FlightPlanFactory {

	public static IFlightPlan create(String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
		return create(null, planName, start, wayPoints);
	}

	public static IFlightPlan create(String uavid, String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
		return new FlightPlan(uavid, planName, start, wayPoints);
	}

}
