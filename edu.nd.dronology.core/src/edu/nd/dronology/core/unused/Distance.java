package edu.nd.dronology.core.unused;
//package edu.nd.dronology.core.util;
//
//import edu.nd.dronology.core.vehicle.IDrone;
//
///**
// * Computes distance between two drones.
// * Does not take into account earth's curviture because distances are expected to be small.
// * @author Jane
// *
// */
//class Distance {	
//	/**
//	 * 
//	 * @param drone1 Drone-1
//	 * @param drone2 Drone-2
//	 * @return distance computed using degrees latitude and longitude
//	 */
//	public static long getDistance(IDrone drone1, IDrone drone2){
//		long longDelta = Math.abs(drone1.getLongitude() - drone2.getLongitude());
//		long latDelta = Math.abs(drone1.getLatitude() - drone2.getLatitude());
//		return (long) Math.sqrt((Math.pow(longDelta, 2)) + (Math.pow(latDelta, 2)));
//	}
//}
