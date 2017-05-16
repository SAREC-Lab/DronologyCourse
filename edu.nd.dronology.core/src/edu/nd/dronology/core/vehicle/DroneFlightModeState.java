package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.exceptions.FlightZoneException;

/**
 * Associates a drone state object with a drone. ONLY set this in the drone constructor. NEVER interchange at runtime - otherwise drone state will be incorrectly changed. State changes for Flight
 * Modes must follow the transition: OnGround -> AwaitingTakeOffClearance -> TakingOff -> Flying -> Landing All other transitions will result in an exception being thrown
 * 
 * @author Jane Cleland-Huang
 * @version 0.01
 *
 */
public class DroneFlightModeState {
	// Status
	private enum FlightMode {
		ON_GROUND, AWAITING_TAKEOFF_CLEARANCE, TAKING_OFF, FLYING, LANDING
	}

	private FlightMode currentFlightMode = FlightMode.ON_GROUND;

	/**
	 * Constructor States for both FlightMode and SafetyMode set to initial state
	 */
	public DroneFlightModeState() {
		currentFlightMode = FlightMode.ON_GROUND;
	}

	/////////////////////
	// Setters
	/////////////////////
	/**
	 * Set Flight Mode to OnGround
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToOnGround() throws FlightZoneException {
		if (currentFlightMode == FlightMode.LANDING) {
			currentFlightMode = FlightMode.ON_GROUND;
		} else {
			throw new FlightZoneException(
					"You may not transition to " + FlightMode.ON_GROUND + " directly from " + currentFlightMode);
		}
	}

	/**
	 * Set Flight mode to awaiting Takeoff Clearance
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToAwaitingTakeOffClearance() throws FlightZoneException {
		if (currentFlightMode == FlightMode.ON_GROUND) {
			currentFlightMode = FlightMode.AWAITING_TAKEOFF_CLEARANCE;
		} else {
			throw new FlightZoneException(
					"You may not transition to " + FlightMode.AWAITING_TAKEOFF_CLEARANCE + " directly from " + currentFlightMode);
		}
	}

	/**
	 * Set flight mode to Taking off
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToTakingOff() throws FlightZoneException {
		if (currentFlightMode == FlightMode.AWAITING_TAKEOFF_CLEARANCE) {
			currentFlightMode = FlightMode.TAKING_OFF;
		} else {
			throw new FlightZoneException(
					"You may not transition to " + FlightMode.TAKING_OFF + " directly from " + currentFlightMode);
		}
	}

	/**
	 * Set flight mode to Flying
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToFlying() throws FlightZoneException {
		if (currentFlightMode == FlightMode.TAKING_OFF) {
			currentFlightMode = FlightMode.FLYING;
		} else {
			throw new FlightZoneException(
					"You may not transition to " + FlightMode.FLYING + " directly from " + currentFlightMode);
		}
	}

	/**
	 * Set flight mode to Landing
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToLanding() throws FlightZoneException {
		if (currentFlightMode == FlightMode.FLYING) {
			currentFlightMode = FlightMode.LANDING;
		} else {
			throw new FlightZoneException(
					"You may not transition to " + FlightMode.LANDING + " directly from " + currentFlightMode);
		}
	}

	///////////////////////////////////
	// Getters
	//////////////////////////////////

	/**
	 * 
	 * @return true if drone is currently on the ground, false otherwise
	 */
	public boolean isOnGround() {
		return currentFlightMode == FlightMode.ON_GROUND;

	}

	/**
	 * 
	 * @return true if drone is currently in AwaitingTakeOffClearance mode, false otherwise
	 */
	public boolean isAwaitingTakeoffClearance() {
		return currentFlightMode == FlightMode.AWAITING_TAKEOFF_CLEARANCE;

	}

	/**
	 * 
	 * @return true if drone is currently taking off, false otherwise
	 */
	public boolean isTakingOff() {
		return currentFlightMode == FlightMode.TAKING_OFF;
	}

	/**
	 * 
	 * @return true if drone is currently flying, false otherwise
	 */
	public boolean isFlying() {
		return currentFlightMode == FlightMode.FLYING;

	}

	/**
	 * 
	 * @return true if drone is currently landing, false otherwise
	 */
	public boolean isLanding() {
		return currentFlightMode == FlightMode.LANDING;

	}

	/**
	 * 
	 * @return current status
	 */
	public String getStatus() {
		return currentFlightMode.toString();
	}
}
