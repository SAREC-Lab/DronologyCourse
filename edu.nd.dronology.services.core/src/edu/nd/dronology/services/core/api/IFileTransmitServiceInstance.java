package edu.nd.dronology.services.core.api;

import java.util.Collection;

import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IFileTransmitServiceInstance<ITEM_TYPE> extends IServiceInstance{

	byte[] requestFromServer(String id) throws DronologyServiceException;

	void transmitToServer(String id, byte[] content) throws DronologyServiceException;

	public boolean addItemChangeListener(IItemChangeListener listener);
	
	public boolean removeItemChangeListener(IItemChangeListener listener);
	
	Collection <ITEM_TYPE> getItems();

	ITEM_TYPE createItem() throws  DronologyServiceException;

	void deleteItem(String itemid) throws  DronologyServiceException;
	
	
}
