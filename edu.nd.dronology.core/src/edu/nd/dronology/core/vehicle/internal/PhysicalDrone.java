package edu.nd.dronology.core.vehicle.internal;

import java.util.Random;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.vehicle.AbstractDrone;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.AbstractDroneCommand;
import edu.nd.dronology.core.vehicle.commands.GoToCommand;
import edu.nd.dronology.core.vehicle.commands.SetGroundSpeedCommand;
import edu.nd.dronology.core.vehicle.commands.SetModeCommand;
import edu.nd.dronology.core.vehicle.commands.SetMonitoringFrequencyCommand;
import edu.nd.dronology.core.vehicle.commands.SetVelocityCommand;
import edu.nd.dronology.core.vehicle.commands.TakeoffCommand;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Placeholder for physical drone code.
 * 
 * @author Jane
 *
 */
public class PhysicalDrone extends AbstractDrone implements IDrone, IUAVPropertyUpdateNotifier {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PhysicalDrone.class);

	private IDroneCommandHandler baseStation;
	private String droneID;
	private LlaCoordinate currentTarget;

	public PhysicalDrone(String drnName, IDroneCommandHandler baseStation) {
		super(drnName);
		this.baseStation = baseStation;
		currentTarget = new LlaCoordinate(0, 0, 0);
		try {
			droneID = drnName;
			baseStation.setStatusCallbackNotifier(droneID, this);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public double getLatitude() {
		return getCoordinates().getLatitude();
	}

	@Override
	public double getLongitude() {
		return getCoordinates().getLongitude();
	}

	@Override
	public double getAltitude() {
		return getCoordinates().getAltitude();
	}

	Random r = new Random();

	@Override
	public void flyTo(LlaCoordinate targetCoordinates, Double speed) {
		if (targetCoordinates != currentTarget) {
			// TODO: add some time limit for refreshing the information in case it didn't
			// properly get sent
			currentTarget = targetCoordinates;
			try {
				baseStation.sendCommand(new GoToCommand(droneID, targetCoordinates));
				if (speed != null && speed > 0) {
					baseStation.sendCommand(new SetGroundSpeedCommand(droneID, speed));
				}
			} catch (DroneException e) {
				LOGGER.error(e);
			}
		}

	}

	@Override
	public LlaCoordinate getCoordinates() {
		return droneStatus.getCoordinates();
	}

	@Override
	public void land() throws FlightZoneException {
		try {
			baseStation.sendCommand(new SetModeCommand(droneID, SetModeCommand.MODE_LAND));
		} catch (DroneException e) {
			throw new FlightZoneException(e);
		}
	}

	@Override
	public void takeOff(double altitude) throws FlightZoneException {
		try {
			baseStation.sendCommand(new TakeoffCommand(droneID, altitude));
		} catch (DroneException e) {
			throw new FlightZoneException(e);
		}
	}

	@Override
	public double getBatteryStatus() {
		return droneStatus.getBatteryLevel();
	}

	@Override
	public boolean move(double i) {
		// update data from the server
		// TODO: this might not necessarily be the best place to update this
		// baseStation.getIncomingData();
		return !isDestinationReached(0);
		// TODO Auto-generated method stub

	}

	@Override
	public void setVoltageCheckPoint() {
		// TODO Auto-generated method stub

	}

	@Override
	@CoordinateChange
	public boolean isDestinationReached(int i) {
		return Math.abs(currentPosition.distance(currentTarget)) < DronologyConstants.THRESHOLD_WAYPOINT_DISTANCE;
	}

	@Override
	public void updateCoordinates(LlaCoordinate location) {
		// LOGGER.info("Coordinates updated");

		super.setCoordinates(location.getLatitude(), location.getLongitude(), location.getAltitude());

	}

	@Override
	public void updateDroneState(String status) {
		LOGGER.info(status);

	}

	@Override
	public void setGroundSpeed(double speed) {
		try {
			baseStation.sendCommand(new SetGroundSpeedCommand(droneID, speed));
		} catch (DroneException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		try {
			baseStation.sendCommand(new SetVelocityCommand(droneID, x, y, z));
		} catch (DroneException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void updateBatteryLevel(double batteryLevel) {
		super.updateBatteryLevel(batteryLevel);

	}

	@Override
	public void updateVelocity(double velocity) {
		super.setVelocity(velocity);
	}

	@Override
	public void sendCommand(AbstractDroneCommand command) throws DroneException {
		baseStation.sendCommand(command);
		
	}
}
