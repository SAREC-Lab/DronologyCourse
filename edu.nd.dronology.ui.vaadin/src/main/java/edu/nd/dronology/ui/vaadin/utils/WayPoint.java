package edu.nd.dronology.ui.vaadin.utils;

import org.vaadin.addon.leaflet.shared.Point;

/**
 * This is the class that contains methods related to each WayPoint.
 * 
 * @author Michelle Galbavy
 */

public class WayPoint {
	private String id = "";
	private String longitude = "";
	private String latitude = "";
	private String altitude = "";
	private String approachingSpeed = "";
	
	public WayPoint (Point point) {
		longitude = Double.toString(point.getLon());
		latitude = Double.toString(point.getLat());
	}
	public boolean isAtPoint(Point point) {
		if (this.latitude.equals(Double.toString(point.getLat())) && this.longitude.equals(Double.toString(point.getLon()))) {
			return true;
		}
		else {
			return false;
		}
	}
	public Point getPoint() {
		Point point = new Point(Double.parseDouble(longitude), Double.parseDouble(latitude));
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
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public String getApproachingSpeed() {
		return approachingSpeed;
	}
	public void setApproachingSpeed(String approachingSpeed) {
		this.approachingSpeed = approachingSpeed;
	}
	
}
