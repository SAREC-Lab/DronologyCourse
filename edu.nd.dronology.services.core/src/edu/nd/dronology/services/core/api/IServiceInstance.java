package edu.nd.dronology.services.core.api;

import java.util.Properties;

import edu.nd.dronology.services.core.listener.IServiceListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * Base interface for all server processes.
 * 
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IServiceInstance {

	/**
	 * 
	 * @return The id of the service.
	 */
	public String getServiceID();

	/**
	 * 
	 * @return The human readable description for this service.
	 */
	public String getDescription();

	/**
	 * @return The configuration properties of the service containing infos on host, port...
	 */
	public Properties getConfigurationProperties();

	/**
	 * 
	 * @return The current status of the service.
	 */
	public ServiceStatus getStatus();

	/**
	 * Starts the services.
	 * 
	 * @throws DronologyServiceException
	 */
	void startService() throws DronologyServiceException;

	/**
	 * Stops the service.
	 * 
	 * @throws DronologyServiceException
	 */
	void stopService() throws DronologyServiceException;

	/**
	 * @return The {@link ServiceInfo} object.
	 */
	public ServiceInfo getServiceInfo();

	/**
	 * Adds a new {@link IServiceListener} to the service.<br>
	 * The listener is triggered when the status of the service changes.
	 * 
	 * @param listener
	 *          The listener to be added.
	 * @return True if adding was performed successfully, false otherwise.
	 */
	boolean addServiceListener(IServiceListener listener);

	/**
	 * 
	 * @param listener
	 *          The listener to be removed.
	 * @return True if removing was performed successfully, false otherwise.
	 */
	boolean remoteServiceListener(IServiceListener listener);




}
