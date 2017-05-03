package edu.nd.dronology.core.drones_runtime;

import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.FlightZoneException;

/**
 * Placeholder for physical drone code.
 * 
 * @author Jane
 *
 */
public class PhysicalDrone extends AbstractDrone implements IDrone, Runnable {

	public PhysicalDrone(String drnName) {
		super(drnName);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flyTo(Coordinates targetCoordinates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void land() throws FlightZoneException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeOff(int altitude) throws FlightZoneException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBatteryStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean move(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVoltageCheckPoint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDestinationReached(int distanceMovedPerTimeStep) {
		// TODO Auto-generated method stub
		return false;
	}

}
