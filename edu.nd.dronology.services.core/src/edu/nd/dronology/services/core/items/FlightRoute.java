package edu.nd.dronology.services.core.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.util.LlaCoordinate;

public class FlightRoute implements IFlightRoute {

	private String name;
	private String id;
	private String category = "Default";
	private LinkedList<LlaCoordinate> coordinates;

	public FlightRoute() {
		id = UUID.randomUUID().toString();
		coordinates = new LinkedList<>();
		coordinates.add(new LlaCoordinate(0, 0, 0));
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
	public List<LlaCoordinate> getCoordinates() {
		return Collections.unmodifiableList(coordinates);
	}

	@Override
	public void addCoordinate(LlaCoordinate coordinate) {
		coordinates.add(coordinate);
	}

	@Discuss(discuss = "this corrently breakes if you add 2 identical coordinates...")
	@Override
	public int removeCoordinate(LlaCoordinate coordinate) {
		int index = coordinates.indexOf(coordinate);
		if (index != -1) {
			coordinates.remove(coordinate);
		}
		return index;
	}

	@Override
	public void addCoordinate(LlaCoordinate coordinate, int index) {
		coordinates.add(index, coordinate);
	}
}
