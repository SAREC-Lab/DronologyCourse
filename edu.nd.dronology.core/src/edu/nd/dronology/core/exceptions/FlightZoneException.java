package edu.nd.dronology.core.exceptions;

/**
 * Supports customized flight zone exceptions
 * 
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class FlightZoneException extends Throwable {
	/**
	* 
	*/
	private static final long serialVersionUID = 8522577350228262490L;

	public FlightZoneException(String msg) {
		super(msg);
	}

	public FlightZoneException(Throwable t) {
		super(t);
	}
}
