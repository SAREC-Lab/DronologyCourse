package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlightInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 256865471183839829L;
	private List<FlightPlanInfo> currentFlights = Collections.emptyList();
	private List<FlightPlanInfo> pending = Collections.emptyList();
	private List<FlightPlanInfo> awaiting = Collections.emptyList();
	private List<FlightPlanInfo> completed = Collections.emptyList();

	public FlightInfo(String name, String id) {
		super(name, id);
		currentFlights = new ArrayList<>();
		pending = new ArrayList<>();
		awaiting = new ArrayList<>();
		completed = new ArrayList<>();
	}

	public void addCurrentFlight(FlightPlanInfo planInfo) {
		currentFlights.add(planInfo);
	}

	public List<FlightPlanInfo> getCurrentFlights() {
		return currentFlights;
	}

	public List<FlightPlanInfo> getPendingFlights() {
		return pending;
	}

	public List<FlightPlanInfo> getAwaitingFlights() {
		return awaiting;
	}

	public List<FlightPlanInfo> getCompletedFlights() {
		return completed;
	}

	public void addPending(FlightPlanInfo planInfo) {
		pending.add(planInfo);

	}

	public void addAwaitingTakeoff(FlightPlanInfo planInfo) {
		awaiting.add(planInfo);

	}

	public void addCompleted(FlightPlanInfo planInfo) {
		completed.add(planInfo);

	}

}
