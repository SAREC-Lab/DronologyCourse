package edu.nd.dronology.core;

import edu.nd.dronology.core.util.LlaCoordinate;


public interface IUAVPropertyUpdateNotifier {

	void updateCoordinates(LlaCoordinate location);

	void updateDroneState(String status);

	void updateBatteryLevel(double batteryLevel);

	void updateVelocity(double velocity);

}
