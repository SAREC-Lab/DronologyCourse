package edu.nd.dronology.services.core.remote;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import edu.nd.dronology.services.core.base.AbstractRegisterableUnit;

/**
 * 
 * Transmittable object for a Remote Clients containing information on the Subscriber.
 * 
 * @author Michael Vierhauser
 * 
 */
@XmlRootElement
public class RemoteInfo extends AbstractRegisterableUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876222975522920266L;

	/** Flag for subscribing to all sources. */
	public static final String ALL = "all";

	private Set<String> subscribeIDs = new HashSet<>();

	protected RemoteInfo(String ID, String hostname) {
		super(ID, hostname);
	}

	/**
	 * @return A list of subscribed sources.
	 */
	public Set<String> getSubscribeIDs() {
		return Collections.unmodifiableSet(subscribeIDs);
	}


	/**
	 * 
	 * @param id The source to be added.
	 */
	public void addSourceID(String id) {
		subscribeIDs.add(id);

	}



	@Override
	public String toString() {
		return "REMOTE CLIENT  [id: " + ID /* + " | callbackURL: " + callbackURL + " subscribeIDs: " + getRegisteredIds() */
				+ "]";
	}

	@Override
	public String getUnitType() {
		return "Subscriber";
	}
}
