package edu.nd.dronology.monitoring.service;

import java.util.Collection;

import edu.nd.dronology.monitoring.monitoring.UAVValidationInformation;
import edu.nd.dronology.monitoring.service.internal.DroneSafetyServiceInstance;
import edu.nd.dronology.monitoring.service.internal.IDroneSafetyServiceInstance;
import edu.nd.dronology.monitoring.validation.ValidationEntry;
import edu.nd.dronology.monitoring.validation.ValidationResult;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class DroneSafetyService extends AbstractServerService<IDroneSafetyServiceInstance> {

	private static volatile DroneSafetyService INSTANCE;

	protected DroneSafetyService() {
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneSafetyService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneSafetyService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneSafetyService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneSafetyServiceInstance initServiceInstance() {
		return new DroneSafetyServiceInstance();
	}

	public ValidationResult validateUAVSafetyCase(String uavid, String safetycase) throws DronologyServiceException {
		return serviceInstance.validateUAVSafetyCase(uavid, safetycase);
	}

	public void addValidationListener(IMonitoringValidationListener listener) {
		serviceInstance.addValidationListener(listener);

	}

	public void removeValidationListener(IMonitoringValidationListener listener) {
		serviceInstance.removeValidationListener(listener);

	}

	public Collection<UAVValidationInformation> getValidationInfo() {
		return serviceInstance.getValidationInfo();
	}

	public UAVValidationInformation getValidationInfo(String uavid) throws DronologyServiceException {
		return serviceInstance.getValidationInfo(uavid);
	}

	public void notifyValidationListeners(String uavid, ValidationEntry validationResult) {
		serviceInstance.notifyValidationListeners(uavid,validationResult);
		
	}

}
