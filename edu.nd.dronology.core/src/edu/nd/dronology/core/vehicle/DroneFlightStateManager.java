package edu.nd.dronology.core.vehicle;

import com.github.oxo42.stateless4j.StateMachine;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Associates a drone state object with a drone. ONLY set this in the drone constructor. NEVER interchange at runtime - otherwise drone state will be incorrectly changed. State changes for Flight
 * Modes must follow the transition: OnGround -> AwaitingTakeOffClearance -> TakingOff -> Flying -> Landing All other transitions will result in an exception being thrown
 * 
 * @author Jane Cleland-Huang
 * @version 0.01
 *
 */
public class DroneFlightStateManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneFlightStateManager.class);

	@Discuss(discuss = "in air_air mode not considered so far..needs to be included!")
	private enum FlightMode {
		ON_GROUND, AWAITING_TAKEOFF_CLEARANCE, TAKING_OFF, FLYING, IN_AIR, LANDING
	}

	private enum FlightModeTransition {
		TO_ON_GROUND, PLAN_ASSIGNED, TAKEOFF_GRANTED, TARGET_ALTITUED_REACHED, PLAN_COMPLETE, ZERO_ALTITUED_REACHED, LANDING_GRANTED;
	}

	StateMachine<FlightMode, FlightModeTransition> uavStateMachine;

	private FlightMode currentFlightMode = FlightMode.ON_GROUND;

	private final String droneId;

	/**
	 * Constructor States for both FlightMode and SafetyMode set to initial state
	 */
	public DroneFlightStateManager(ManagedDrone drone) {
		this.droneId = drone.getDroneName();
		currentFlightMode = FlightMode.ON_GROUND;
		buildStateMachine();
	}

	private void buildStateMachine() {
		uavStateMachine = new StateMachine<>(FlightMode.ON_GROUND);
		uavStateMachine.configure(FlightMode.ON_GROUND).permit(FlightModeTransition.PLAN_ASSIGNED,
				FlightMode.AWAITING_TAKEOFF_CLEARANCE);
		uavStateMachine.configure(FlightMode.AWAITING_TAKEOFF_CLEARANCE).permit(FlightModeTransition.TAKEOFF_GRANTED,
				FlightMode.TAKING_OFF);
		uavStateMachine.configure(FlightMode.TAKING_OFF).permit(FlightModeTransition.TARGET_ALTITUED_REACHED,
				FlightMode.FLYING);
		uavStateMachine.configure(FlightMode.FLYING).permit(FlightModeTransition.PLAN_COMPLETE, FlightMode.IN_AIR);
		uavStateMachine.configure(FlightMode.IN_AIR).permit(FlightModeTransition.PLAN_ASSIGNED, FlightMode.FLYING);
		uavStateMachine.configure(FlightMode.IN_AIR).permit(FlightModeTransition.LANDING_GRANTED, FlightMode.LANDING);
		uavStateMachine.configure(FlightMode.LANDING).permit(FlightModeTransition.ZERO_ALTITUED_REACHED,
				FlightMode.ON_GROUND);

	}

	/**
	 * Set Flight Mode to OnGround
	 * 
	 * @throws FlightZoneException
	 *           if mode change does not follow allowed state transition.
	 */
	public void setModeToOnGround() throws FlightZoneException {

		if (uavStateMachine.canFire(FlightModeTransition.TO_ON_GROUND)) {
			uavStateMachine.fire(FlightModeTransition.TO_ON_GROUND);
			notifyStateChange(uavStateMachine.getState());

		} else {
			LOGGER.error("You may not transition from '" + uavStateMachine.getState() + "' with trigger '"
					+ FlightModeTransition.TO_ON_GROUND + "'");
		}

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

		if (uavStateMachine.canFire(FlightModeTransition.PLAN_ASSIGNED)) {
			uavStateMachine.fire(FlightModeTransition.PLAN_ASSIGNED);
			notifyStateChange(uavStateMachine.getState());
		} else {
			LOGGER.error("You may not transition from '" + uavStateMachine.getState() + "' with trigger '"
					+ FlightModeTransition.PLAN_ASSIGNED + "'");
		}

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

		if (uavStateMachine.canFire(FlightModeTransition.TAKEOFF_GRANTED)) {
			uavStateMachine.fire(FlightModeTransition.TAKEOFF_GRANTED);
			notifyStateChange(uavStateMachine.getState());
		} else {
			LOGGER.error("You may not transition from '" + uavStateMachine.getState() + "' with trigger '"
					+ FlightModeTransition.TAKEOFF_GRANTED + "'");
		}

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

		if (uavStateMachine.canFire(FlightModeTransition.TARGET_ALTITUED_REACHED)) {
			uavStateMachine.fire(FlightModeTransition.TARGET_ALTITUED_REACHED);
			notifyStateChange(uavStateMachine.getState());
		} else {
			LOGGER.error("You may not transition from '" + uavStateMachine.getState() + "' with trigger '"
					+ FlightModeTransition.TARGET_ALTITUED_REACHED + "'");
		}

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

		if (uavStateMachine.canFire(FlightModeTransition.LANDING_GRANTED)) {
			uavStateMachine.fire(FlightModeTransition.LANDING_GRANTED);
			notifyStateChange(uavStateMachine.getState());
		} else {
			LOGGER.error("You may not transition from '" + uavStateMachine.getState() + "' with trigger '"
					+ FlightModeTransition.LANDING_GRANTED + "'");
		}

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

	private void notifyStateChange(FlightMode state) {
		LOGGER.info("Drone '" + droneId + "' set to: " + uavStateMachine.getState());

	}

}
