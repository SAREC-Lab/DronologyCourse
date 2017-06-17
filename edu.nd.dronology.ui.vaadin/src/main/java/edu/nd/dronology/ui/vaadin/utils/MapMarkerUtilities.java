package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;
import java.util.UUID;

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

	ArrayList<WayPoint> mapPoints = new ArrayList<>();
	ArrayList<LPolyline> polylines = new ArrayList<>();
	private Grid<WayPoint> grid = new Grid<>(WayPoint.class);
	
	public MapMarkerUtilities() {
		grid.getColumn("latitude").setCaption("Latitude");
		grid.getColumn("longitude").setCaption("Longitude");
	}

	public void addPin(Point point, LMap map) {
		LMarker leafletMarker = new LMarker(point);
		leafletMarker.setId(UUID.randomUUID().toString());
		
        leafletMarker.addClickListener(event -> {
        	for (int i = 0; i < mapPoints.size(); i++) {
        		if (mapPoints.get(i).getId().equals(leafletMarker.getId()))
        			mapPoints.remove(mapPoints.get(i));
        	}

        	map.removeComponent(leafletMarker);
        	removeAllLines(polylines, map);
        	grid.setItems(mapPoints);
        	polylines = drawLines(mapPoints, map);
        });
        
        /**
         * Drag End Listener is a listener that updates the path if a waypoint is moved.
         * The leafletMarker description is used to keep track of the previous point.
         * @author Patrick Falvey
         */
        leafletMarker.addDragEndListener(event-> {
        	int index = -1;
        	for (int i = 0; i < mapPoints.size(); i++) {
        		if (mapPoints.get(i).getId().equals(leafletMarker.getId()))
      				index = i;
        	}
        	mapPoints.get(index).setLatitude(Double.toString(leafletMarker.getPoint().getLat()));
        	mapPoints.get(index).setLongitude(Double.toString(leafletMarker.getPoint().getLon()));
        	removeAllLines(polylines, map);
        	polylines = drawLines(mapPoints, map);
        	grid.setItems(mapPoints);
        });
        
		map.addComponent(leafletMarker);
		WayPoint p = new WayPoint(point);
		p.setId(leafletMarker.getId());
		mapPoints.add(p);
		removeAllLines(polylines, map);
    	polylines = drawLines(mapPoints, map);
		grid.setItems(mapPoints);
	}
	
	public ArrayList<LPolyline> drawLines(ArrayList<WayPoint> mapPoints, LMap map) {
		ArrayList<LPolyline> polylines = new ArrayList<>();
		
		for (int i = mapPoints.size() - 1; i > 0; i--) {
        	LPolyline polyline = new LPolyline(mapPoints.get(i).toPoint(), mapPoints.get(i-1).toPoint());
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
	
	public Grid<WayPoint> getGrid() {
		return grid;
	}

}