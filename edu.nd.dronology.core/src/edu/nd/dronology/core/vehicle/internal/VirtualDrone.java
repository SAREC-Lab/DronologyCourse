package edu.nd.dronology.core.vehicle.internal;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.simulator.IFlightSimulator;
import edu.nd.dronology.core.simulator.SimulatorFactory;
import edu.nd.dronology.core.simulator.simplesimulator.DroneVoltageSimulator;
import edu.nd.dronology.core.simulator.simplesimulator.FlightSimulator;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.AbstractDrone;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Creates a virtual drone. iDrone interface needs refactoring badly!!!!
 * 
 * @author Jane Cleland-Huang
 * @version 0.01
 */
public class VirtualDrone extends AbstractDrone implements IDrone {

	private static final ILogger LOGGER = LoggerProvider.getLogger(VirtualDrone.class);
//	private DroneVoltageSimulator voltageSimulator;
//	private FlightSimulator flightSimulator;
	
	IFlightSimulator simulator;
	

	/**
	 * Constructs drone without specifying its current position. This will be used by the physical drone (later) where positioning status will be acquired from the drone.
	 * 
	 * @param drnName
	 */
	public VirtualDrone(String drnName) {
		super(drnName);
		simulator =SimulatorFactory.getSimulator();
//		voltageSimulator = new DroneVoltageSimulator();
//		flightSimulator = new FlightSimulator(this);
	}

	@Override
	public void takeOff(int targetAltitude) throws FlightZoneException {
		simulator.startBatteryDrain();
		droneStatus.updateBatteryLevel(simulator.getVoltage()); // Need more incremental drain!!
		try {
			Thread.sleep(targetAltitude * 100); // Simulates attaining height. Later move to simulator.
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void flyTo(Coordinate targetCoordinates) {
		NullUtil.checkNull(targetCoordinates);
	//	LOGGER.info("Flying to: "+ targetCoordinates.toString());
		simulator.setFlightPath(currentPosition, targetCoordinates);
	}

	@Override
	public void land() throws FlightZoneException {
		try {
			Thread.sleep(1500);
		simulator.checkPoint();
		simulator.stopBatteryDrain();
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}
		

	@Override
	public double getBatteryStatus() {
		droneStatus.updateBatteryLevel(simulator.getVoltage());
		return simulator.getVoltage();
	}

	@Override
	public boolean move(int i) { // ALSO NEEDS THINKING ABOUT FOR non-VIRTUAL
		// System.out.println("Trying to move: " + droneName);
		getBatteryStatus();
		boolean moveStatus = simulator.move(10);
		droneStatus.updateCoordinates(getLatitude(), getLongitude(), getAltitude());
		
		
		// DroneCollectionStatus.getInstance().testStatus();
		return moveStatus;
	}

	@Override
	public void setVoltageCheckPoint() {
		simulator.checkPoint();

	}

	@Override
	public boolean isDestinationReached(int distanceMovedPerTimeStep) {
		return simulator.isDestinationReached(distanceMovedPerTimeStep);
	}

	@Override
	public void setGroundSpeed(double speed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		// TODO Auto-generated method stub
		
	}

}
