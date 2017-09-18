package edu.nd.dronology.core.vehicle.commands;

public interface IDroneCommand {

	public static final String ATTRIBUTE_FREQUENCY = "frequency";
	public static final String ATTRIBUTE_ALTITUDE = "altitude";
	public static final String ATTRIBUTE_SPEED = "speed";
	public static final String ATTRIBUTE_MODE = "mode";

	public static final String ATTRIBUTE_X = "x";
	public static final String ATTRIBUTE_Y = "y";
	public static final String ATTRIBUTE_Z = "z";

	
	public static final String ATTRIBUTE_SUCCESS = "success";
	
	String toJsonString();

	void timestamp();

}
