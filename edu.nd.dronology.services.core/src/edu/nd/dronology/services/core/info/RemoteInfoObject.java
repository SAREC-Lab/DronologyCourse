package edu.nd.dronology.services.core.info;

import java.util.Map;
import java.util.Map.Entry;

import edu.nd.dronology.services.core.api.IRemoteInfoObject;

import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * Abstract base class for all info objects.
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class RemoteInfoObject implements IRemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1610958467030762516L;
	private String name;
	private String id;
	private Map<String, String> attributes = new TreeMap<>();

	/**
	 * @param name
	 *          The name of the object; must not be null.
	 * @param id
	 *          The unique id of the object; must not be null.
	 */
	public RemoteInfoObject(String name, String id) {
		if (name == null || id == null) {
			throw new IllegalArgumentException("Parameter must not be null!");
		}
		this.name = name;
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Set<Entry<String, String>> getAttributes() {
		return attributes.entrySet();
	}

	@Override
	public void addAttribute(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Parameter must not be null!");
		}
		attributes.put(key, value);
	}

	@Override
	public String getAttribute(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Parameter must not be null!");
		}
		if (attributes.containsKey(key)) {
			return attributes.get(key);
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteInfoObject other = (RemoteInfoObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
	@Override
	public int compareTo(IRemoteInfoObject o) {
		return this.id.compareTo(o.getId());
	}

}
