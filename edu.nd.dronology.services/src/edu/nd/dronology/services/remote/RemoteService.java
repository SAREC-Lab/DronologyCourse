package edu.nd.dronology.services.remote;

import java.util.Collection;

import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.remote.RemoteInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.remote.IRemoteConnectionServiceInstance;
import edu.nd.dronology.services.instances.remote.RemoteConnectionServiceInstance;

public class RemoteService extends AbstractServerService<IRemoteConnectionServiceInstance> {

	private static volatile RemoteService INSTANCE;

	@Override
	protected IRemoteConnectionServiceInstance initServiceInstance() {
		return new RemoteConnectionServiceInstance();
	}

	/**
	 * @return The singleton RemoteService instance
	 */
	public static RemoteService getInstance() {
		if (INSTANCE == null) {
			synchronized (RemoteService.class) {
				if (INSTANCE == null) {
					INSTANCE = new RemoteService();
				}
			}
		}
		return INSTANCE;
	}

	public void performCleanup() {
		// TODO Auto-generated method stub

	}

	public Collection<RemoteInfo> getRegisteredRemoteClients() {
		return serviceInstance.getRegisteredRemoteClients();
	}

	public void register(RemoteInfo rInfo) {
		serviceInstance.register(rInfo);
	}

	public void unregister(RemoteInfo rInfo) {
		serviceInstance.unregister(rInfo);
	}

	// public void logExternal(LogEventAdapter event) {
	// serviceInstance.logExternal(event);
	// }
	public void addRemoteManager(IRemoteManager manager) throws DronologyServiceException {
		serviceInstance.addRemoteManager(manager);

	}

}
