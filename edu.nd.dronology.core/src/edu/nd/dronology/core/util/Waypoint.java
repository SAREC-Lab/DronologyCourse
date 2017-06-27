package edu.nd.dronology.core.util;

import java.io.Serializable;

import edu.nd.dronology.util.NullUtil;

public class Waypoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5855436372355880741L;

	private final LlaCoordinate coordinate;
	private boolean destinationReached = false;

	public LlaCoordinate getCoordinate() {
		return coordinate;
	}

	public Waypoint(LlaCoordinate coordinate) {
		super();
		NullUtil.checkNull(coordinate);
		this.coordinate = coordinate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Waypoint))
			return false;
		Waypoint other = (Waypoint) obj;
		return coordinate.equals(other.getCoordinate());
	}

	public boolean isReached() {
		return destinationReached;
	}

	@Override
	public int hashCode() {
		return coordinate.hashCode();
	}

	public void reached(boolean reached) {
		this.destinationReached = reached;

	}

}
