package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightManagerRemoteService extends IRemoteableService {

	// public FlightInfo getFlightDetails() throws RemoteException;

	// @Deprecated
	// void planFlight(String planName, LlaCoordinate coordinates,
	// List<LlaCoordinate> flight) throws RemoteException;
	//
	// @Discuss(discuss = "change from coordinates to real route....")
	// @Deprecated
	// void planFlight(String uavid, String planName, LlaCoordinate start,
	// List<LlaCoordinate> wayPoints)
	// throws RemoteException;

	void planFlight(String planName, List<Waypoint> wayPoints) throws RemoteException, Exception;

	void planFlight(String uavid, String planName, List<Waypoint> wayPoints) throws RemoteException, Exception;

	void returnToHome(String iavid) throws RemoteException, Exception;

	void pauseFlight(String iavid) throws RemoteException, Exception;

	FlightInfo getFlightInfo(String uavId) throws RemoteException, Exception;

	Collection<FlightPlanInfo> getCurrentFlights() throws RemoteException;  

}
