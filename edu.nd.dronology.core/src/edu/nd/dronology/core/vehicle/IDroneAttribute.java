package edu.nd.dronology.core.vehicle;

public interface IDroneAttribute<T> {
	
	
	public final String ATTRIBUTE_BATTERY_VOLTAGE = "batteryVoltage";
	
	public final String ATTRIBUTE_LOCATION = "location";

	String getKey();

	T getValue();

}
