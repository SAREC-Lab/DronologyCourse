package edu.nd.dronology.ui.vaadin.utils;

public class CoordinateUtilities {
	public static Boolean isValidLatitude (double latitude) {
		return (latitude < 90 && latitude > -90);
	}
	public static Boolean isValidLongitude (double longitude) {
		return (longitude < 180 && longitude > -180);
	}
	
	public static String toSignedDegreesFormat (double latlon) {
		return String.format("%.6f", latlon);
	}
	public static String toSignedDegreesFormat (String latlon) {
		return String.format("%.6f", Double.parseDouble(latlon));
	}
}
