package edu.nd.dronology.core.air_traffic_control;

import java.util.ArrayList;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.utilities.PointDelta;


/**
 * Safety manager is responsible for monitoring drone positions to ensure minimum safety distance is not violated
 * @author jane
 *
 */
	public class DroneSeparationMonitor{
	ArrayList<ManagedDrone> drones = new ArrayList<ManagedDrone>();
	Long safetyZone;  // Set arbitrarily for now.
	
	/**
	 * Construct the safety manager.  SafetyZone size is hard coded at 10000 degree points.
	 */
	public DroneSeparationMonitor(){
		drones = new ArrayList<ManagedDrone>();
		safetyZone = (long) 10000; 
	}
	
	/** 
	 * Attach a drone to the safety manager.  Only attached drones are managed.
	 * @param drone
	 */
	public void attachDrone(ManagedDrone drone){
		drones.add(drone);
	}
	
	/**
	 * Detach a drone from the safety manager.  
	 * @param drone
	 */
	public void detachDrone(ManagedDrone drone){
		if(drones.contains(drone))
			drones.remove(drone);
	}
	
	/**
	 * Computes the distance between two drones
	 * @return distance remaining in degree points.
	 */
	public long getDistance(ManagedDrone D1, ManagedDrone D2){
		long longDelta = Math.abs(D1.getLongitude() - D2.getLongitude());
		long latDelta = Math.abs(D1.getLatitude() - D2.getLatitude());
		return (long) Math.sqrt((Math.pow(longDelta, 2)) + (Math.pow(latDelta, 2)));
	}
	
	/**
	 * Checks if a drone has permission to take off. A drone may NOT take off if any other drone currently attached to the
	 * safety manager is in the vicinity.
	 * @param managedDrone The drone which is requesting permission to take off.  This is checked against the complete list of drones.
	 * @return
	 */
	public boolean permittedToTakeOff(ManagedDrone managedDrone){
		for(ManagedDrone drone2: drones){
			if (!managedDrone.equals(drone2) && drone2.getFlightModeState().isFlying())
				if (getDistance(managedDrone,drone2) < safetyZone*1.5) // We require extra distance prior to takeoff
					return false;		
		}
		return true;
	}				
	
	/**
	 * Performs pairwise checks of safety violations between all drones currently attached to the safety manager.
	 */
	public void checkForViolations(){		
		for(ManagedDrone drone: drones){
			for(ManagedDrone drone2: drones) {
				if (!drone.equals(drone2) && drone.getFlightModeState().isFlying() && drone2.getFlightModeState().isFlying()){//!drone.isUnderSafetyDirectives() && !drone2.isUnderSafetyDirectives()){
					if (getDistance(drone,drone2) < safetyZone){
							double angle1 = PointDelta.computeAngle(drone.getCoordinates(),drone.getTargetCoordinates());
							double angle2 = PointDelta.computeAngle(drone2.getCoordinates(),drone2.getTargetCoordinates());
							//new Roundabout(drone, drone2);
							// PLACEHOLDER FOR new avoidance strategy.
						}
					}				
			}
		
		}
	}
}

