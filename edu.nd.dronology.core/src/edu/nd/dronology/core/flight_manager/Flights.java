package edu.nd.dronology.core.flight_manager;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.fleet_manager.DroneFleet;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Tracks the status of all flights as they move through the cycle of pending, awaiting take off, current, to completed.
 * 
 * @author Jane Cleland-Huang
 *
 */
public class Flights {

	private static final ILogger LOGGER = LoggerProvider.getLogger(Flights.class);

	private List<FlightPlan> pendingFlights;
	private List<FlightPlan> awaitingTakeOffFlights;
	private List<FlightPlan> currentFlights;
	private List<FlightPlan> completedFlights;
	private static int maximumAllowedCurrentFlights = 2;
	private DroneSeparationMonitor safetyMgr;
	private boolean grounded = false;

	/**
	 * Constructor
	 * 
	 * @param fzView
	 *          View needed to update panel showing flight status
	 * @param safetyMgr
	 *          Needed to check for safe takeoff
	 * @throws InterruptedException
	 */
	public Flights(DroneSeparationMonitor safetyMgr) throws InterruptedException {
		pendingFlights = new ArrayList<>();
		currentFlights = new ArrayList<>();
		completedFlights = new ArrayList<>();
		awaitingTakeOffFlights = new ArrayList<>();
		this.safetyMgr = safetyMgr;
	}

	/**
	 * Coordinate the return to home base of all flights.
	 * 
	 * @throws FlightZoneException
	 */
	public void groundAllFlights() throws FlightZoneException {
		grounded = true;
		// Clear pending flights
		pendingFlights.clear();

		// Clear awaitingTakeOffFlights
		// Deep copy
		List<FlightPlan> tempList = new ArrayList<>(awaitingTakeOffFlights);
		for (FlightPlan flightPlan : tempList) {
			ManagedDrone drone = flightPlan.getAssignedDrone();
			if (awaitingTakeOffFlights.contains(drone)) {
				drone.unassignFlight();
				drone.getFlightModeState().setModeToOnGround();
				drone.getFlightSafetyModeState().setSafetyModeToNormal();
				flightPlan.clearAssignedDrone();
			}
		}

		// Tell current Flights to return home
		for (FlightPlan flightPlan : currentFlights) {
			ManagedDrone drone = flightPlan.getAssignedDrone();
			if (drone.getFlightModeState().isFlying())
				drone.returnToHome();
		}
	}

	/**
	 * 
	 * @return true if any flights are currently in "awaiting take off" mode.
	 */
	public boolean hasAwaitingTakeOff() {
		return awaitingTakeOffFlights.size() > 0;

	}

	/**
	 * 
	 * @return true if any flights are in "pending" mode
	 */
	public boolean hasPendingFlight() {
		return pendingFlights.size() > 0;

	}

	/**
	 * 
	 * @return true if any flights are awaiting permission to launch
	 */
	public boolean permissionToLaunch() {
		if (!grounded && (currentFlights.size() + awaitingTakeOffFlights.size()) < maximumAllowedCurrentFlights)
			return true;
		else
			return false;
	}

	/**
	 * Assigns the next available flight plan to a pending flight at the top of the pending list.
	 * 
	 * @return the next available flight plan
	 */
	public FlightPlan getNextFlightPlan() {
		if (pendingFlights.size() > 0) {
			FlightPlan flightPlan = pendingFlights.remove(0);
			awaitingTakeOffFlights.add(flightPlan);
			return flightPlan;
		} else
			return null;
	}

	/**
	 * 
	 * @return list of pending flights
	 */
	public List<FlightPlan> getPendingFlights() {
		return pendingFlights;
	}

	/**
	 * 
	 * @return list of flights awaiting permission to takeoff
	 */
	public List<FlightPlan> getAwaitingTakeOffFlights() {
		return awaitingTakeOffFlights;
	}

