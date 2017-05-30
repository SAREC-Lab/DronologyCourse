package edu.nd.dronology.services.core.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.util.UnitState;

/**
 * Abstract class for registerable units that register at the server.<br>
 * 
 * @author Michael Vierhauser
 * 
 */

public abstract class AbstractRegisterableUnit implements Serializable {

	private static final long serialVersionUID = 2983958174901066192L;
	/** The timestamp when the unit was created **/
	public static final String TIMESTAMP = "timestamp";
	/** The IP address of the sender **/
	public static String IP_ADDRESS = "ipaddress";
	/** The designated command port of the unit **/
	public static final String COMMAND_PORT = "commandport";
	/** Unregister flag **/
	public static final String UNREGISTER = "unregister";
	/** The current state of the unit **/
	public static String STATE = "state";
	protected final String ID;
	

	
	public static final  String HAS_REMOTEOBJECT = "hasRemoteObject";
	public static final String IS_MANAGED = "isManaged";
	
	protected final String hostname;
	private Map<String, String> attributes = new HashMap<>();

	protected AbstractRegisterableUnit(String ID, String hostname) {
		super();
		this.ID = ID;
		this.hostname = hostname;
		addAttribute(STATE, UnitState.UNKNOWN.name());
	}

	/**
	 * 
	 * @return The id of the unit.
	 */
	public String getID() {
		return ID;
	}

	/**
	 * 
	 * @return The hostname of the unit.
	 */
	public String getHost() {
		return hostname;
	}

	/**
	 * 
	 * @return A map of key-values of attributes.
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ID.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object thatObject) {
		if (this == thatObject) {
			return true;
		}
		if (thatObject == null) {
			return false;
		}
		if (getClass() != thatObject.getClass()) {
			return false;
		}
		AbstractRegisterableUnit other = (AbstractRegisterableUnit) thatObject;
		if (!other.ID.equals(this.ID)) {
			return false;
		}
		return true;
	}

	/**
	 * Adds a new attribute to the unit.
	 * 
	 * @param key
	 * @param value
	 */
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @return The attribute associated with the key.<br>
	 *         Retunrs {@code null} if no attribute exists for the give key.
	 */
	public String getAttribute(String key) {
		if (attributes.containsKey(key)) {
			return attributes.get(key);
		}
		return null;
	}
/**
 * 
 * @return The type of the unit as human readable string representation.
 */
	public abstract String getUnitType();

}
