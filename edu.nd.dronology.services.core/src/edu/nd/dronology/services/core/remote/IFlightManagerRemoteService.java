package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.List;

import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.services.core.info.FlightInfo;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightManagerRemoteService extends IRemoteableService {

	void planFlight(Coordinates coordinates, List<Coordinates> flight) throws RemoteException;

	public FlightInfo getFlightDetails() throws RemoteException;

}
