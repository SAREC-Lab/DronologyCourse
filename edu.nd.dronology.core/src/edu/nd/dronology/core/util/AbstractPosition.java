package edu.nd.dronology.core.util;

public abstract class AbstractPosition {
	public abstract NVector toNVector();
	public abstract PVector toPVector();
	public abstract LlaCoordinate toLlaCoordinate();
	
	public double distance(AbstractPosition other) {
		return NVector.laserDistance(this.toNVector(), other.toNVector());
	}
	
	public double travelDistance(AbstractPosition other) {
		return NVector.travelDistance(this.toNVector(), other.toNVector());
	}

}
