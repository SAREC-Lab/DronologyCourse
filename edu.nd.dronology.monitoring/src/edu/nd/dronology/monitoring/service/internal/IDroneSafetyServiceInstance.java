package edu.nd.dronology.monitoring.service.internal;

import java.util.Collection;

import edu.nd.dronology.monitoring.monitoring.UAVValidationInformation;
import edu.nd.dronology.monitoring.service.IMonitoringValidationListener;
import edu.nd.dronology.monitoring.validation.ValidationEntry;
import edu.nd.dronology.monitoring.validation.ValidationResult;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IDroneSafetyServiceInstance extends IServiceInstance {

	ValidationResult validateUAVSafetyCase(String uavid, String safetycase) throws DronologyServiceException;

	void addValidationListener(IMonitoringValidationListener listener);

	void removeValidationListener(IMonitoringValidationListener listener);

	Collection<UAVValidationInformation> getValidationInfo();

	UAVValidationInformation getValidationInfo(String uavid) throws DronologyServiceException;

	void notifyValidationListeners(String uavid, ValidationEntry validationResult);

}
