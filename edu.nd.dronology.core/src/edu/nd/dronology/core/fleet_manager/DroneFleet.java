package edu.nd.dronology.core.fleet_manager;

import java.util.ArrayList;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;

/**
 * Holds a fleet of virtual or physical drones.
 * @author Jane
 *
 */
public class DroneFleet {
	ArrayList<ManagedDrone> availableDrones;
	ArrayList<ManagedDrone> busyDrones;
	private static DroneFleet instance = null;
	
	public static DroneFleet getInstance() {
		if(instance == null) {
			instance = new DroneFleet();
		}
		return instance;
	}
	
	/**
	 * Specifies whether virtual or physical drones will be created according to the previously specified
	 * runtime drone type.  (See RuntimeDroneTypes.java)
	 */
	protected DroneFleet(){
		if (RuntimeDroneTypes.getInstance().isSimulation())
			availableDrones = VirtualDroneFleetFactory.getInstance().getDrones(); 
		else
			availableDrones = PhysicalDroneFleetFactory.getInstance().getDrones();
		
		busyDrones = new ArrayList<ManagedDrone>();
	}
	
	/**
	 * Checks for an available drone from the fleet.
	 * @return true if drone is available, false if it is not.
	 */
	public boolean hasAvailableDrone(){
		System.out.println("Drones available: " + availableDrones.size());
		if (availableDrones.size() > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the next available drone.  Currently uses FIFO to recycle drones.
	 * @return
	 */
	public ManagedDrone getAvailableDrone(){
		if (!availableDrones.isEmpty()){
			ManagedDrone drone = availableDrones.get(0);
			availableDrones.remove(drone);
			busyDrones.add(drone);
			return drone;
		}	
		else
			return null;
	}
	
	public void setAvailableDrone(ManagedDrone managedDrone){
		availableDrones.add(managedDrone);
	}
	
		
	/**
	 * When a drone completes a mission, returns it to the pool of available drones.
	 * @param drone
	 */
	public void returnDroneToAvailablePool(ManagedDrone drone){
		if (busyDrones.contains(drone)){
			busyDrones.remove(drone);
			availableDrones.add(drone);
		}
	}
	
}

