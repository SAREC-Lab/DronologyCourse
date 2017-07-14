package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;

import edu.nd.dronology.core.util.LlaCoordinate;

public class UAVStateMessage extends AbstractUAVMessage<Object> implements Serializable {

	private static final long serialVersionUID = -5703232763831907307L;
	public static final String MESSAGE_TYPE = "state";

	private LlaCoordinate location;
	private LlaCoordinate attitude;
	private LlaCoordinate velocity;

	private DroneStatus status;
	private DroneMode mode;

	private boolean armed;
	private boolean armable;

	private double groundspeed;

	private BatteryStatus batterystatus;

	public enum DroneMode {
		GUIDED, INIT, LAND, RTL, POSHOLD, OF_LOITER, STABILIZE, AUTO, THROW, DRIFT, FLIP, AUTOTUNE, ALT_HOLD, BRAKE, LOITER, AVOID_ADSB, POSITION, CIRCLE, SPORT, ACRO;
	}

	public enum DroneStatus {
		STANDBY, UNINIT, BOOT, CALIBRATING, ACTIVE, CRITICAL, EMERGENCY, POWEROFF, INIT;
	}

	public UAVStateMessage(String message, String uavid) {
		super(message, uavid);
	}

	public LlaCoordinate getLocation() {
		return location;
	}

	public void setLocation(LlaCoordinate location) {
		this.location = location;
	}

	public LlaCoordinate getAttitude() {
		return attitude;
	}

	public void setAttitude(LlaCoordinate attitude) {
		this.attitude = attitude;
	}

	public LlaCoordinate getVelocity() {
		return velocity;
	}

	public void setVelocity(LlaCoordinate velocity) {
		this.velocity = velocity;
	}

	public boolean isArmable() {
		return armable;
	}

	public void setArmable(boolean armable) {
		this.armable = armable;
	}

	public double getGroundspeed() {
		return groundspeed;
	}

	public void setGroundspeed(double groundspeed) {
		this.groundspeed = groundspeed;
	}

	public DroneStatus getStatus() {
		return status;
	}

	public void setStatus(DroneStatus status) {
		this.status = status;
	}

	public boolean isArmed() {
		return armed;
	}

	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	public DroneMode getMode() {
		return mode;
	}

	public void setMode(DroneMode mode) {
		this.mode = mode;
	}

	public void setType(String type) {
		this.type = type;

	}

	public BatteryStatus getBatterystatus() {
		return batterystatus;
	}

	public void setBatterystatus(BatteryStatus batterystatus) {
		this.batterystatus = batterystatus;
	}

	@Override
	public String toString() {
		return "armed=" + armed + "| mode " + mode + " | Coordinate[" + Double.toString(getLocation().getLatitude())
				+ "," + Double.toString(getLocation().getLongitude()) + ","
				+ Double.toString(getLocation().getAltitude()) + "]";
	}

	public static class BatteryStatus {
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
