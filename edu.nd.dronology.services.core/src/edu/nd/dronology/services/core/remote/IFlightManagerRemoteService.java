package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.mission.IMissionPlan;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightManagerRemoteService extends IRemoteableService {

	void planFlight(String planName, List<Waypoint> wayPoints) throws RemoteException, Exception;

	void planFlight(String uavid, String planName, List<Waypoint> wayPoints) throws RemoteException, Exception;

	void planMission(IMissionPlan missionPlan) throws RemoteException, Exception;

	void returnToHome(String iavid) throws RemoteException, Exception;

	void pauseFlight(String iavid) throws RemoteException, Exception;

	FlightInfo getFlightInfo(String uavId) throws RemoteException, Exception;

	Collection<FlightPlanInfo> getCurrentFlights() throws RemoteException;

	void cancelPendingFlights(String uavid) throws RemoteException, Exception;


}
