package edu.nd.dronology.services.core.listener;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.api.ServiceStatus;

/**
 * Listener interface for notifying on status changes of a {@link IServiceInstance}
 * 
 *  
 * @author Michael Vierhauser
 * 
 */
public interface IServiceListener  {

	/**
	 * 
	 * @param newState The new status of the server process.
	 */
	public void statusChanged(ServiceStatus newState);

}
