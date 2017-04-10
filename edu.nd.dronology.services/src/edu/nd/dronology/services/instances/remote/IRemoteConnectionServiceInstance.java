package edu.nd.dronology.services.instances.remote;

import java.util.Collection;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.remote.RemoteInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IRemoteConnectionServiceInstance extends IServiceInstance{

	void register(RemoteInfo rInfo);

	Collection<RemoteInfo> getRegisteredRemoteClients();

	void unregister(RemoteInfo rInfo);

	//void logExternal(LogEventAdapter event);

	void addRemoteManager(IRemoteManager manager)  throws DronologyServiceException;

}
