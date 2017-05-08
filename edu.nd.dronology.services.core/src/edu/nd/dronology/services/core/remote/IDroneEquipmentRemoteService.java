package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.info.EquipmentTypeInfo;

public interface IDroneEquipmentRemoteService extends IRemoteableService, IFileTransmitRemoteService<DroneEquipmentInfo> {

	Collection<EquipmentTypeInfo> getEquipmentTypes() throws RemoteException;
	
}
