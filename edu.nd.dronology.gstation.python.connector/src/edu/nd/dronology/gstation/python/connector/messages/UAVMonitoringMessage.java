package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;

public class UAVMonitoringMessage extends AbstractUAVMessage<Object> implements Serializable {

	private static final long serialVersionUID = 1502042637906425729L;
	public static final String MESSAGE_TYPE = "monitoring";

	public UAVMonitoringMessage(String messagetype, String uavid) {
		super(messagetype, uavid);
	}

	public void setType(String type) {
		this.type = type;

	}
	

	
	
}
