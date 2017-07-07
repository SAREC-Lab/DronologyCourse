package edu.nd.dronology.monitoring.safety.facades;

import java.rmi.RemoteException;

import edu.nd.dronology.services.core.remote.IRemoteableService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IDroneSafetyRemoteService extends IRemoteableService {


	void registerDroneSafetyCase(String uavid, String safetycase) throws DronologyServiceException, RemoteException;
}