	/**
	 * 
	 * @return arraylist of currently flying flights
	 */ 
	public List<FlightPlan> getCurrentFlights() {
		return currentFlights;
	}

	/**
	 * 
	 * @return arraylist of completed flights
	 */
	public List<FlightPlan> getCompletedFlights() {
		return completedFlights;
	}

	/**
	 * Adds a new flight to the pending list
	 * 
	 * @param flightPlan
	 *          Flight plan to be added to pending flights
	 */
	public void addNewFlight(FlightPlan flightPlan) {
		pendingFlights.add(flightPlan);
	}

	/**
	 * Checks if the next pending flight is able to takeoff. Currently takeoff occurs in order of pending list.
	 * 
	 * @param droneFleet
	 * @throws FlightZoneException
	 */
	public void checkForTakeOffReadiness(DroneFleet droneFleet) throws FlightZoneException {
		// Technical debt.
		// Checks first waiting drone each time it is called.
		if (awaitingTakeOffFlights.size() > 0) {
			FlightPlan awaitingFlightPlan = awaitingTakeOffFlights.get(0);
			ManagedDrone drone = awaitingFlightPlan.getAssignedDrone();
			if (safetyMgr.permittedToTakeOff(drone)) {
				LOGGER.info(drone.getDroneName() + " taking off");
				drone.setTargetAltitude(awaitingFlightPlan.getStartLocation().getAltitude());
				drone.takeOff();
				moveAwaitingToCurrent(awaitingFlightPlan);
			}
		}
	}

	/**
	 * Checks to see if any flights have just landed.
	 * 
	 * @param droneFleet
	 * @param safetyMgr
	 */
	public void checkForLandedFlights(DroneFleet droneFleet, DroneSeparationMonitor safetyMgr) {
		ArrayList<FlightPlan> justLanded = new ArrayList<>();
		for (FlightPlan flightPlan : currentFlights) {
			if (flightPlan.getAssignedDrone() != null) {
				ManagedDrone drone = flightPlan.getAssignedDrone();
				if (drone.getFlightModeState().isOnGround()) {
					safetyMgr.detachDrone(drone);
					justLanded.add(flightPlan);
					LOGGER.info("Drone " + drone.getDroneName() + " has landed.");
				}
			}
		}
		for (FlightPlan flightPlan : justLanded) {
			moveCurrentToCompleted(flightPlan);
			ManagedDrone drone = flightPlan.getAssignedDrone();
			droneFleet.returnDroneToAvailablePool(drone);
		}
	}

	/**
	 * 
	 * @return maximum number of drones that are allowed to fly
	 */
	public int getMaximumNumberFlightsAllowed() {
		return maximumAllowedCurrentFlights;
	}

	/**
	 * Moves a flight from current to completed status
	 * 
	 * @param flightPlan
	 *          associated with the flight
	 */
	public void moveCurrentToCompleted(FlightPlan flightPlan) {
		if (currentFlights.contains(flightPlan)) {
			currentFlights.remove(flightPlan);
			completedFlights.add(flightPlan);
			try {
				flightPlan.setStatusToCompleted();
			} catch (FlightZoneException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Moves flight from awaiting permission to take off -- to current
	 * 
	 * @param flightPlan
	 *          associated with flight
	 */
	public void moveAwaitingToCurrent(FlightPlan flightPlan) {
		if (awaitingTakeOffFlights.contains(flightPlan)) {
			awaitingTakeOffFlights.remove(flightPlan);
			currentFlights.add(flightPlan);
		}
	}

	/**
	 * Moves flight from pending to awaiting permission to takeoff
	 * 
	 * @param flightPlan
	 *          associated with flight
	 */
	public void movePendingToAwaiting(FlightPlan flightPlan) {
		if (pendingFlights.contains(flightPlan)) {
			pendingFlights.remove(flightPlan);
			awaitingTakeOffFlights.add(flightPlan);
		}
	}
}
