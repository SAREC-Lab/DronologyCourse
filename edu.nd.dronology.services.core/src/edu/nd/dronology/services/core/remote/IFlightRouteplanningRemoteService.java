package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
/**
 * Extends {@link IFileTransmitRemoteService} and provides methods for retrieving and saving models from the server.
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightRouteplanningRemoteService extends IRemoteableService, IFileTransmitRemoteService<FlightRouteInfo> {

	
 Collection<FlightRouteCategoryInfo> getFlightPathCategories() throws RemoteException;
	
}
