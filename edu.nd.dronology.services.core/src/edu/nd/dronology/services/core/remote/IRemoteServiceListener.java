package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;

import edu.nd.dronology.services.core.api.IRemotable;
import edu.nd.dronology.services.core.api.ServiceStatus;
/**
 * Listener for notifying clients on status changes of the registered service.
 * 
 * 
 * @author Michael Vierhauser
 *
 */
public interface IRemoteServiceListener extends IRemotable{
	
	/**
	 * 
	 * @param newState The current status of the service.
	 * @throws RemoteException
	 */
	public void statusChanged(ServiceStatus newState) throws RemoteException;


}
