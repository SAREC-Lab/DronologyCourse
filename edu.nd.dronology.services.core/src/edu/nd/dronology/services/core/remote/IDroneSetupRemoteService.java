package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.core.vehicle.proxy.UAVProxy;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IDroneSetupRemoteService extends IRemoteableService {

	@Deprecated
	Map<String, UAVProxy> getDrones() throws RemoteException;

	Collection<IUAVProxy> getActiveUAVs() throws RemoteException;

	void initializeDrones(DroneInitializationInfo... info) throws RemoteException, DronologyServiceException;

	void addDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

	void removeDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

}
