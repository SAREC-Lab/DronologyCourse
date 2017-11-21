package edu.nd.dronology.ui.vaadin.utils;

import java.util.UUID;

import org.vaadin.addon.leaflet.shared.Point;

import edu.nd.dronology.core.util.Waypoint;

/**
 * This is the class that contains methods related to each WayPoint.
 * 
 * @author Michelle Galbavy
 */

public class UIWayPoint {
	private String id = UUID.randomUUID().toString();
	private String longitude = "";
	private String latitude = "";
	private String altitude = "0";
	private String transitSpeed = "0";
	private boolean isreached = false;
	private int order = 0;
	
	public UIWayPoint (Waypoint waypoint) {
		longitude = CoordinateUtilities.toSignedDegreesFormat(waypoint.getCoordinate().getLongitude());
		latitude = CoordinateUtilities.toSignedDegreesFormat(waypoint.getCoordinate().getLatitude());
		
		altitude = String.valueOf(waypoint.getCoordinate().getAltitude());
		transitSpeed = String.valueOf(waypoint.getApproachingspeed());
	}
	
	public UIWayPoint (Point point, boolean isreached) {
		longitude = CoordinateUtilities.toSignedDegreesFormat(point.getLon());
		latitude = CoordinateUtilities.toSignedDegreesFormat(point.getLat());
		this.isreached = isreached;
	}
	public boolean isAtPoint(Point point) {
		if (this.latitude.equals(Double.toString(point.getLat())) && this.longitude.equals(Double.toString(point.getLon()))) {
			return true;
		} else {
			return false;
		}
	}
	public Point toPoint() {
		Point point = new Point(Double.parseDouble(latitude), Double.parseDouble(longitude));
		return point;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = CoordinateUtilities.toSignedDegreesFormat(longitude);
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = CoordinateUtilities.toSignedDegreesFormat(latitude);
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public String getTransitSpeed() {
		return transitSpeed;
	}
	public void setTransitSpeed(String transitSpeed) {
		this.transitSpeed = transitSpeed;
	}
	public boolean isReached() {
		return isreached;
	}
	public void setOrder(int order) { 
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	public float getLatitudeFloat() {
		// Also written to return a float so input values can be checked for validity.
		return Float.valueOf(latitude);
	}
	public void setLatitudeFloat(float latitude) {
		// Also written to return a float so input values can be checked for validity.
		this.latitude = String.valueOf(latitude);
	}
	public float getLongitudeFloat() {
		// Also written to return a float so input values can be checked for validity.
		return Float.valueOf(longitude);
	}
	public void setLongitudeFloat(float longitude) {
		// Also written to return a float so input values can be checked for validity.
		this.longitude = String.valueOf(longitude);
	}
	public float getAltitudeFloat() {
		// Also written to return a float so input values can be checked for validity.
		return Float.valueOf(altitude);
	}
	public void setAltitudeFloat(float altitude) {
		// Also written to return a float so input values can be checked for validity.
		this.altitude = String.valueOf(altitude);
	}
	public float getTransitSpeedFloat() {
		// Also written to return a float so input values can be checked for validity.
		return Float.valueOf(transitSpeed);
	}
	public void setTransitSpeedFloat(float transitSpeed) {
		// Also written to return a float so input values can be checked for validity.
		this.transitSpeed = String.valueOf(transitSpeed);
	}
}
