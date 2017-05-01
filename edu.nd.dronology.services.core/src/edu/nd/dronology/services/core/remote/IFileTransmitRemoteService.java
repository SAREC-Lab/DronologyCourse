package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.api.IRemotable;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IFileTransmitRemoteService<ITEM_TYPE> extends IRemotable {

	byte[] requestFromServer(String id) throws RemoteException, DronologyServiceException;

	void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException;

	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException;

	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException;

	
	Collection <ITEM_TYPE> getItems() throws RemoteException;

	ITEM_TYPE createItem() throws RemoteException, DronologyServiceException;

	void deleteItem(String itemid) throws RemoteException, DronologyServiceException;
}
