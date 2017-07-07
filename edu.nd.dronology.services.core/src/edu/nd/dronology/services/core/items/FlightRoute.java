package edu.nd.dronology.services.core.items;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;

public class FlightRoute implements IFlightRoute {

	private String name;
	private String id;
	private String category = "Default";
	private LinkedList<Waypoint> waypoints;
	private double takeoffaltitude = 5;

	public double setTakeoffAltitude() {
		return takeoffaltitude;
	}

	public FlightRoute() {
		id = UUID.randomUUID().toString();
		waypoints = new LinkedList<>();
		waypoints.add(new Waypoint(new LlaCoordinate(0, 0, 0)));
		name = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCategory(String category) {
		this.category = category;

	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public List<Waypoint> getWaypoints() {
		return Collections.unmodifiableList(waypoints);
	}

	@Override
	public void addWaypoint(Waypoint waypoint) {
		waypoints.add(waypoint);
	}

	@Discuss(discuss = "this currently breaks if you add 2 identical coordinates...")
	@Override
	public int removeWaypoint(Waypoint coordinate) {
		int index = waypoints.indexOf(coordinate);
		if (index != -1) {
			waypoints.remove(coordinate);
		}
		return index;
	}

	@Override
	public void addWaypoint(Waypoint waypoint, int index) {
		waypoints.add(index, waypoint);
	}

	@Override
	public Waypoint removeWaypoint(int index) {
		return waypoints.remove(index);

	}

	@Override
	public void setTakeoffAltitude(double altitude) {
		if (altitude <= 0) {
			throw new IllegalArgumentException("Takeoff altitude must not be a postive number > 0");
		}
		this.takeoffaltitude = altitude;
	}
}
