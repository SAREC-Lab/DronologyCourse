package edu.nd.dronology.services.core.persistence;

public class PersistenceException extends Throwable {

	public PersistenceException(Exception e) {
		super(e);
	}

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7093839510895063175L;

}
