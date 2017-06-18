package edu.nd.dronology.core.simulator.simplesimulator;

import edu.nd.dronology.core.simulator.IFlightSimulator;
import edu.nd.dronology.core.util.Coordinate;

public class SimpleSimulator implements IFlightSimulator{
	
	
	 FlightSimulator flightSimulator;
	 DroneVoltageSimulator voltageSimulator;
	 
	public SimpleSimulator(){
		flightSimulator=new FlightSimulator();
		voltageSimulator=new DroneVoltageSimulator();
	}

	@Override
	public void startBatteryDrain() {
		voltageSimulator.startBatteryDrain();
		
	}

	@Override
	public double getVoltage() {
		return voltageSimulator.getVoltage();
	}

	@Override
	public void setFlightPath(Coordinate currentPosition, Coordinate targetCoordinates) {
		flightSimulator.setFlightPath(currentPosition, targetCoordinates);
		
	}

	@Override
	public void checkPoint() {
		voltageSimulator.checkPoint();
		
	}

	@Override
	public boolean isDestinationReached(int distanceMovedPerTimeStep) {
		return flightSimulator.isDestinationReached(distanceMovedPerTimeStep);
	}

	@Override
	public void stopBatteryDrain() {
	voltageSimulator.startBatteryDrain();
		
	}

	@Override
	public boolean move(int step) {
	return flightSimulator.move(step);
	}

}
