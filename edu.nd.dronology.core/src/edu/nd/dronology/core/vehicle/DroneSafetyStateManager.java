package edu.nd.dronology.core.vehicle;

/**
 * Associates a drone safety state object with a drone. ONLY set this in the drone constructor. NEVER interchange at runtime - otherwise drone state will be incorrectly changed.
 * 
 * @author Jane Cleland-Huang
 * @version 0.01
 *
 */
public class DroneSafetyStateManager {

	private enum SafetyMode {
		DIVERTED, HALTED, NORMAL
	}

	private SafetyMode safetyMode;

	/**
	 * Constructor States for both FlightMode and SafetyMode set to initial state
	 */
	public DroneSafetyStateManager() {
		safetyMode = SafetyMode.NORMAL;
	}

	// Setters
	public void setSafetyModeToNormal() {
		safetyMode = SafetyMode.NORMAL;
	}

	public void setSafetyModeToDiverted() {
		safetyMode = SafetyMode.DIVERTED;
	}

	public void setSafetyModeToHalted() {
		safetyMode = SafetyMode.HALTED;
	}

	public boolean isSafetyModeNormal() {
		return safetyMode == SafetyMode.NORMAL;
	}

	public boolean isSafetyModeDiverted() {
		return safetyMode == SafetyMode.DIVERTED;

	}

	public boolean isSafetyModeHalted() {
		return safetyMode == SafetyMode.HALTED;

	}

	public String getSafetyStatus() {
		return safetyMode.toString();
	}

}
