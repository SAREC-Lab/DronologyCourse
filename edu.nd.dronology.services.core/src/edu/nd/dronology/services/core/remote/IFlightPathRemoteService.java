package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.FlightPathCategoryInfo;
import edu.nd.dronology.services.core.info.FlightPathInfo;
/**
 * Meta-Model Service Interface: Handling artifact  models.<br>
 * Extends {@link IFileTransmitRemoteService} and provides methods for retrieving and saving models from the server.
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightPathRemoteService extends IRemoteableService, IFileTransmitRemoteService<FlightPathInfo> {

	
 Collection<FlightPathCategoryInfo> getFlightPathCategories() throws RemoteException;
	
}
