package edu.nd.dronology.ui.cc.main.monitoring;

import edu.nd.dronology.core.util.Coordinate;

public class WrappedCoordinate {

	private Coordinate coordinate;

	public WrappedCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public String toString() {
		return coordinate.toString();
	}

}
