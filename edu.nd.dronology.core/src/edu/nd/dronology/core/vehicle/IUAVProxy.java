package edu.nd.dronology.core.vehicle;

import java.util.Map;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public interface IUAVProxy {

	String getID();

	String getStatus();

	double getLongitude();

	double getLatitude();

	double getAltitude();

	double getBatteryLevel();

	double getVelocity();

	Map<String, String> getInfo();

	LlaCoordinate getCoordinates();

}