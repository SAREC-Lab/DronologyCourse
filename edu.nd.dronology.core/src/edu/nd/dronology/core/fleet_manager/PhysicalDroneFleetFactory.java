package edu.nd.dronology.core.fleet_manager;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.PhysicalDrone;
import edu.nd.dronology.core.drones_runtime.VirtualDrone;
import edu.nd.dronology.core.drones_runtime.iDrone;
import edu.nd.dronology.core.physical_environment.BaseManager;

/**
 * Creates a fleet of physical drones.
 * Not yet implemented.  
 * @author Jane
 *
 */
public class PhysicalDroneFleetFactory extends DroneFleetFactory{

	public PhysicalDroneFleetFactory(int fleetSize, BaseManager baseMgr) {
		super(fleetSize, baseMgr);
	}
	@Override
	protected ManagedDrone makeDroneAtUniqueBase(BaseManager baseManager){
		iDrone drone = new PhysicalDrone(createDroneID(uniqDroneID++));
		ManagedDrone managedDrone = new ManagedDrone(drone,createDroneID(uniqDroneID++));
		baseManager.assignDroneToBase(managedDrone); // Assigns drone.  Sets coordinates
		//drone.setBaseCoordinates(BaseCoordinates.getInstance().getNextBase());
		return managedDrone;
	}
}