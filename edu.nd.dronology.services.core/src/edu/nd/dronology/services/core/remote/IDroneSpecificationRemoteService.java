package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;

public interface IDroneSpecificationRemoteService extends IRemoteableService, IFileTransmitRemoteService<DroneSpecificationInfo> {

	Collection<TypeSpecificationInfo> getTypeSpecifications() throws RemoteException;
	
}
