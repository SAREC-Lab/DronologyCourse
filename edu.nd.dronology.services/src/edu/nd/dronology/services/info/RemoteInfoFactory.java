package edu.nd.dronology.services.info;

import java.util.List;

import edu.nd.dronology.core.flight_manager.FlightPlan;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;

public class RemoteInfoFactory {

	public static FlightInfo createFlightInfo(Flights flights) {

		FlightInfo info = new FlightInfo("FlightInfo", "FlightInfo");

		for (FlightPlan flt : flights.getCurrentFlights()) {
			FlightPlanInfo fpl = createPlanInfo(flt);
			info.addCurrentFlight(fpl);

		}

		for (FlightPlan flt : flights.getAwaitingTakeOffFlights()) {
			FlightPlanInfo fpl = createPlanInfo(flt);
			info.addAwaitingTakeoff(fpl);
		}

		for (FlightPlan flt : flights.getCompletedFlights()) {
			FlightPlanInfo fpl = createPlanInfo(flt);
			info.addCompleted(fpl);
		}

		for (FlightPlan flt : flights.getPendingFlights()) {
			FlightPlanInfo fpl = createPlanInfo(flt);
			info.addPending(fpl);
		}

		return info;

	}

	private static FlightPlanInfo createPlanInfo(FlightPlan flt) {
		FlightPlanInfo flightPlanInfo = new FlightPlanInfo(flt.getFlightID(), flt.getFlightID());
		String droneId = flt.getAssignedDrone() != null ? flt.getAssignedDrone().getDroneName() : "--";
		List<Coordinates> waypoints = flt.getWayPoints();
		Coordinates start = flt.getStartLocation();
		long startTime = flt.getStartTime();
		long endTime = flt.getEndTime();

		flightPlanInfo.setDroneId(droneId);
		flightPlanInfo.setWaypoints(waypoints);
		flightPlanInfo.setStartLocation(start);
		flightPlanInfo.setStartTime(startTime);
		flightPlanInfo.setEndTime(endTime);

		return flightPlanInfo;
	}

}
