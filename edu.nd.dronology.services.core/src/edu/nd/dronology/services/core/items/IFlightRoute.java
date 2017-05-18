package edu.nd.dronology.services.core.items;

import java.util.List;

import edu.nd.dronology.core.util.Coordinate;

public interface IFlightRoute extends IPersistableItem {
	String getDescription();

	void setDescription(String description);
	
	void setCategory(String category);
	
	
	String getCategory();

	void addCoordinate(Coordinate coordinate);

	List<Coordinate> getCoordinates();

	void removeCoordinate(Coordinate coordinate);
	
	
}
