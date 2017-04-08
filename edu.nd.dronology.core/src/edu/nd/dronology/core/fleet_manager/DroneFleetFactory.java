package edu.nd.dronology.core.fleet_manager;
import java.util.ArrayList;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.iDrone;
import edu.nd.dronology.core.gui_middleware.DroneCollectionStatus;
import edu.nd.dronology.core.gui_middleware.DroneStatus;
import edu.nd.dronology.core.physical_environment.BaseManager;
import edu.nd.dronology.core.physical_environment.DroneBase;

/**
 * Abstract factory class for drone fleet factory
 * @author Jane
 *
 */
public abstract class DroneFleetFactory {
	private final ArrayList<ManagedDrone> drones = new ArrayList<ManagedDrone>();
	int uniqDroneID = 0;
	BaseManager baseManager;
	
	/**
	 * Creates a fleet of size fleetSize.  Defers creation of the drone to a subclass for creating a fleet of
	 * virtual drones or physical drones.
	 * @param fleetSize
	 */
	public DroneFleetFactory(int fleetSize, BaseManager baseManager){
		for(int j=0;j<fleetSize;j++){
			ManagedDrone drone = makeDroneAtUniqueBase(baseManager); 
			drones.add(drone);
			drone.startThread();
			this.baseManager = baseManager;
		}
	}
	
	protected String createDroneID(int ID){
		return "DRN" + Integer.toString(ID);		
	}
	
	/**
	 * Returns list of drones
	 * @return array list of iDrones
	 */
	public ArrayList<ManagedDrone> getDrones(){
		return drones;
	}
	
	abstract protected ManagedDrone makeDroneAtUniqueBase(BaseManager baseManager);
	
}

