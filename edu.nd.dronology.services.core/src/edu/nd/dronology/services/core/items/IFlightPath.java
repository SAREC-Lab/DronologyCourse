package edu.nd.dronology.services.core.items;

import java.util.List;

import edu.nd.dronology.core.utilities.Coordinates;

public interface IFlightPath extends IPersistableItem {
	String getDescription();

	void setDescription(String description);
	
	
	void setCategory(String category);
	

	
	String getCategory();

	void addCoordinate(Coordinates coordinate);

	List<Coordinates> getCoordinates();

	void rempoveCoordinate(Coordinates coordinate);
	
	
}
