package edu.nd.dronology.services.core.info;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.util.NullUtil;

public class DroneInitializationInfo extends RemoteInfoObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3002533064742692033L;
	private String type;
	private Coordinate initialLocation;

	public DroneInitializationInfo(String id, String type, Coordinate initialLocation) {
		super(id, id);
		NullUtil.checkNull(type, initialLocation);
		this.type = type;
		this.initialLocation = initialLocation;

	}

	public String getType() {
		return type;
	}

	public Coordinate getInitialLocation() {
		return initialLocation;
	}

}
