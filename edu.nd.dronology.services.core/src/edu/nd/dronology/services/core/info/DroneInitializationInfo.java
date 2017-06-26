package edu.nd.dronology.services.core.info;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.util.NullUtil;

public class DroneInitializationInfo extends RemoteInfoObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3002533064742692033L;
	private String type;
	private LlaCoordinate initialLocation;

	public DroneInitializationInfo(String id, String type, LlaCoordinate initialLocation) {
		super(id, id);
		NullUtil.checkNull(type, initialLocation);
		this.type = type;
		this.initialLocation = initialLocation;

	}

	public String getType() {
		return type;
	}

	public LlaCoordinate getInitialLocation() {
		return initialLocation;
	}

}
