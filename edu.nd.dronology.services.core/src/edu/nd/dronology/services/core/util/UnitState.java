package edu.nd.dronology.services.core.util;

/**
 * Enum that reflects the current state of a Probe.
 * 
 * @author Michael Vierhauser
 * 
 */
public enum UnitState {

	/**
	 * The unit is running and able to send data.
	 */
	RUNNING,

	/**
	 * The unit is paused and no data is sent to the server.
	 */
	PAUSED,
	/**
	 * The unit has been terminated. No further data will be processed until restarted.
	 */
	TERMINATED,

	/**
	 * Current status is unknown.
	 */
	UNKNOWN,

	/**
	 * The unit is registered at the server.
	 */
	REGISTERED,

	/**
	 * The unit has been unregistered from the server.
	 */
	UNREGISTERED, 
	
	
	ERROR

}
