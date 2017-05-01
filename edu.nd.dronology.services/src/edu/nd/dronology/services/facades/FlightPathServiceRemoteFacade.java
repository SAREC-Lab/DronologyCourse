package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.FlightPathCategoryInfo;
import edu.nd.dronology.services.core.info.FlightPathInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IFlightPathRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.flightpath.FlightPathService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightPathServiceRemoteFacade extends AbstractRemoteFacade implements IFlightPathRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightPathServiceRemoteFacade.class);
	private static FlightPathServiceRemoteFacade INSTANCE;

	protected FlightPathServiceRemoteFacade() throws RemoteException {
		super(FlightPathService.getInstance());
	}

	public static IFlightPathRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				INSTANCE = new FlightPathServiceRemoteFacade();
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public byte[] requestFromServer(String id) throws RemoteException, DronologyServiceException {
		return FlightPathService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		FlightPathService.getInstance().transmitToServer(id,content);

	}

	@Override
	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return FlightPathService.getInstance().addItemChangeListener(listener);
	}

	@Override
	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return FlightPathService.getInstance().removeItemChangeListener(listener);
	}

	@Override
	public Collection<FlightPathInfo> getItems() throws RemoteException {
		return FlightPathService.getInstance().getItems();
	}

	@Override
	public FlightPathInfo createItem() throws RemoteException, DronologyServiceException {
		return FlightPathService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		FlightPathService.getInstance().deleteItem(itemid);

	}

	@Override
	public Collection<FlightPathCategoryInfo> getFlightPathCategories() {
		return FlightPathService.getInstance().getFlightPathCategories();
	}

}