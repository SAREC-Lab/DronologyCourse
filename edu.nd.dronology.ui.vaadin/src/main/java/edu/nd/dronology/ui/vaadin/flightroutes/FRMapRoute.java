package edu.nd.dronology.ui.vaadin.flightroutes;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.shared.Point;

/**
 * This is the class that contains all logic for plotting flight routes
 * 
 * @author Michelle Galbavy
 */

public class FRMapRoute {
	private static final long serialVersionUID = 1L;
	
	public FRMapRoute() {
	}

	public void addPin(Point point, FRMapComponent map) {
		LMarker leafletMarker = new LMarker(point);
		map.getMapInstance().addComponent(leafletMarker);
		
	}
}