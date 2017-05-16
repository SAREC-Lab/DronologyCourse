package edu.nd.dronology.core.drones_runtime.internal;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.simplesimulator.DroneVoltageSimulator;
import edu.nd.dronology.core.simplesimulator.FlightSimulator;
import edu.nd.dronology.core.util.Coordinates;
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
	private DroneVoltageSimulator voltageSimulator;
	private FlightSimulator flightSimulator;

	/**
	 * Constructs drone without specifying its current position. This will be used by the physical drone (later) where positioning status will be acquired from the drone.
	 * 
	 * @param drnName
	 */
	public VirtualDrone(String drnName) {
		super(drnName);
		voltageSimulator = new DroneVoltageSimulator();
		flightSimulator = new FlightSimulator(this);
	}

	@Override
	public void takeOff(int targetAltitude) throws FlightZoneException {
		voltageSimulator.startBatteryDrain();
		droneStatus.updateBatteryLevel(voltageSimulator.getVoltage()); // Need more incremental drain!!
		try {
			Thread.sleep(targetAltitude * 100); // Simulates attaining height. Later move to simulator.
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void flyTo(Coordinates targetCoordinates) {
		NullUtil.checkNull(targetCoordinates);
		flightSimulator.setFlightPath(currentPosition, targetCoordinates);
	}

	@Override
	public void land() throws FlightZoneException {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
		voltageSimulator.checkPoint();
		voltageSimulator.stopBatteryDrain();
	}

	@Override
	public double getBatteryStatus() {
		droneStatus.updateBatteryLevel(voltageSimulator.getVoltage());
		return voltageSimulator.getVoltage();
	}

	@Override
	public boolean move(int i) { // ALSO NEEDS THINKING ABOUT FOR non-VIRTUAL
		// System.out.println("Trying to move: " + droneName);
		getBatteryStatus();
		boolean moveStatus = flightSimulator.move(10);
		droneStatus.updateCoordinates(getLatitude(), getLongitude(), getAltitude());
		// DroneCollectionStatus.getInstance().testStatus();
		return moveStatus;
	}

	@Override
	public void setVoltageCheckPoint() {
		voltageSimulator.checkPoint();

	}

	@Override
	public boolean isDestinationReached(int distanceMovedPerTimeStep) {
		return flightSimulator.isDestinationReached(distanceMovedPerTimeStep);
	}

}
