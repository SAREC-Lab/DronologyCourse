package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.simulator.simplesimulator.SimpleSimulator;

public class SimulatorFactory {
	
	public static IFlightSimulator getSimulator(){
		return new SimpleSimulator();
	}

}
