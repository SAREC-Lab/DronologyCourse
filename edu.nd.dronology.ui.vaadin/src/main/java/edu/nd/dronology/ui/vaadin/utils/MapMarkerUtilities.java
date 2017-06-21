package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

/**
 * This is the class that contains all logic for plotting flight routes.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {
	private LMap map;
	private Grid<WayPoint> grid;
	private ArrayList<WayPoint> mapPoints = new ArrayList<>();
	private ArrayList<LPolyline> polylines = new ArrayList<>();
	
	public MapMarkerUtilities(LMap map, Grid<WayPoint> grid) {
		this.map = map;
		this.grid = grid;
		grid.getColumn("latitude").setCaption("Latitude");
		grid.getColumn("longitude").setCaption("Longitude");
	}
	
	public MapMarkerUtilities(LMap map){
		this.map = map;
	}

	public void addNewPin(Point point) {
		WayPoint p = new WayPoint(point);
		p.setId(UUID.randomUUID().toString());
		addPinForWayPoint(p);
		
		mapPoints.add(p);
		removeAllLines(polylines);
    polylines = drawLines(mapPoints);
		grid.setItems(mapPoints);
	}
	
	public void addPinForWayPoint(WayPoint wayPoint) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setId(wayPoint.getId());
		
    leafletMarker.addClickListener(event -> {
    	for (int i = 0; i < mapPoints.size(); i++) {
    		if (mapPoints.get(i).getId().equals(leafletMarker.getId()))
    			mapPoints.remove(mapPoints.get(i));
    	}

    	map.removeComponent(leafletMarker);
    	removeAllLines(polylines);
    	polylines = drawLines(mapPoints);
    	grid.setItems(mapPoints);
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
    	removeAllLines(polylines);
    	polylines = drawLines(mapPoints);
    	grid.setItems(mapPoints);
    });

		map.addComponent(leafletMarker);
	}
	
	public void updatePinForWayPoint(WayPoint wayPoint) {
		Iterator<Component> itr = map.iterator();
		while(itr.hasNext()) {
			Object o = itr.next();
			if (o.getClass() == LMarker.class) {
				LMarker marker = (LMarker)o;
				if (marker.getId().equals(wayPoint.getId())) {
					marker.getPoint().setLat(Double.valueOf(wayPoint.getLatitude()));
					marker.getPoint().setLon(Double.valueOf(wayPoint.getLongitude()));
					break;
				}
			}
		}
  	removeAllLines(polylines);
  	polylines = drawLines(mapPoints);
	}
	
	public ArrayList<LPolyline> drawLines(ArrayList<WayPoint> mapPoints) {
		ArrayList<LPolyline> polylines = new ArrayList<>();
		
		for (int i = mapPoints.size() - 1; i > 0; i--) {
			LPolyline polyline = new LPolyline(mapPoints.get(i).toPoint(), mapPoints.get(i-1).toPoint());
      polylines.add(polyline);
			map.addComponent(polyline);
		}
		return polylines;
	}
	
	public void removeAllLines(ArrayList<LPolyline> polylines) {
		for (int i = polylines.size() - 1; i >= 0; i--) {
			map.removeComponent(polylines.get(i));
    }
	}
}