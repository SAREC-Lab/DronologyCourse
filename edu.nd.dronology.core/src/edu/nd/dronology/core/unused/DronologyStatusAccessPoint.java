package edu.nd.dronology.core.unused;
//package edu.nd.dronology.core.gui_middleware;
//
//import java.util.Collections;
//import java.util.Map;
//
///**
// * Provides external access point for Dronology
// * 
// * @author Jane
// *
// */
//public class DronologyStatusAccessPoint {
//	private DroneCollectionStatus droneCollection;
//	private static volatile DronologyStatusAccessPoint INSTANCE = null;
//
//	protected DronologyStatusAccessPoint() {
//		droneCollection = DroneCollectionStatus.getInstance();
//	}
//
//	/**
//	 * Return an instance of DronologyStatusAccessPoint
//	 * 
//	 * @return instance
//	 */
//	public static DronologyStatusAccessPoint getInstance() {
//		if (INSTANCE == null) {
//			synchronized (DronologyStatusAccessPoint.class) {
//				if (INSTANCE == null) {
//					INSTANCE = new DronologyStatusAccessPoint();
//				}
//			}
//		}
//		return INSTANCE;
//	}
//
//	/**
//	 * Get access to current drone status. This may be replaced later - but for now, external applications get a pointer to the droneCollection. We need to prevent external agents from modifying this.
//	 * (Not done yet -- but we will modify this) Status is reported through a Map<String,DroneStatus> collection where the first argument is the ID of the drone and the second is its current status. See
//	 * DroneStatus API for available attributes for each drone.
//	 * 
//	 * @return DroneCollection
//	 */
//	public Map<String, DroneStatus> getAllDroneStatus() {
//		return Collections.unmodifiableMap(droneCollection.getDrones());
//	}
//}
