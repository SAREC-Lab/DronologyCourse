package edu.nd.dronology.services.core.items;

import java.util.List;

import edu.nd.dronology.core.util.Coordinates;

public interface IFlightRoute extends IPersistableItem {
	String getDescription();

	void setDescription(String description);
	
	void setCategory(String category);
	
	
	String getCategory();

	void addCoordinate(Coordinates coordinate);

	List<Coordinates> getCoordinates();

	void removeCoordinate(Coordinates coordinate);
	
	
}
