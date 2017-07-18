package edu.nd.dronology.core.util;

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

}
