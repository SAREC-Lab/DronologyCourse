package edu.nd.dronology.core.util;

public abstract class AbstractPosition {
	public abstract NVector toNVector();
	public abstract PVector toPVector();
	public abstract LlaCoordinate toLlaCoordinate();
	
	public double distance(AbstractPosition other) {
		return this.toNVector().distance(other.toNVector());
	}
	
	public double travelDistance(AbstractPosition other) {
		return NVector.travelDistance(this.toNVector(), other.toNVector());
	}

}
