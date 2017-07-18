package edu.nd.dronology.core.util;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * A terrestrial position in WGS-84. this class implements utility methods for
 * subclasses.
 * 
 * @author Michael Murphy
 *
 */
public abstract class AbstractPosition {

	/**
	 * Convert this position if necessary to an NVector.
	 * 
	 * @return a terrestrial position defined an NVector and an altitude.
	 */
	public abstract NVector toNVector();

	/**
	 * Convert this position if necessary to a PVector.
	 * 
	 * @return a terrestrial position defined an X, Y and Z coordinate.
	 */
	public abstract PVector toPVector();

	/**
	 * Convert this position if necessary to an LlaCoordinate.
	 * 
	 * @return a terrestrial position defined by a latitude, longitude, and
	 *         altitude.
	 */
	public abstract LlaCoordinate toLlaCoordinate();

	/**
	 * Calculates the distance from this position to other position. This is the
	 * distance a laser bean would travel to reach the other point.
	 * 
	 * @param other
	 *            the position of the point to calculate the distance to.
	 * @return the distance to the other position in meters
	 */
	public double distance(AbstractPosition other) {
		return NVector.laserDistance(this.toNVector(), other.toNVector());
	}

	/**
	 * Calculates the distance a drone would realistically travel to get from
	 * this position to the other position.
	 * 
	 * Warning! this code slow, the time it takes to run is proportional to the
	 * distance from this to other.
	 * 
	 * @param other
	 *            the position of the point to calculate the distance to.
	 * @return the distance a drone would need to travel to get to the other
	 *         position in meters
	 */
	public double travelDistance(AbstractPosition other) {
		return NVector.travelDistance(this.toNVector(), other.toNVector());
	}
	
	/**
	 * Calculate the rotation matrix representation of this position. This rotation
	 * matrix can take displacement vectors in ECEF coordinates and rotate them into NED
	 * coordinates at this position.
	 * 
	 * This position cannot be at the poles as north and east directions don't make
	 * sense there.
	 * 
	 * This is the matrix inverse of equation 11 in <a href=
	 * "http://www.navlab.net/Publications/A_Nonsingular_Horizontal_Position_Representation.pdf">this
	 * paper.</a>
	 * 
	 * 
	 * @return a 3x3 rotation matrix where the rows can be interpreted as vectors
	 *         pointing in the north, east and down directions respectively.
	 */
	public RealMatrix toRotMatrix() {
		NVector n = this.toNVector();
		Vector3D nvec = new Vector3D(n.getX(), n.getY(), n.getZ());
		Vector3D z = new Vector3D(0, 0, 1);
		Vector3D east = z.crossProduct(nvec).normalize();
		Vector3D north = nvec.crossProduct(east).normalize();
		Vector3D down = nvec.negate();
		double[][] data = { north.toArray(), east.toArray(), down.toArray() };
		return new Array2DRowRealMatrix(data);
	}

}
