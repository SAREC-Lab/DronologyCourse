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

	ArrayList<WayPoint> mapPoints = new ArrayList<WayPoint>();
	ArrayList<LPolyline> polylines = new ArrayList<LPolyline>();
	private Grid<WayPoint> grid = new Grid<>(WayPoint.class);
	
	public MapMarkerUtilities() {
		grid.getColumn("latitude").setCaption("Latitude");
		grid.getColumn("longitude").setCaption("Longitude");
	}

	public void addPin(Point point, LMap map) {
		LMarker leafletMarker = new LMarker(point);
        leafletMarker.addClickListener(event -> {
        	for (int i = 0; i < mapPoints.size(); i++) {
        		if (mapPoints.get(i).isAtPoint(event.getPoint()))
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
        	if (leafletMarker.getDescription().equals(""))
        		index = mapPoints.indexOf(point);
        	else {
        		for (int i = 0; i < mapPoints.size(); i++){
        			String latlon = Double.toString(mapPoints.get(i).getPoint().getLat());
        			latlon += Double.toString(mapPoints.get(i).getPoint().getLon());
        			if (latlon.equals(leafletMarker.getDescription())){
        				index = i;
        			}
        		}
        	}
        	String description = (Double.toString(leafletMarker.getPoint().getLat()) + Double.toString(leafletMarker.getPoint().getLon()));
        	leafletMarker.setDescription(description);
        	mapPoints.get(index).setLatitude(Double.toString(leafletMarker.getPoint().getLat()));
        	mapPoints.get(index).setLongitude(Double.toString(leafletMarker.getPoint().getLon()));
        	removeAllLines(polylines, map);
        	polylines = drawLines(mapPoints, map);
        	grid.setItems(mapPoints);
        });
		map.addComponent(leafletMarker);
		mapPoints.add(new WayPoint(point));
		removeAllLines(polylines, map);
    	polylines = drawLines(mapPoints, map);
		grid.setItems(mapPoints);
	}
	
	public ArrayList<LPolyline> drawLines(ArrayList<WayPoint> mapPoints, LMap map) {
		ArrayList<LPolyline> polylines = new ArrayList<LPolyline>();
		
		for (int i = mapPoints.size() - 1; i > 0; i--) {
        	LPolyline polyline = new LPolyline(mapPoints.get(i).getPoint(), mapPoints.get(i-1).getPoint());
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