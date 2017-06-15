package edu.nd.dronology.core.util;

public class NVector {

	public static final double SEMI_MAJOR = 6378137.0;
	public static final double SEMI_MINOR = 6356752.31;// 4245;

	private double x;

	private double y;

	private double z;

	private double altitude;

	public NVector(double x, double y, double z, double altitude) {
		double m = Math.sqrt(x * x + y * y + z * z);
		this.x = x / m;
		this.y = y / m;
		this.z = z / m;
		this.altitude = altitude;
	}

	public NVector toNVector() {
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(altitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NVector))
			return false;
		NVector other = (NVector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("NVector(%f, %f, %f, altitude=%f)", x, y, z, altitude);
	}

	/**
	 * @return the distance above sea level in meters
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * @return the x component of the n-vector
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y component of the n-vector
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z component of the n-vector
	 */
	public double getZ() {
		return z;
	}

	public double distance(NVector other) {
		PVector pSelf = toPVector();
		PVector pOther = other.toPVector();
		double dx = pSelf.getX() - pOther.getX();
		double dy = pSelf.getY() - pOther.getY();
		double dz = pSelf.getZ() - pOther.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);

	}

	public PVector toPVector() {
		/*
		 * The formula this code is based on can be found in a journal article
		 * called: "A Non-singular Horizontal Position Representation" by
		 * Kenneth Gade. You can find it at https://goo.gl/iCqdCn (see equation
		 * 22 in appendix B)
		 * 
		 * Note: equation 22 is unconventional as it swaps the z component with
		 * x component. This code follows the more common convention and returns
		 * z and x to their proper place
		 */
		double a = SEMI_MAJOR;
		double b = SEMI_MINOR;
		double ab2 = (a * a) / (b * b);
		double f = b / Math.sqrt(z * z + ab2 * y * y + ab2 * x * x);
		double pz = f * z + altitude * z;
		double py = f * ab2 * y + altitude * y;
		double px = f * ab2 * x + altitude * x;
		return new PVector(px, py, pz);
	}
	
	public LlaCoordinate toLlaCoordinate() {
		double lat = Math.asin(this.getZ());
		double lon = Math.atan2(this.getY(), this.getX());
		return new LlaCoordinate(Math.toDegrees(lat), Math.toDegrees(lon), this.getAltitude());
	}

}
