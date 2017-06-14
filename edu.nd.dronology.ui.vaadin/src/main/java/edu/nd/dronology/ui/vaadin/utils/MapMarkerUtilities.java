package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Grid;

/**
 * This is the class that contains all logic for plotting flight routes.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {

	ArrayList<Point> mapPoints = new ArrayList<Point>();
	ArrayList<LPolyline> polylines = new ArrayList<LPolyline>();
	private Grid<Point> grid = new Grid<>();
	
	public MapMarkerUtilities() {
		grid.addColumn(Point::getLat).setCaption("Latitude");
		grid.addColumn(Point::getLon).setCaption("Longitude");
	}

	public void addPin(Point point, LMap map) {
		LMarker leafletMarker = new LMarker(point);
		
        leafletMarker.addClickListener(event -> {
        	map.removeComponent(leafletMarker);
        	mapPoints.remove(point);
        	removeAllLines(polylines, map);
        	grid.setItems(mapPoints);
        	polylines = drawLines(mapPoints, map);
        });
        
		map.addComponent(leafletMarker);
		mapPoints.add(point);
		removeAllLines(polylines, map);
    	polylines = drawLines(mapPoints, map);
		grid.setItems(mapPoints);
	}
	
	public ArrayList<LPolyline> drawLines(ArrayList<Point> mapPoints, LMap map) {
		ArrayList<LPolyline> polylines = new ArrayList<LPolyline>();
		
		for (int i = mapPoints.size() - 1; i > 0; i--) {
        	LPolyline polyline = new LPolyline(mapPoints.get(i), mapPoints.get(i-1));
        	polylines.add(polyline);
			map.addComponent(polyline);
		}
		return polylines;
	}
	
	public void removeAllLines(ArrayList<LPolyline> polylines, LMap map) {
    	for (int i = polylines.size() - 1; i >= 0; i--) {
    		map.removeComponent(polylines.get(i));
    	}
	}
	
	public Grid<Point> getGrid() {
		return grid;
	}

}