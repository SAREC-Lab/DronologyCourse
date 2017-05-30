package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import edu.nd.dronology.services.specification.DroneSpecificationService;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSpecificationServiceRemoteFacade extends AbstractRemoteFacade implements IDroneSpecificationRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSpecificationServiceRemoteFacade.class);
	private static volatile DroneSpecificationServiceRemoteFacade INSTANCE;

	protected DroneSpecificationServiceRemoteFacade() throws RemoteException {
		super(DroneSpecificationService.getInstance());
	}

	public static IDroneSpecificationRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (DroneSpecificationServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new DroneSpecificationServiceRemoteFacade();
					}
				}
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public byte[] requestFromServer(String id) throws RemoteException, DronologyServiceException {
		return DroneSpecificationService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		DroneSpecificationService.getInstance().transmitToServer(id, content);

	}

	@Override
	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException {
		throw new NotImplementedException();
	}

	@Override
	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException {
		throw new NotImplementedException();
	}

	@Override
	public Collection<DroneSpecificationInfo> getItems() throws RemoteException {
		return DroneSpecificationService.getInstance().getItems();
	}

	@Override
	public DroneSpecificationInfo createItem() throws RemoteException, DronologyServiceException {
		return DroneSpecificationService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Collection<TypeSpecificationInfo> getTypeSpecifications() throws RemoteException {
		return DroneSpecificationService.getInstance().getTypeSpecifications();
	}

}