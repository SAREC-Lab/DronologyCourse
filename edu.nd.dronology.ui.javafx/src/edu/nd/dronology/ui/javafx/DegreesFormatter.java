package edu.nd.dronology.ui.javafx;

public class DegreesFormatter {

	public static String prettyFormatDegrees(Double degrees) {
		boolean minus = false;
		if (degrees < 0)
			minus = true;
		degrees = Math.abs(degrees);
		String strDegrees = degrees.toString();
		String firstPart = strDegrees.substring(0, 2);
		String endPart = strDegrees.substring(2);
		if (minus) {
			return "-" + firstPart + "." + endPart;
		} else {
			return firstPart + "." + endPart;
		}
	}
}
