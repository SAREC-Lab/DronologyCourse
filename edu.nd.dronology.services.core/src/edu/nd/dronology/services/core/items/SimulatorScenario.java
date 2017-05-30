package edu.nd.dronology.services.core.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SimulatorScenario implements ISimulatorScenario {

	private String name;
	private String id;
	private String category = "Default";
	private List<AssignedDrone> drones;
	private List<String> flightPaths;

	public SimulatorScenario() {
		id = UUID.randomUUID().toString();
		name = id;
		drones = new ArrayList<>();
		flightPaths = new ArrayList<>();
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
	public String getCategory() {
		return category;
	}

	@Override
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public List<AssignedDrone> getAssignedDrones() {
		return Collections.unmodifiableList(drones);
	}

	@Override
	public boolean addAssignedDrone(String droneId) {
		AssignedDrone toAdd = new AssignedDrone(droneId);
		if (drones.contains(toAdd)) {
			return false;
		}
		return drones.add(toAdd);
	}

	@Override
	public boolean addAssignedPath(String pathId) {
		if (flightPaths.contains(pathId)) {
			return false;
		}
		return flightPaths.add(pathId);
	}

	@Override
	public boolean removeAssignedDrone(String droneId) {
		AssignedDrone toRemove = new AssignedDrone(droneId);
		if (!drones.contains(toRemove)) {
			return false;
		}
		return drones.remove(toRemove);
	}

	@Override
	public boolean removeAssignedPath(String pathId) {
		if (!flightPaths.contains(pathId)) {
			return false;
		}
		return flightPaths.remove(pathId);
	}

	@Override
	public List<String> getAssignedFlightPaths() {
		return Collections.unmodifiableList(flightPaths);
	}

}
