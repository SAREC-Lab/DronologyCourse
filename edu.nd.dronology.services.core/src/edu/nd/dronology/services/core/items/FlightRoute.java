package edu.nd.dronology.services.core.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.nd.dronology.core.util.Coordinate;

public class FlightRoute implements IFlightRoute {

	private String name;
	private String id;
	private String category ="Default";
	private List<Coordinate> coordinates ;

	
	public FlightRoute(){
		id = UUID.randomUUID().toString();
		coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(0, 0, 0));
		name =id;
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
	 this.category  = category;
		
	}

	@Override
	public String getCategory() {
		return category;
	}
	
	@Override
	public List<Coordinate> getCoordinates() {
		return Collections.unmodifiableList(coordinates);
	}
	@Override
	public void addCoordinate(Coordinate coordinate) {
		coordinates .add(coordinate);
	}
	@Override
	public void removeCoordinate(Coordinate coordinate) {
		coordinates.remove(coordinate);
	}

}
