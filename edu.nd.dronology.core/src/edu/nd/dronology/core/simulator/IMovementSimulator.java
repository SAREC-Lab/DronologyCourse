package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.util.LlaCoordinate;

public interface IMovementSimulator {
	
	boolean move(double i);
	void setFlightPath(LlaCoordinate currentPosition, LlaCoordinate targetCoordinates);

	void checkPoint();

	boolean isDestinationReached(double distanceMovedPerTimeStep);
	

}
