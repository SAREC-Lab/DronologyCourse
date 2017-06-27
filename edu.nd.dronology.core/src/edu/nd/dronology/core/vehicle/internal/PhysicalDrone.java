package edu.nd.dronology.core.vehicle.internal;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.IDroneStatusUpdateListener;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.CoordinateConverter;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.vehicle.AbstractDrone;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.GoToCommand;
import edu.nd.dronology.core.vehicle.commands.SetGroundSpeedCommand;
import edu.nd.dronology.core.vehicle.commands.SetModeCommand;
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
public class PhysicalDrone extends AbstractDrone implements IDrone, IDroneStatusUpdateListener {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PhysicalDrone.class);

	private IDroneCommandHandler baseStation;
	private String droneID;
	private LlaCoordinate currentTarget;

	public PhysicalDrone(String drnName, IDroneCommandHandler baseStation) {
		super(drnName);
		this.baseStation = baseStation;
		currentTarget = new LlaCoordinate(0, 0, 0);

		// droneID = 0; // TODO: fix this to properly obtain a drone ID
		// the drone ID is made available in the baseStation.getIncomingData()
		// call, which currently only runs after the PhysicalDrone instance is
		// created
		// TODO: fix the timing of this to actually get incoming data all the
		// time (maybe in another thread?)
		try {
			// droneID = baseStation.getNewDroneID();
			droneID = drnName;
			baseStation.setStatusCallbackListener(droneID, this);
		} catch (Exception | DroneException e) {
			LOGGER.error(e);
		}
		// createDispatchThread

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

	@Override
	public void flyTo(LlaCoordinate targetCoordinates) {
		// if (targetCoordinates != currentTarget) { // TODO: add some time
		// limit for refreshing the information in case it didn't properly get
		// sent
		// currentTarget = targetCoordinates;
		// baseStation.sendCommand(droneID, "gotoLocation",
		// baseStation.getDroneState(droneID).JSONfromCoord(targetCoordinates));
		// }

		if (targetCoordinates != currentTarget) { // TODO: add some time limit
			// for refreshing the
			// information in case it
			// didn't properly get sent
			currentTarget = targetCoordinates;
			try {
				baseStation.sendCommand(new GoToCommand(droneID, targetCoordinates));
			} catch (DroneException e) {
				LOGGER.error(e);
			}
		}

	}

	@Override
	public LlaCoordinate getCoordinates() {

		// IDroneAttribute<Coordinate> location = baseStation.getAttribute(droneID, IDroneAttribute.ATTRIBUTE_BATTERY_VOLTAGE);
		// Coordinate coordinate = location.getValue();
		// LOGGER.info("Coordinates retrieved: (" + Long.toString(coordinate.getLatitude()) + ","
		// + Long.toString(coordinate.getLongitude()) + "," + Integer.toString(coordinate.getAltitude()) + ")");
		// return coordinate;
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
		// HashMap<String, Object> tempData = new HashMap<String, Object>();
		// tempData.put("altitude", altitude);
		// baseStation.sendCommand(droneID, "takeoff", tempData);
		try {
			baseStation.sendCommand(new TakeoffCommand(droneID, altitude));
		} catch (DroneException e) {
			throw new FlightZoneException(e);
		}
	}

	// @Override
	// public void setCoordinates(long lat, long lon, int alt) {
	// // TODO Auto-generated method stub
	// }

	@Override
	public double getBatteryStatus() {
		// in volts

		return droneStatus.getBatteryLevel();

		// IDroneAttribute<Double> attribute = baseStation.getAttribute(droneID, IDroneAttribute.ATTRIBUTE_BATTERY_VOLTAGE);
		// Double level = attribute.getValue();
		// LOGGER.info("Battery LeveL :" +level);
		// return level;
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
		int horizThreshold = 50;
		int vertThreshold = 2;
		// Coordinates currentPos =
		// baseStation.getDroneState(droneID).getLocation();
		LlaCoordinate currentPos = getCoordinates();
		// float dx = currentTarget.getLatitude() - currentPos.getLatitude();
		// float dy = currentTarget.getLongitude() - currentPos.getLongitude();
		// int dz = currentTarget.getAltitude() - currentPos.getAltitude();

		float dx = CoordinateConverter.floatToCoordLong(currentTarget.getLatitude())
				- CoordinateConverter.floatToCoordLong(currentPos.getLatitude());
		float dy = CoordinateConverter.floatToCoordLong(currentTarget.getLongitude())
				- CoordinateConverter.floatToCoordLong(currentPos.getLongitude());
		int dz = new Double(currentTarget.getAltitude() - currentPos.getAltitude()).intValue();

		if (dx < 0) {
			dx = -1 * dx;
		}
		if (dy < 0) {
			dy = -1 * dy;
		}
		if (dz < 0) {
			dz = -1 * dz;
		}
		// LOGGER.info(dx+":"+dy+":"+dz);
		if (dx > horizThreshold) {
			return false;
		}
		if (dy > horizThreshold) {
			return false;
		}
		if (dz > vertThreshold) {
			return false;
		}
		return true;
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
		// super.

	}

	@Override
	public void updateVelocity(double velocity) {
		super.setVelocity(velocity);
	}
}
