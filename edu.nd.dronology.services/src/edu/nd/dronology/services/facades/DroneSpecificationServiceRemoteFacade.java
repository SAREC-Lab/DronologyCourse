package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.info.EquipmentTypeInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IDroneEquipmentRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.equipment.DroneEquipmentService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneEquipmentServiceRemoteFacade extends AbstractRemoteFacade implements IDroneEquipmentRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneEquipmentServiceRemoteFacade.class);
	private static volatile DroneEquipmentServiceRemoteFacade INSTANCE;

	protected DroneEquipmentServiceRemoteFacade() throws RemoteException {
		super(DroneEquipmentService.getInstance());
	}

	public static IDroneEquipmentRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (DroneEquipmentServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new DroneEquipmentServiceRemoteFacade();
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
		return DroneEquipmentService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		DroneEquipmentService.getInstance().transmitToServer(id, content);

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
	public Collection<DroneEquipmentInfo> getItems() throws RemoteException {
		return DroneEquipmentService.getInstance().getItems();
	}

	@Override
	public DroneEquipmentInfo createItem() throws RemoteException, DronologyServiceException {
		return DroneEquipmentService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Collection<EquipmentTypeInfo> getEquipmentTypes() throws RemoteException {
		return DroneEquipmentService.getInstance().getEquipmentTypes();
	}

}