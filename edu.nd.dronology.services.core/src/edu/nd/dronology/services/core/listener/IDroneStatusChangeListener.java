package edu.nd.dronology.services.core.listener;

import java.rmi.RemoteException;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.api.IRemotable;

public interface IDroneStatusChangeListener extends IRemotable{

	void droneStatusChanged(IUAVProxy status) throws RemoteException;
	
}
