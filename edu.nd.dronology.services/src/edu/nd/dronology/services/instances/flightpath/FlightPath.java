package edu.nd.dronology.services.instances.flightpath;

import java.util.UUID;

public class FlightPath implements IFlightPath {

	private String name;
	private String id;

	
	public FlightPath(){
		id = UUID.randomUUID().toString();
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

}
