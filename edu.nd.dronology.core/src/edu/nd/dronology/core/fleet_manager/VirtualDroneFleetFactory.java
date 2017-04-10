package edu.nd.dronology.core.fleet_manager;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.VirtualDrone;
import edu.nd.dronology.core.drones_runtime.iDrone;
import edu.nd.dronology.core.flight_manager.SoloDirector;
import edu.nd.dronology.core.home_bases.BaseManager;
import edu.nd.dronology.core.utilities.Coordinates;

public class VirtualDroneFleetFactory extends DroneFleetFactory{
	
	public VirtualDroneFleetFactory(int fleetSize, BaseManager baseMgr) {
		super(fleetSize, baseMgr);
	}
	
	@Override
	protected ManagedDrone makeDroneAtUniqueBase(BaseManager baseManager){
		iDrone drone = new VirtualDrone(createDroneID(uniqDroneID++));
		ManagedDrone managedDrone = new ManagedDrone(drone,createDroneID(uniqDroneID++));
		baseManager.assignDroneToBase(managedDrone); // Assigns drone.  Sets coordiantes.
		Coordinates currentPosition = managedDrone.getBaseCoordinates();
		drone.setCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude(), currentPosition.getAltitude());
		//drone.setBaseCoordinates(BaseCoordinates.getInstance().getNextBase());
		
		
		return managedDrone;
	}
	
}

