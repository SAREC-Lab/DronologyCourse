package edu.nd.dronology.gstation.python.connector.service;

import edu.nd.dronology.gstation.python.connector.GroundStationException;
import edu.nd.dronology.gstation.python.connector.GroundstationConnector;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
import edu.nd.dronology.services.core.api.IServiceInstance;

public interface IDroneConnectorServiceInstance extends IServiceInstance {

	void closeConnection(String groundstationid);

	void handleConnection(GroundstationConnector handler);

	void registerConnection(GroundstationConnector connector, ConnectionRequestMessage msg) throws GroundStationException;



}
