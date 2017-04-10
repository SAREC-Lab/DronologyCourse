package edu.nd.dronology.services.core.api;

import java.util.Map.Entry;
import java.util.Set;

/**
 * Base interface for all serializable objects sent over remote interfaces containing information on constraints, requirements, models etc.
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IRemoteInfoObject extends IRemoteTransferObject, Comparable<IRemoteInfoObject> {

	/**
	 * 
	 * @param key
	 *          The key of the attribute.
	 * @return The value for the given attribute key.<br>
	 *         Returns {@code null} if no value for the given key is available.
	 */
	String getAttribute(String key);

	String getName();

	String getId();

	Set<Entry<String, String>> getAttributes();

	void addAttribute(String key, String value);

}
