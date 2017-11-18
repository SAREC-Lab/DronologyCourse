package edu.nd.dronology.core.util;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.util.NullUtil;

public class DistanceUtil {

	/**
	 * Calculate distance between two points in latitude and longitude taking into account height difference. If you are not interested in height difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2 End altitude in meters
	 * 
	 * @returns Distance in Meters
	 */
	public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

		// lat1 = lat1/1000000;
		// lat2 = lat2/1000000;
		// lon1 = lon1/1000000;
		// lon2 = lon2/1000000;
		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

	public static double distance(LlaCoordinate currentPosition, LlaCoordinate targetPosition) {
		return distance(currentPosition.getLatitude(), targetPosition.getLatitude(), currentPosition.getLongitude(),
				targetPosition.getLongitude(), 0, 0);
	}

	public static double calculateTotalDistance(LlaCoordinate... coordinates) {
		NullUtil.checkArrayNull(coordinates);
		if (coordinates.length < 2) {
			throw new IllegalArgumentException("At least 2 coordinates are required to calculate the distance between them!");
		}
		double totaldistance = 0;
		for (int i = 0; i < coordinates.length; i++) {
			LlaCoordinate current = coordinates[i];
			if (coordinates.length < (i + 2)) {
				break;
			}
			LlaCoordinate next = coordinates[i + 1];
			totaldistance = distance(current, next);
		}
		return totaldistance;
	}

}
