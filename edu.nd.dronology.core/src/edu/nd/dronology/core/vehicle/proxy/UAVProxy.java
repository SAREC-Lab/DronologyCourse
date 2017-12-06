package edu.nd.dronology.core.vehicle.proxy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IUAVProxy;

/**
 * 
 * Proxy class for Manged Drones. <br>
 * This datastructure is intended to be passed to external clients and contains necessary information regarding the actual UAV managed by Dronology
 *
 */
public class UAVProxy implements Serializable, IUAVProxy {
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

	public UAVProxy(String ID, long latitude, long longitude, int altitude, double batteryLevel, double velocity) {
		this.ID = ID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.batteryLevel = batteryLevel;
		this.velocity = velocity;
		info = new HashMap<>();
		status = "UNKNOWN";
	}


	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getID()
	 */
	@Override
	public String getID() {
		return ID;
	}

	public String getGroundstationId() {
		return groundstationId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getStatus()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getLongitude()
	 */
	@Override
	public double getLongitude() {
		return longitude;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getLatitude()
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getAltitude()
	 */
	@Override
	public double getAltitude() {
		return altitude;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getBatteryLevel()
	 */
	@Override
	public double getBatteryLevel() {
		return batteryLevel;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getVelocity()
	 */
	@Override
	public double getVelocity() {
		return velocity;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getInfo()
	 */
	@Override
	public Map<String, String> getInfo() {
		return info;
	}

	/* (non-Javadoc)
	 * @see edu.nd.dronology.core.vehicle.proxy.IUAVProxy2#getCoordinates()
	 */
	@Override
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
