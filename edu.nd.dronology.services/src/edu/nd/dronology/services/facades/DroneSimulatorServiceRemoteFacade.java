package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IDroneSimulatorRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.dronesimulator.DroneSimulatorService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSimulatorServiceRemoteFacade extends AbstractRemoteFacade implements IDroneSimulatorRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSimulatorServiceRemoteFacade.class);
	private static volatile DroneSimulatorServiceRemoteFacade INSTANCE;

	protected DroneSimulatorServiceRemoteFacade() throws RemoteException {
		super(DroneSimulatorService.getInstance());
	}

	public static IDroneSimulatorRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			synchronized (DroneSimulatorServiceRemoteFacade.class) {
				try {
					if (INSTANCE == null) {
						INSTANCE = new DroneSimulatorServiceRemoteFacade();
					}
				} catch (RemoteException e) {
					LOGGER.error(e);
				}
			}
		}
		return INSTANCE;

	}

	@Override
	public byte[] requestFromServer(String id) throws RemoteException, DronologyServiceException {
		return DroneSimulatorService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		DroneSimulatorService.getInstance().transmitToServer(id, content);

	}

	@Override
	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return DroneSimulatorService.getInstance().addItemChangeListener(listener);
	}

	@Override
	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return DroneSimulatorService.getInstance().removeItemChangeListener(listener);
	}

	@Override
	public Collection<SimulatorScenarioInfo> getItems() throws RemoteException {
		return DroneSimulatorService.getInstance().getItems();
	}

	@Override
	public SimulatorScenarioInfo createItem() throws RemoteException, DronologyServiceException {
		return DroneSimulatorService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		DroneSimulatorService.getInstance().deleteItem(itemid);

	}

	@Override
	public void activateScenario(SimulatorScenarioInfo scenario) throws RemoteException, DronologyServiceException {
		DroneSimulatorService.getInstance().activateScenario(scenario);
		
	}

	@Override
	public Collection<SimulatorScenarioCategoryInfo> getCategories() throws RemoteException {
		return DroneSimulatorService.getInstance().getCategories();
		
	}
}