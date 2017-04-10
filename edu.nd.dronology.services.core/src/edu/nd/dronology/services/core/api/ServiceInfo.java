package edu.nd.dronology.services.core.api;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.nd.dronology.util.NullUtil;

/**
 * 
 * Wrapper object for various attributes and properties of an {@link IServiceInstance}
 * 
 * 
 * @author Michael Vierhauser
 * 
 */
public class ServiceInfo implements IRemoteTransferObject, Comparable<ServiceInfo> {

	public static final String ATTRIBUTE_SERVICEID = "Service ID";
	public static final String ATTRIBUTE_SOCKET = "Socket";
	public static final String ATTRIBUTE_REMOTE = "Remote";
	public static final String ATTRIBUTE_FILE = "File";
	
	public static final String ATTRIBUTE_TYPE= "Type";
	public static final String ATTRIBUTE_DISPATCHQUEUESIZE = "Queue Size";
	public static final String ATTRIBUTE_DISTRIBUTORPRIORITY = "Priority";
	public static final String ATTRIBUTE_SUBSCRIBER = "Registered Subscriber";
	public static final String ATTRIBUTE_TYPE_DISTRIBUTOR = "Distributor";
	public static final String ATTRIBUTE_TYPE_SERVICE = "Service";

	public static final String ATTRIBUTE_TIMESTAMP = "Timestamp";
	/**
	 * 
	 */
	private static final long serialVersionUID = 376263095158789667L;

	


	private Map<String, String> attributes = new TreeMap<>();
	private Map<String, String> properties = new TreeMap<>();
	private String serviceID;
	private String description;
	private ServiceStatus serviceStatus = ServiceStatus.STOPPED;
	private String className;
	private final int order;

	/**
	 * 
	 * @param serviceID
	 * @param serviceStatus
	 * @param description
	 * @param attributes
	 * @param properties
	 * @param order
	 */
	public ServiceInfo(String serviceID, ServiceStatus serviceStatus, String description, Map<String, String> attributes,
			Map<String, String> properties, int order) {
		super();
		NullUtil.checkNull(attributes, properties);
		this.attributes = new TreeMap<>(attributes);
		this.properties = new TreeMap<>(properties);
		this.serviceID = serviceID;
		this.serviceStatus = serviceStatus;
		this.description = description;
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> e : attributes.entrySet()) {
			sb.append(e.getKey());
			sb.append(":");
			sb.append(e.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return The attributes of the server process.
	 */
	public Map<String, String> getDetails() {
		return attributes;
	}

	/**
	 * 
	 * @return The configuration properties of the server process.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @return The id of the service.
	 */
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * @return A description for the service.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return M map of configuration properties.
	 */
	public Map<String, String> getConfigurationProperties() {
		return properties;
	}

	/**
	 * @return The current status of the service.
	 */
	public ServiceStatus getStatus() {
		return serviceStatus;
	}

	/**
	 * @return The canonical name of the service class.
	 */
	public String getServiceClass() {
		return className;
	}

	/**
	 * @param className
	 *          The canonical name of the service class.
	 */
	public void setServiceClass(String className) {
		this.className = className;
	}

	@Override
	public int compareTo(ServiceInfo that) {
		return Integer.compare(this.order, that.order);
	}

	/**
	 * Adds an key-value attribute pair to the attribute table.
	 * 
	 * @param key
	 * @param value
	 */
	public void addAttribute(String key, String value) {
		attributes.put(key, value);

	}

	public void setStatus(ServiceStatus serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

}
