package edu.nd.dronology.core.vehicle.internal;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.AbstractDrone;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.GoToCommand;
import edu.nd.dronology.core.vehicle.commands.SetModeCommand;
import edu.nd.dronology.core.vehicle.commands.TakeoffCommand;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Placeholder for physical drone code.
 * 
 * @author Jane
 *
 */
public class PhysicalDrone extends AbstractDrone implements IDrone {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PhysicalDrone.class);

	private IDroneCommandHandler baseStation;
	private int droneID;
	private Coordinate currentTarget;

	public PhysicalDrone(String drnName, IDroneCommandHandler baseStation) {
		super(drnName);
		this.baseStation = baseStation;
		currentTarget = new Coordinate(0, 0, 0);
		// droneID = 0; // TODO: fix this to properly obtain a drone ID
		// the drone ID is made available in the baseStation.getIncomingData() call, which currently only runs after the PhysicalDrone instance is created
		// TODO: fix the timing of this to actually get incoming data all the time (maybe in another thread?)
		try {
			droneID = baseStation.getNewDroneID();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public long getLatitude() {
		return getCoordinates().getLatitude();
	}

	@Override
	public long getLongitude() {
		return getCoordinates().getLongitude();
	}

	@Override
	public int getAltitude() {
		return getCoordinates().getAltitude();
	}

	@Override
	public void flyTo(Coordinate targetCoordinates) {
		// if (targetCoordinates != currentTarget) { // TODO: add some time limit for refreshing the information in case it didn't properly get sent
		// currentTarget = targetCoordinates;
		// baseStation.sendCommand(droneID, "gotoLocation",
		// baseStation.getDroneState(droneID).JSONfromCoord(targetCoordinates));
		// }

		if (targetCoordinates != currentTarget) { // TODO: add some time limit for refreshing the information in case it didn't properly get sent
			currentTarget = targetCoordinates;
			try {
				baseStation.sendCommand(new GoToCommand(Integer.toString(droneID), targetCoordinates));
			} catch (DroneException e) {
				LOGGER.error(e);
			}
		}

	}

	@Override
	public Coordinate getCoordinates() {
		// return baseStation.getDroneState(droneID).getLocation();
		return baseStation.getLocation(droneID);
	}

	@Override
	public void land() throws FlightZoneException {
		try {
			baseStation.sendCommand(new SetModeCommand(Integer.toString(droneID), SetModeCommand.MODE_LAND));
		} catch (DroneException e) {
			throw new FlightZoneException(e);
		}
	}

	@Override
	public void takeOff(int altitude) throws FlightZoneException {
		// HashMap<String, Object> tempData = new HashMap<String, Object>();
		// tempData.put("altitude", altitude);
		// baseStation.sendCommand(droneID, "takeoff", tempData);
		try {
			baseStation.sendCommand(new TakeoffCommand(Integer.toString(droneID), altitude));
		} catch (DroneException e) {
			throw new FlightZoneException(e);
		}
	}

	@Override
	public void setCoordinates(long lat, long lon, int alt) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getBatteryStatus() {
		// in volts
		// return baseStation.getDroneState(droneID).getBatteryVoltage();
		return baseStation.getBatteryVoltage(droneID);
	}

	@Override
	public boolean move(int i) {
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
	public boolean isDestinationReached(int i) {
		int horizThreshold = 50;
		int vertThreshold = 2;
		// Coordinates currentPos = baseStation.getDroneState(droneID).getLocation();
		Coordinate currentPos = baseStation.getLocation(droneID);
		float dx = currentTarget.getLatitude() - currentPos.getLatitude();
		float dy = currentTarget.getLongitude() - currentPos.getLongitude();
		int dz = currentTarget.getAltitude() - currentPos.getAltitude();
		if (dx < 0) {
			dx = -1 * dx;
		}
		if (dy < 0) {
			dy = -1 * dy;
		}
		if (dz < 0) {
			dz = -1 * dz;
		}
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
}
