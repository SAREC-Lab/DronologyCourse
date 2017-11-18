package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;
import java.util.Map;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public class ConnectionRequestMessage extends AbstractUAVMessage<Object> implements Serializable {

	private static final long serialVersionUID = 1502042637906425729L;
	public static final String MESSAGE_TYPE = "connect";


	public ConnectionRequestMessage(String groundstationId) {
		super(MESSAGE_TYPE, groundstationId);
	}

	public void setType(String type) {
		this.type = type;

	}


}
