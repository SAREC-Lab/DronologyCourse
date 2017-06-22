package edu.nd.dronology.core.util;

public class CoordinateConverter {

	public static Coordinate toCoordiate(LlaCoordinate location) {

		long lat = floatToCoordLong(location.getLatitude());
		long lon = floatToCoordLong(location.getLongitude());
		int alt = (int) Math.ceil(location.getAltitude());
		System.out.println("ALTITUDE: " + alt + " --> " + location.getAltitude());
		return new Coordinate(lat, lon, alt);
	}

	public static long floatToCoordLong(double coord) {
		double floatScaled = coord * 1000000.0;
		long longScaled = (long) floatScaled;
		return longScaled;
	}

}
