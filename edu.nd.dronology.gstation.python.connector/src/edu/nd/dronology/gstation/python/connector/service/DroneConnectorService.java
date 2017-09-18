package edu.nd.dronology.gstation.python.connector.service;

import edu.nd.dronology.gstation.python.connector.GroundStationException;
import edu.nd.dronology.gstation.python.connector.GroundstationConnector;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
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

	public void closeConnection(String groundstationid) {
		serviceInstance.closeConnection(groundstationid);

	}

	public void handleConnection(GroundstationConnector connector) {
		serviceInstance.handleConnection(connector);
		
	}

	public void registerConnection(GroundstationConnector connector, ConnectionRequestMessage msg) throws GroundStationException{
		serviceInstance.registerConnection(connector,msg);
		
	}

}
