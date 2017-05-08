package edu.nd.dronology.services.core.listener;

import java.rmi.RemoteException;

import edu.nd.dronology.core.gui_middleware.DroneStatus;
import edu.nd.dronology.services.core.api.IRemotable;

public interface IDroneStatusChangeListener extends IRemotable{

	void droneStatusChanged(DroneStatus status) throws RemoteException;
	
}
