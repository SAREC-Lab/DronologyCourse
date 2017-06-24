package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.simulator.simplesimulator.SimpleSimulator;
import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

public class SimulatorFactory {
	
	public static IFlightSimulator getSimulator(VirtualDrone drone){
		return new SimpleSimulator(drone);
	}

}
