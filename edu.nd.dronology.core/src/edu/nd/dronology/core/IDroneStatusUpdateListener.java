package edu.nd.dronology.core;

import edu.nd.dronology.core.util.Coordinate;

public interface IDroneStatusUpdateListener {

	void updateCoordinates(Coordinate location);

	void updateDroneState(String status);

	void updateBatteryLevel(double batteryLevel);

}
