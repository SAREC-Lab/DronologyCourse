package edu.nd.dronology.core.simulator.nvecsimulator;

import edu.nd.dronology.core.air_traffic_control.DistanceUtil;
import edu.nd.dronology.core.simulator.IFlightSimulator;
import edu.nd.dronology.core.simulator.simplesimulator.FlightSimulator;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.NVector;
import edu.nd.dronology.core.vehicle.internal.VirtualDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class Simulator implements IFlightSimulator {
	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightSimulator.class);
	private VirtualDrone drone;
	private NVector currentPosition;
	private NVector targetPosition;
	
	public Simulator(VirtualDrone drone) {
		this.drone = drone;
	}
	@Override
	public boolean isDestinationReached(double distanceMovedPerTimeStep) {
		return NvecInterpolator.move(currentPosition, targetPosition, distanceMovedPerTimeStep).equals(targetPosition);
	}
	@Override
	public boolean move(double i) {
		currentPosition = NvecInterpolator.move(currentPosition, targetPosition, i);
		drone.setCoordinates(currentPosition.toLlaCoordinate());
		LOGGER.trace("Remaining Dinstance: " + NVector.travelDistance(currentPosition, targetPosition));
		return currentPosition.equals(targetPosition);
	}

	@Override
	public void setFlightPath(LlaCoordinate currentPosition, LlaCoordinate targetCoordinates) {
		this.currentPosition = currentPosition.toNVector();
		this.targetPosition = targetCoordinates.toNVector();

	}
	@Override
	public void startBatteryDrain() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopBatteryDrain() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void checkPoint() {
		// TODO Auto-generated method stub

	}



}
