package edu.nd.dronology.services.core.info;

import edu.nd.dronology.core.util.Coordinates;
import edu.nd.dronology.util.NullUtil;

public class DroneInitializationInfo extends RemoteInfoObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3002533064742692033L;
	private String type;
	private Coordinates initialLocation;

	public DroneInitializationInfo(String id, String type, Coordinates initialLocation) {
		super(id, id);
		NullUtil.checkNull(type, initialLocation);
		this.type = type;
		this.initialLocation = initialLocation;

	}

	public String getType() {
		return type;
	}

	public Coordinates getInitialLocation() {
		return initialLocation;
	}

}
