package edu.nd.dronology.ui.cc.main.monitoring;

import edu.nd.dronology.core.util.LlaCoordinate;

public class WrappedCoordinate {

	private LlaCoordinate coordinate;

	public WrappedCoordinate(LlaCoordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public String toString() {
		return coordinate.toString();
	}

}
