package edu.nd.dronology.gstation.python.connector;

import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;

public interface IMonitoringMessageHandler {

	void notify(UAVMonitoringMessage message);

}
