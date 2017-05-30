package edu.nd.dronology.services.instances;

import edu.nd.dronology.services.core.items.DroneSpecification;
import edu.nd.dronology.services.core.items.FlightRoute;
import edu.nd.dronology.services.core.items.IDroneSpecification;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.items.SimulatorScenario;

public class DronologyElementFactory {

	public static IFlightRoute createNewFlightPath() {
		return new FlightRoute();
	}

	public static IDroneSpecification createNewDroneEqiupment() {
		return new DroneSpecification();
	}

	public static ISimulatorScenario createNewSimulatorScenario() {
		return new SimulatorScenario();
	}

}
