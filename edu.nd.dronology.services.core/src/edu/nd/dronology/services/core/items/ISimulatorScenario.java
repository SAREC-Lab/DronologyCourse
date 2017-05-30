package edu.nd.dronology.services.core.items;

import java.util.List;

public interface ISimulatorScenario extends IPersistableItem {
	
	String getDescription();

	void setDescription(String description);

	String getCategory();

	void setCategory(String category);
	
	
	List<AssignedDrone> getAssignedDrones();
	List<String> getAssignedFlightPaths();

	boolean removeAssignedDrone(String droneId);

	boolean removeAssignedPath(String pathId);

	boolean addAssignedPath(String pathId);

	boolean addAssignedDrone(String droneId);
	
}
