package edu.nd.dronology.monitoring.service;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.monitoring.monitoring.UAVValidationInformation;
import edu.nd.dronology.monitoring.validation.ValidationResult;
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
	public ValidationResult validateUAVSafetyCase(String uavid, String safetycase)
			throws DronologyServiceException, RemoteException {
		return DroneSafetyService.getInstance().validateUAVSafetyCase(uavid, safetycase);

	}

	@Override
	public void addValidationListener(IMonitoringValidationListener listener)
			throws DronologyServiceException, RemoteException {
		DroneSafetyService.getInstance().addValidationListener(listener);

	}
	
	@Override
	public void removeValidationListener(IMonitoringValidationListener listener)
			throws DronologyServiceException, RemoteException {
		DroneSafetyService.getInstance().removeValidationListener(listener);

	}

	@Override
	public Collection<UAVValidationInformation> getValidationInfo() {
		return DroneSafetyService.getInstance().getValidationInfo();
	}

	@Override
	public UAVValidationInformation getValidationInfo(String uavid) throws DronologyServiceException {
		return DroneSafetyService.getInstance().getValidationInfo(uavid);
	}

}