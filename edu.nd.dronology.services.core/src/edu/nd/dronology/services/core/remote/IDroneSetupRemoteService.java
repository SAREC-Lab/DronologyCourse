package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.status.DroneStatus;
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
	Map<String, DroneStatus> getDrones() throws RemoteException;

	void initializeDrones(DroneInitializationInfo... info) throws RemoteException, DronologyServiceException;

	void addDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

	void removeDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

}
