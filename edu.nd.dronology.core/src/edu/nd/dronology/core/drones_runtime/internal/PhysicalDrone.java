package edu.nd.dronology.core.drones_runtime.internal;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.Coordinates;
import edu.nd.dronology.core.vehicle.AbstractDrone;
import edu.nd.dronology.core.vehicle.IDrone;

/**
 * Placeholder for physical drone code.
 * 
 * @author Jane
 *
 */
public class PhysicalDrone extends AbstractDrone implements IDrone, Runnable {

	protected PhysicalDrone(String drnName) {
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
