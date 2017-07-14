package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;

import edu.nd.dronology.core.util.LlaCoordinate;

public class UAVHandshakeMessage extends AbstractUAVMessage<Object> implements Serializable {

	private static final long serialVersionUID = 1502042637906425729L;
	public static final String MESSAGE_TYPE = "handshake";

	private LlaCoordinate home;

	public UAVHandshakeMessage(String messagetype, String uavid) {
		super(messagetype, uavid);
	}

	public void setType(String type) {
		this.type = type;

	}

	public LlaCoordinate getHome() {
		return home;
	}

	public void setHome(LlaCoordinate home) {
		this.home = home;
	}

}
