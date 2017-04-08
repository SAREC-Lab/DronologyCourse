package edu.nd.dronology.core.gui_middleware;

import java.util.Map;

/**
 * Provides the sole access point for external and internal GUIs into the Dronology System
 * @author Jane
 *
 */
public class DronologyAccessPoint {
	DroneCollectionStatus droneCollection;
	Map<String,DroneStatus> drones;
	
	private static DronologyAccessPoint instance = null;
	protected DronologyAccessPoint() {
		droneCollection = DroneCollectionStatus.getInstance();
	}
	
	/**
	 * Return an instance of DronologyAccessPoint
	 * @return
	 */
	public static DronologyAccessPoint getInstance() {
	   if(instance == null) {
	      instance = new DronologyAccessPoint();
	   }
	   return instance;
	}
	
	// For now we return the MAP for the internal GUIs.
	// We can add additional functions getting serialized data for external GUIs.
	public Map<String,DroneStatus> getAllDroneStatus(){
		return droneCollection.getDrones();		
	}	
}
