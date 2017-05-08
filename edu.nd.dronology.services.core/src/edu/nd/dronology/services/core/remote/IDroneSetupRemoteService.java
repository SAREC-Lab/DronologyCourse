package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.gui_middleware.DroneStatus;
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
	void initializeDrones(List<String[]> newDrones, boolean b) throws RemoteException;

	@Deprecated
	Map<String, DroneStatus> getDrones() throws RemoteException;

	void initializeDrones(DroneInitializationInfo... info) throws RemoteException, DronologyServiceException;

	void addDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

	void removeDroneStatusChangeListener(IDroneStatusChangeListener listener)
			throws RemoteException, DronologyServiceException;

}
