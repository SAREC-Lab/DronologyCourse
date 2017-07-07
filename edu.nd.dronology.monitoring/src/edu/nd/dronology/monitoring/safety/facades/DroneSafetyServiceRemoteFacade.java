package edu.nd.dronology.monitoring.safety.facades;

import java.rmi.RemoteException;

import edu.nd.dronology.monitoring.safety.internal.DroneSafetyService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSafetyServiceRemoteFacade extends AbstractRemoteFacade implements IDroneSafetyRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSafetyServiceRemoteFacade.class);
	private static volatile DroneSafetyServiceRemoteFacade INSTANCE;

	protected DroneSafetyServiceRemoteFacade() throws RemoteException {
		super(DroneSafetyService.getInstance());
	}

	public static IDroneSafetyRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (DroneSafetyServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new DroneSafetyServiceRemoteFacade();
					}
				}
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;

	}

	@Override
	public void registerDroneSafetyCase(String uavid, String safetycase)
			throws DronologyServiceException, RemoteException {
		DroneSafetyService.getInstance().registerDroneSafetyCase(uavid, safetycase);

	}

}