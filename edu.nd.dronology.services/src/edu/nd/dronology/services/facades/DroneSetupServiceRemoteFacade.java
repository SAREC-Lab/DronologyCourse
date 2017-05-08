package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.gui_middleware.DroneStatus;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSetupServiceRemoteFacade extends AbstractRemoteFacade implements IDroneSetupRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSetupServiceRemoteFacade.class);
	private static DroneSetupServiceRemoteFacade INSTANCE;

	protected DroneSetupServiceRemoteFacade() throws RemoteException {
		super(DroneSetupService.getInstance());
	}

	public static IDroneSetupRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				INSTANCE = new DroneSetupServiceRemoteFacade();
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public void initializeDrones(List<String[]> newDrones, boolean b) throws RemoteException {
		DroneSetupService.getInstance().initializeDrones(newDrones, b);

	}

	@Override
	public Map<String, DroneStatus> getDrones() throws RemoteException {
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

}