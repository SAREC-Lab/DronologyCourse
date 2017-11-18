package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.core.vehicle.proxy.UAVProxy;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSetupServiceRemoteFacade extends AbstractRemoteFacade implements IDroneSetupRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSetupServiceRemoteFacade.class);
	private static volatile DroneSetupServiceRemoteFacade INSTANCE;

	protected DroneSetupServiceRemoteFacade() throws RemoteException {
		super(DroneSetupService.getInstance());
	}

	public static IDroneSetupRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (DroneSetupServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new DroneSetupServiceRemoteFacade();
					}
				}
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;

	}

	@Override
	public Map<String, UAVProxy> getDrones() throws RemoteException {
		return DroneSetupService.getInstance().getDrones();
	}

	@Override
	public void initializeDrones(DroneInitializationInfo... info) throws RemoteException, DronologyServiceException {
		DroneSetupService.getInstance().initializeDrones(info);

	}

	@Override
	public void addDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		DroneSetupService.getInstance().addDroneStatusChangeListener(listener);
	}

	@Override
	public void removeDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		DroneSetupService.getInstance().removeDroneStatusChangeListener(listener);

	}

	@Override
	public Collection<IUAVProxy> getActiveUAVs() throws RemoteException {
		return DroneSetupService.getInstance().getActiveUAVs();
	}

}