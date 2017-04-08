package edu.nd.dronology.core.gui_middleware;

import java.util.HashMap;
import java.util.Map;

//Singleton class
public class DroneCollectionStatus {
	Map<String,DroneStatus> drones;
	
	private static DroneCollectionStatus instance = null;
	  
	public static DroneCollectionStatus getInstance() {
	  if(instance == null) {
	      instance = new DroneCollectionStatus();
	  }
	  return instance;
    }
	
	public void testStatus(){
		System.out.println("Print current drone dump");
		for(DroneStatus droneStatus: drones.values()){
			System.out.println(droneStatus.toString());
		}
	}
	
	public Map<String,DroneStatus> getDrones(){
		return drones;
	}

	protected DroneCollectionStatus(){
		drones = new HashMap<String,DroneStatus>();
	}
	
	public void addDrone(DroneStatus drone){
		drones.put(drone.ID, drone);
	}
	
	public void removeDrone(String droneID){
		if(drones.containsKey(droneID))
			drones.remove(droneID);
	}
	
	public void removeDrone(DroneStatus drone){
		if(drones.containsKey(drone.ID))
			drones.remove(drone.ID);
	}
	
	public DroneStatus getDrone(String droneID){
		if (drones.containsKey(droneID))
			return drones.get(droneID);
		else
			return null;
	}	
}
