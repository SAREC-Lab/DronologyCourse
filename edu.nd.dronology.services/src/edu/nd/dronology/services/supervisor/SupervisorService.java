package edu.nd.dronology.services.supervisor;

import java.util.Map;

import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class SupervisorService extends AbstractServerService<ISupervisorServiceInstance> {

	private static SupervisorService INSTANCE;

	protected SupervisorService() {
		super();
	}

	/**
	 * @return The singleton SupervisorService instance
	 */
	public static SupervisorService getInstance() {
		if (INSTANCE == null) {
			synchronized (SupervisorService.class) {
				INSTANCE = new SupervisorService();
			}
		}
		return INSTANCE;
	}

	@Override
	protected ISupervisorServiceInstance initServiceInstance() {
		return new SupervisorServiceInstance();
	}

	public void shutdownServer() {
		serviceInstance.shutdownServer();
	}

	public void restartAllServices() {
		serviceInstance.restartAllServices();
	}

	public String getFlightPathLocation() {
		return serviceInstance.getFlightPathLocation();
	}
	
	
	public String getSimScenarioLocation() {
		return serviceInstance.getSimScenarioLocation();
	}

	public String getDroneEquipmentLocation() {
		return serviceInstance.getDroneEquipmentLocation();
	}
	

	public String getWorkspaceLocation() {
		return serviceInstance.getWorkspaceLocation();

	}

	public Map<String, String> getGlobalProperties() {
		return serviceInstance.getGlobalProperties();
	}

	public boolean importItem(String fileName, byte[] byteArray, boolean overwrite) throws DronologyServiceException {
		return serviceInstance.importItem(fileName, byteArray, overwrite);
	}

	
	
	

	public void restart(String serviceClass) throws DronologyServiceException {
		getService(serviceClass).restartService();

	}



}
