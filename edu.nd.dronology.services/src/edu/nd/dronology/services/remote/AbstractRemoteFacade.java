package edu.nd.dronology.services.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.listener.IServiceListener;
import edu.nd.dronology.services.core.remote.IRemoteableService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/** 
 * Base implementation for all services implementing {@link IRemoteableService}
 * @author Michael Vierhauser
 *
 */
public class AbstractRemoteFacade extends UnicastRemoteObject implements IRemoteableService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7820839372017475896L;
	protected AbstractServerService<?> service;

	protected AbstractRemoteFacade(AbstractServerService<?> service) throws RemoteException {
		super();
		this.service = service;
	}

	@Override
	public ServiceInfo getServiceInfo() throws RemoteException {
		return service.getServerInfo();
	}

	@Override
	public void startService() throws RemoteException, DronologyServiceException {
		service.startService();
	}

	@Override
	public void stopService() throws RemoteException, DronologyServiceException {
		service.stopService();

	}

	@Override
	public boolean removeServiceListener(IServiceListener servicelistener) throws RemoteException {
		return service.removeServiceListener(servicelistener);

	}

	@Override
	public boolean addServiceListener(IServiceListener servicelistener) throws RemoteException {
		return service.removeServiceListener(servicelistener);

	}

	@Override
	public void restartService() throws RemoteException, DronologyServiceException {
		service.restartService();
	}

}
