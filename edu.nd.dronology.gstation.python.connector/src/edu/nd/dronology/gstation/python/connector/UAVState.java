package edu.nd.dronology.gstation.python.connector;

import java.util.UUID;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.util.CoordinateConverter;
import edu.nd.dronology.core.util.LlaCoordinate;

public final class UAVState {

	// private static transient final ILogger LOGGER = LoggerProvider.getLogger(PythonDroneState.class);

	private final LlaCoordinate location;
	private final LlaCoordinate attitude;
	private final LlaCoordinate velocity;
	private final LlaCoordinate gimbalRotation;
	private final BatteryStatus battery;
	private final LlaCoordinate home;
	private final DroneStatus status;
	private final double heading;
	private final boolean armable;
	private final double airspeed;
	private final double groundspeed;
	private final boolean armed;
	private final DroneMode mode;
	private final String id;

	private enum DroneMode {
		GUIDED, INIT, LAND, RTL, POSHOLD, OF_LOITER, STABILIZE, AUTO, THROW, DRIFT, FLIP, AUTOTUNE, ALT_HOLD, BRAKE, LOITER, AVOID_ADSB, POSITION, CIRCLE, SPORT, ACRO;
	}

	private enum DroneStatus {
		STANDBY, UNINIT, BOOT, CALIBRATING, ACTIVE, CRITICAL, EMERGENCY, POWEROFF, INIT;
	}

	public UAVState() {
		location = new LlaCoordinate(0, 0, 0);
		attitude = new LlaCoordinate(0, 0, 0);
		velocity = new LlaCoordinate(0, 0, 0);
		gimbalRotation = new LlaCoordinate(0, 0, 0);
		home = new LlaCoordinate(0, 0, 0);
		battery = new BatteryStatus();
		status = DroneStatus.INIT;
		heading = 0;
		armable = false;
		airspeed = 0;
		groundspeed = 0;
		armed = false;
		mode = DroneMode.INIT;
		id = UUID.randomUUID().toString();

	}

	public String getId() {
		return id;
	}

	public Coordinate getLocation() {
		return CoordinateConverter.toCoordiate(location);
	}

	public Coordinate getAttitude() {
		return CoordinateConverter.toCoordiate(attitude);
	}

	public Coordinate getVelocity() {
		return CoordinateConverter.toCoordiate(velocity);
	}

	public Coordinate getGimbalRotation() {
		return CoordinateConverter.toCoordiate(gimbalRotation);
	}

	public double getBatteryVoltage() {
		return battery.getBatteryVoltage();
	}

	public double getBatteryCurrent() {
		return battery.getBatteryCurrent();
	}

	public double getBatteryLevel() {
		return battery.getBatteryLevel();
	}

	public Coordinate getHome() {
		return CoordinateConverter.toCoordiate(home);
	}

	public String getStatus() {
		return status.name();
	}

	public double getHeading() {
		return heading;
	}

	public boolean getArmable() {
		return armable;
	}

	public double getAirspeed() {
		return airspeed;
	}

	public double getGroundspeed() {
		return groundspeed;
	}

	public boolean getArmed() {
		return armed;
	}

	public String getMode() {
		return mode.name();
	}

	@Override
	public String toString() {
		return "armed=" + armed + "| mode " + mode + " | Coordinate[" + Double.toString(getLocation().getLatitude()) + ","
				+ Double.toString(getLocation().getLongitude()) + "," + Double.toString(getLocation().getAltitude()) + "]";
	}

	public class BatteryStatus {
		private double current;
		private double voltage;
		private double level;

		public double getBatteryLevel() {
			return level;
		}

		public double getBatteryCurrent() {
			return current;
		}

		public double getBatteryVoltage() {
			return voltage;
		}
	}

}
