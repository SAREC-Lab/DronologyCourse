/**
 * 
 */
package edu.nd.dronology.core.util;

/**
 * A terrestrial position defined by latitude, longitude, and altitude (LLA)
 * 
 * @author Michael Murphy
 *
 */
public class LlaCoordinate {
	private double latitude;
	private double longitude;
	private double altitude;

	/**
	 * A terrestrial position defined by latitude, longitude, and altitude
	 * (LLA).
	 * 
	 * @param latitude
	 *            the angle north of the equator in degrees (negative angles
	 *            define latitudes in the southern hemisphere). Must be a value
	 *            within this interval: -90 <= latitude <= 90
	 * @param longitude
	 *            the angle east of the prime meridian in degrees (negative
	 *            angles define longitudes in the western hemisphere). Must be a
	 *            value within this interval: -180 < longitude <= 180
	 * @param altitude
	 *            the distance above sea level in meters or more precisely the
	 *            distance above the surface of the WGS-84 reference ellipsoid.
	 * @throws IllegalArgumentException
	 *             when the latitude or longitude is outside the specified range
	 */
	public LlaCoordinate(double latitude, double longitude, double altitude) {
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LlaCoordinate))
			return false;
		LlaCoordinate other = (LlaCoordinate) obj;
		if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	/**
	 * The altitude
	 * 
	 * @return the distance above sea level or more precisely the distance above
	 *         the surface of the WGS-84 reference ellipsoid. For this project
	 *         we need this distance in meters
	 */
	public double getAltitude() {
		return altitude;
	}

	@Override
	public String toString() {
		return String.format("LlaCoordinate(%f, %f, %f)", latitude, longitude, altitude);
	}

	/**
	 * The latitude angle.
	 * 
	 * @return the angle north of the equator in degrees (negative angles define
	 *         latitudes in the southern hemisphere).
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * The longitude angle
	 * 
	 * @return the angle east of the prime meridian in degrees (negative angles
	 *         define longitudes in the western hemisphere)
	 */
	public double getLongitude() {
		return longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(altitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	public NVector toNVector() {
		/*
		 * The formula this code is based on can be found in a journal article
		 * called: "A Non-singular Horizontal Position Representation" by
		 * Kenneth Gade. You can find it at https://goo.gl/iCqdCn (see equation
		 * 3 in 5.2.1.)
		 * 
		 * Note: equation 3 is unconventional as it swaps the z component with
		 * x component. This code follows the more common convention and returns
		 * z and x to their proper place
		 */
		double lat = Math.toRadians(latitude);
		double lon = Math.toRadians(longitude);
		double cosLat = Math.cos(lat);
		double x = Math.cos(lon) * cosLat;
		double y = Math.sin(lon) * cosLat;
		double z = Math.sin(lat);
		return new NVector(x, y, z, altitude);
	}
	
	public PVector toPVector() {
		return this.toNVector().toPVector();
	}
	
	private void setAltitude(double altitude) {
		// on wrong side of the earth...
		// if (altitude < -6378137) {
		// throw new IllegalArgumentException("Invalid altitude");
		// }
		this.altitude = altitude;
	}

	private void setLatitude(double latitude) {
		if (Math.abs(latitude) > 90) {
			throw new IllegalArgumentException("Invalid latitude");
		}
		this.latitude = latitude;
	}

	private void setLongitude(double longitude) {
		if (longitude > 180 || longitude <= -180) {
			throw new IllegalArgumentException("Invalid longitude");
		}
		this.longitude = longitude;
	}

}
