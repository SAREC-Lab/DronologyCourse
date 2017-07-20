package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;
import java.util.Map;

import edu.nd.dronology.core.util.LlaCoordinate;

public class UAVHandshakeMessage extends AbstractUAVMessage<Object> implements Serializable {

	private static final long serialVersionUID = 1502042637906425729L;
	public static final String MESSAGE_TYPE = "handshake";
	public static final String HOME = "home";

	private String safetyCase;

	public UAVHandshakeMessage(String messagetype, String uavid) {
		super(messagetype, uavid);
	}

	public void setType(String type) {
		this.type = type;

	}

	public LlaCoordinate getHome() {
		if (data.get(HOME) instanceof LlaCoordinate) {
			return (LlaCoordinate) data.get(HOME);
		}
		Map<String, Double> homeMap = (Map<String, Double>) data.get(HOME);
		data.put(HOME, new LlaCoordinate(homeMap.get("x"), homeMap.get("y"), homeMap.get("z")));
		return (LlaCoordinate) data.get(HOME);
	}

	public String getSafetyCase() {
		return safetyCase;
	}

	public void setSafetyCase(String safetyCase) {
		this.safetyCase = safetyCase;
	}

	public void setHome(LlaCoordinate coordinate) {
		data.put(HOME, coordinate);

	}

}
