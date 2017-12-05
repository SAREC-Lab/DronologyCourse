package edu.nd.dronology.core.status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.core.util.LlaCoordinate;

public class DroneStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3319827887969940655L;
	private double latitude;
	private double longitude;
	private double altitude;
	private final String ID;
	private double batteryLevel;
	private double velocity;
	private Map<String, String> info;
	private String status;
	private String groundstationId;

	public DroneStatus(String ID, long latitude, long longitude, int altitude, double batteryLevel, double velocity) {
		this.ID = ID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.batteryLevel = batteryLevel;
		this.velocity = velocity;
		info = new HashMap<>();
		status = "UNKNOWN";
	}

	public void setInfoItem(String infoID, String infoValue) {
		info.put(infoID, infoValue);
	}

	public void delInfoItem(String infoID) {
		if (info.containsKey(infoID))
			info.remove(infoID);
	}

	public String getID() {
		return ID;
	}

	public String getGroundstationId() {
		return groundstationId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void updateCoordinates(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public void updateBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public void updateVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public String toString() {
		return "ID: " + ID + " Pos: (" + latitude + "," + longitude + "," + altitude + ") " + " Vel: " + velocity + " Bat: "
				+ batteryLevel + " --- " + this.status;
	}

	@Override
	public int hashCode() {
		return 17 + ID.hashCode();
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public double getVelocity() {
		return velocity;
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public LlaCoordinate getCoordinates() {
		return new LlaCoordinate(latitude, longitude, altitude);
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;

	}

	public void setGroundstationId(String groundstationId) {
		this.groundstationId = groundstationId;

	}

}
