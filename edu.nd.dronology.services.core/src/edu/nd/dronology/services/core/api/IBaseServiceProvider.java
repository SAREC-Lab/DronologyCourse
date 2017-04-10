package edu.nd.dronology.services.core.api;

import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IBaseServiceProvider {

	void init(String serverHost, int serverPort);
 
	IRemoteManager getRemoteManager() throws DronologyServiceException;

}
