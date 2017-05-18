package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.util.Coordinate;

public class FlightPlanInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 256865471183839829L;
	private String droneId;
	private List<Coordinate> waypoints;
	private Coordinate startLocation;
	private long startTime;
	private long endTime;
	
	public String getDroneId() {
		return droneId;
	}

	public List<Coordinate> getWaypoints() {
		return waypoints;
	}

	public Coordinate getStartLocation() {
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

	public void setWaypoints(List<Coordinate> waypoints) {
		this.waypoints = new ArrayList(waypoints);
		
	}

	public void setStartLocation(Coordinate startLocation) {
	this.startLocation = startLocation;
		
	}

	public void setStartTime(long startTime) {
		this.startTime=startTime;
		
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
		
	}
	


}
