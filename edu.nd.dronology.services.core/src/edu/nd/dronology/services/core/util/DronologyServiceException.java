package edu.nd.dronology.services.core.util;


/**
 * 
 * Default exception that is thrown when any kind of exception occurs server-side Wraps technology specific exceptions such as JMS or RMI exceptions.
 * 
 * @author Michael Vierhauser
 * 
 */
public class DronologyServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4309389079690729311L;

	/**
	 * @param e
	 */
	public DronologyServiceException(Throwable e) {
		super(e);
	}

	/**
	 * @param message
	 */
	public DronologyServiceException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param e
	 */
	public DronologyServiceException(String message, Exception e) {
		super(message,e);
	}

}
