package edu.nd.dronology.monitoring.service;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.monitoring.monitoring.UAVValidationInformation;
import edu.nd.dronology.monitoring.validation.ValidationResult;
import edu.nd.dronology.services.core.remote.IRemoteableService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IDroneSafetyRemoteService extends IRemoteableService {

	ValidationResult validateUAVSafetyCase(String uavid, String safetycase)
			throws DronologyServiceException, RemoteException;

	void addValidationListener(IMonitoringValidationListener listener)
			throws DronologyServiceException, RemoteException;

	Collection<UAVValidationInformation> getValidationInfo() throws RemoteException;

	UAVValidationInformation getValidationInfo(String uavId) throws RemoteException, DronologyServiceException;

	void removeValidationListener(IMonitoringValidationListener listener)
			throws DronologyServiceException, RemoteException;

}
