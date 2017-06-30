package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.List;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.info.FlightInfo;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightManagerRemoteService extends IRemoteableService {

	public FlightInfo getFlightDetails() throws RemoteException;

	@Deprecated
	void planFlight(String planName, LlaCoordinate coordinates, List<LlaCoordinate> flight) throws RemoteException;

	@Discuss(discuss = "change from coordinates to real route....")
	void planFlight(String uavid, String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints)
			throws RemoteException;

}
