package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import edu.nd.dronology.ui.vaadin.activeflights.AFMapComponent;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMapComponent;

/**
 * This is the class that contains all logic for plotting flight routes.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {

	ArrayList<Point> FRMapPoints = new ArrayList<Point>();
	ArrayList<Point> AFMapPoints = new ArrayList<Point>();
	
	public MapMarkerUtilities() {
	}

	public void addPin(Point point, FRMapComponent map) {
		LMarker leafletMarker = new LMarker(point);
		map.getMapInstance().addComponent(leafletMarker);
		FRMapPoints.add(point);
		for (int i = FRMapPoints.size() - 1; i > 0; i--) {
        	LPolyline polyline = new LPolyline(FRMapPoints.get(i), FRMapPoints.get(i-1));
			map.getMapInstance().addComponent(polyline);
		}
	}
	
	public void addPin(Point point, AFMapComponent map) {
		LMarker leafletMarker = new LMarker(point);
		map.getMapInstance().addComponent(leafletMarker);
		AFMapPoints.add(point);
		for (int i = AFMapPoints.size() - 1; i > 0; i--) {
        	LPolyline polyline = new LPolyline(AFMapPoints.get(i), AFMapPoints.get(i-1));
			map.getMapInstance().addComponent(polyline);
		}
	}
	
	// get and set methods
}