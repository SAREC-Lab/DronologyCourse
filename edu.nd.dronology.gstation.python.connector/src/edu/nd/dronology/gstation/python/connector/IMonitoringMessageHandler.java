package edu.nd.dronology.gstation.python.connector;

import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;

public interface IMonitoringMessageHandler {

	void notifyMonitoringMessage(UAVMonitoringMessage message);

	void notifyStatusMessage(UAVStateMessage message);

}
