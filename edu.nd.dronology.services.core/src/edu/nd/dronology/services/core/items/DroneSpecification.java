package edu.nd.dronology.services.core.items;

import java.util.UUID;

public class DroneSpecification implements IDroneSpecification {

	private String id;
	private String name;
	private String description;
	private String type = "Default";

	public DroneSpecification() {
		id = UUID.randomUUID().toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
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
	public String getDescription() {
		return description;
	}

	@Override
	public void setType(String type) {
		this.type = type;

	}

	@Override
	public void setDescription(String description) {
		this.description = description;

	}

	@Override
	public String getType() {
		return type;
	}

}
