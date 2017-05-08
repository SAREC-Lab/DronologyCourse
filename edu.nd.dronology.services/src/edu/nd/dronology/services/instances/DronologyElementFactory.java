package edu.nd.dronology.services.instances;

import edu.nd.dronology.services.core.items.DroneEquipment;
import edu.nd.dronology.services.core.items.FlightPath;
import edu.nd.dronology.services.core.items.IDroneEquipment;
import edu.nd.dronology.services.core.items.IFlightPath;

public class DronologyElementFactory {

	public static IFlightPath createNewFlightPath() {
		return new FlightPath();
	}

	public static IDroneEquipment createNewDroneEqiupment() {
		return new DroneEquipment();
	}

}
