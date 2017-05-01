package edu.nd.dronology.services.core.remote;

import java.io.Serializable;
import java.rmi.RemoteException;

import edu.nd.dronology.services.core.api.IRemotable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.listener.IServiceListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IRemoteableService extends IRemotable, Serializable {

	ServiceInfo getServiceInfo() throws RemoteException;

	void startService() throws RemoteException, DronologyServiceException;

	void stopService() throws RemoteException, DronologyServiceException;

	boolean removeServiceListener(IServiceListener servicelistener) throws RemoteException;

	boolean addServiceListener(IServiceListener servicelistener) throws RemoteException;

	void restartService() throws RemoteException, DronologyServiceException;

}
