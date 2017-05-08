package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.utilities.Coordinates;

public class FlightPlanInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 256865471183839829L;
	private String droneId;
	private List<Coordinates> waypoints;
	private Coordinates startLocation;
	private long startTime;
	private long endTime;
	
	public String getDroneId() {
		return droneId;
	}

	public List<Coordinates> getWaypoints() {
		return waypoints;
	}

	public Coordinates getStartLocation() {
		return startLocation;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}



	public FlightPlanInfo(String name, String id) {
		super(name, id);
	}

	public void setDroneId(String droneId) {
	this.droneId = droneId;
		
	}

	public void setWaypoints(List<Coordinates> waypoints) {
		this.waypoints = new ArrayList(waypoints);
		
	}

	public void setStartLocation(Coordinates startLocation) {
	this.startLocation = startLocation;
		
	}

	public void setStartTime(long startTime) {
		this.startTime=startTime;
		
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
		
	}
	


}
