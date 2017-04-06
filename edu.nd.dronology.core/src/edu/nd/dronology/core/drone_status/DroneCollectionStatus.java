package edu.nd.dronology.core.drone_status;

import java.util.HashMap;
import java.util.Map;

public class DroneCollectionStatus {
	Map<String,DroneStatus> drones;
	
	public DroneCollectionStatus(){
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
