package edu.nd.dronology.services.core.listener;

import java.rmi.RemoteException;
import java.util.Set;

import edu.nd.dronology.services.core.api.IRemotable;

public interface IItemChangeListener extends IRemotable{

	void itemChanged(Set<String> info) throws RemoteException;
	
}
