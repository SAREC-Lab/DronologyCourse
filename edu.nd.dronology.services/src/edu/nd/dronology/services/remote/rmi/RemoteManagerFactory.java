package edu.nd.dronology.services.remote.rmi;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.services.core.remote.IRemoteManager;

public class RemoteManagerFactory {



	public static List<IRemoteManager> createRMIObjects(Integer port) {
		List<IRemoteManager> manager = new ArrayList<>();
		manager.add(RemoteRMIRemoteObject.getInstance(port));
		return manager;
		
	}



}
