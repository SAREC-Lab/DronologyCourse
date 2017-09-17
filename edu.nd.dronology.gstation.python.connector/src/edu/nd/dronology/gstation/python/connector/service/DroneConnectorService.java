package edu.nd.dronology.gstation.python.connector.service;

import edu.nd.dronology.services.core.base.AbstractServerService;

public class DroneConnectorService extends AbstractServerService<IDroneConnectorServiceInstance> {

	private static volatile DroneConnectorService INSTANCE;

	protected DroneConnectorService() {
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneConnectorService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneConnectorService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneConnectorService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneConnectorServiceInstance initServiceInstance() {
		return new DroneConnectorServiceInstance();
	}

	

}
