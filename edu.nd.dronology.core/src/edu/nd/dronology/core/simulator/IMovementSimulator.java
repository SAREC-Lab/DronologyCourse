package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.util.Coordinate;

public interface IMovementSimulator {
	
	boolean move(int i);
	void setFlightPath(Coordinate currentPosition, Coordinate targetCoordinates);

	void checkPoint();

	boolean isDestinationReached(int distanceMovedPerTimeStep);

}
