package edu.nd.dronology.core;

import edu.nd.dronology.core.util.Coordinate;


@Discuss(discuss="needs renaming to hardware status update listener...")
public interface IDroneStatusUpdateListener {

	void updateCoordinates(Coordinate location);

	void updateDroneState(String status);

	void updateBatteryLevel(double batteryLevel);

	void updateVelocity(double velocity);

}
