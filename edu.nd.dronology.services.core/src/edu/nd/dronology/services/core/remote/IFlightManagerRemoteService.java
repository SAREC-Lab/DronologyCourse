package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.List;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.services.core.info.FlightInfo;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightManagerRemoteService extends IRemoteableService {

	void planFlight(Coordinate coordinates, List<Coordinate> flight) throws RemoteException;

	public FlightInfo getFlightDetails() throws RemoteException;

}
