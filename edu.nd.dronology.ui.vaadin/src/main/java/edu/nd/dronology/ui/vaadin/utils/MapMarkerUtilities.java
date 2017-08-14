package edu.nd.dronology.ui.vaadin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMapComponent;
import edu.nd.dronology.ui.vaadin.flightroutes.mapoperations.MapAddMarkerListener;
import edu.nd.dronology.ui.vaadin.flightroutes.mapoperations.MarkerDragEndListener;
import edu.nd.dronology.ui.vaadin.flightroutes.mapoperations.MarkerMouseOutListener;
import edu.nd.dronology.ui.vaadin.flightroutes.mapoperations.MarkerMouseOverListener;
import edu.nd.dronology.ui.vaadin.flightroutes.mapoperations.PolylineClickListener;

/**
 * This is the class that contains all logic for plotting flight routes. It contains logic for all listeners assigned to waypoints and polylines, the addition
 * of waypoints, updating waypoints, updating pins on the map, entering and exiting edit mode, setting altitude and transit speed from input values, and
 * related functions.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {	
	LMap map;
	private FRMapComponent mapComponent;
	private MapAddMarkerListener mapAddMarkerListener;
	private MarkerMouseOverListener markerMouseOverListener;
	private MarkerMouseOutListener markerMouseOutListener;
	private MarkerDragEndListener markerDragEndListener;
	private PolylineClickListener polylineClickListener;
	
	private List<UIWayPoint> mapPoints = new ArrayList<>(); // need to get rid of this!!!!!
	private String selectedWayPointId = "";
	private boolean isEditable = false;
	
	public MapMarkerUtilities(FRMapComponent mapComponent, Window window) {
		this.mapComponent = mapComponent;
		this.map = mapComponent.getMap();
		this.mapAddMarkerListener = new MapAddMarkerListener(this, window);
		this.markerMouseOverListener = new MarkerMouseOverListener(this);
		this.markerMouseOutListener = new MarkerMouseOutListener(this);
		this.markerDragEndListener = new MarkerDragEndListener(this);
		this.polylineClickListener = new PolylineClickListener(this);
	}
	public MapMarkerUtilities(LMap map) {
		this.map = map;
	}
	//adds a new pin at a specified point and at a certain index in the list of waypoints (index is relevant when adding a waypoint between two other waypoints)
	public UIWayPoint addNewPin(Point point, int index) {
		UIWayPoint p = new UIWayPoint(point, false);
		// Creates a waypoint at the given point, and assigns it a random id.
		
		//if a marker is added in the middle of a route, then the colors will not be updated, as the first and last markers are the same
		
		if (index < mapPoints.size() && index != -1) {
			//-1 signals that a waypoint was added to the end
			addPinForWayPoint(p, false);
		} else {
			addPinForWayPoint(p, true);
		}
		
		// Adds a pin to the map for the waypoint of interest.
		
		if (index == -1) {
			mapPoints.add(p);
		} else {
			mapPoints.add(index, p);
		}
		
		for (int i = 0; i < mapPoints.size(); i++) {
			mapPoints.get(i).setOrder(i + 1);
		}
		// Resets the order of all of the waypoints in case one of them is added in the middle of a polyline.
		
		refreshMapAndGrid();
		// Redraws lines and resets the points in the grid to match the new points on the map.
		
		return p;
	}
	public UIWayPoint addNewPinRemoveOld(Point point) {
		// Called in a loop in the FRMainLayout class, this function clears mapPoints if is the first point added, then adds all of the pins back from Dronology.
		
		UIWayPoint p = new UIWayPoint(point, false);
		addPinForWayPoint(p, true);
		// Adds the new pin.
		
		mapPoints.add(p);
		refreshMapAndGrid();
		
		for (int i = 0; i < mapPoints.size(); i++) {
			mapPoints.get(i).setOrder(i + 1);
		}
		// Redraws and resets the map to reflect the new changes.
		
		return p;
	}
	//adds a pin in a location designated by the wayPoints. Also takes an argument determining whether or not to update marker colors when called
	public void addPinForWayPoint(UIWayPoint wayPoint, boolean updateColors) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setData(wayPoint);
		
		addPinListeners(leafletMarker);
		leafletMarker.setId(wayPoint.getId());
		// Creates a new marker to show the position of the input waypoint.

		leafletMarker.addMouseOverListener(markerMouseOverListener);
		leafletMarker.addMouseOutListener(markerMouseOutListener);
		
		if (isEditable) {
			leafletMarker.addDragEndListener(markerDragEndListener);
		}
		// Adds listeners to markers if the map is editable.

		map.addComponent(leafletMarker);
		
		//only updates marker colors if directed
		if(updateColors){
			updatePinColors();
		}
	}
	//retrieves the pins of different colors, removes the pins currently on the map, and re-adds them as the correctly colored markers
	public void updatePinColors(){
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource greenIcon = new FileResource(new File(basepath+"/VAADIN/img/green-icon-with-shadow.png"));
		FileResource redIcon = new FileResource(new File(basepath+"/VAADIN/img/red-icon-with-shadow.png"));
		FileResource blueIcon = new FileResource(new File(basepath+"/VAADIN/img/blue-icon-with-shadow.png"));
		
		List<LMarker> storedPins = getPins();
		
		List<LMarker> points = getPins();
		for (int i = 0; i < points.size(); i++) {
			map.removeComponent(points.get(i));
		}

		//resets the colors so that the first and last are green and red respectively - all others are blue
		for (int i = 0; i < storedPins.size(); i++) {
			if (i == 0) {
				storedPins.get(i).setIcon(greenIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			}
			else if (i == storedPins.size() - 1) {
				storedPins.get(i).setIcon(redIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			} else {
				storedPins.get(i).setIcon(blueIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			}
			map.addComponent(storedPins.get(i));
		}
	}
	// Updates the latitude and longitude of a waypoint to the wayPoint passed in as an argument.
	public void updatePinForWayPoint(UIWayPoint wayPoint) {
		Iterator<Component> itr = map.iterator();
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o.getClass() == LMarker.class) {
				LMarker marker = (LMarker)o;
				if (marker.getId().equals(wayPoint.getId())) {
					marker.getPoint().setLat(Double.valueOf(wayPoint.getLatitude()));
					marker.getPoint().setLon(Double.valueOf(wayPoint.getLongitude()));
					// Updates latitude and longitude values of a waypoint.
					break;
				}
			}
		}
	}
	/**
	 * 
	 * @param mapPoints
	 * @param mode
	 * 			determines the color of the line. 0 is gray. 1 is black. 2 is orange.
	 * 			For flight routes, the mode should primarily be 1. For active flights,
	 * 			it varies on if a drone is focused, checked, or neither.
	 * @param fromActive
	 * 			should be true if drawLines is being called from the active flights UI. This
	 * 			determines if the first line segment should be green (which it shouldn't
	 * 			be in the flight routes UI). 
	 * @return list of polylines drawn on the map
	 */
	public List<LPolyline> drawLines(List<UIWayPoint> mapPoints, int mode, boolean fromActive) {
		// Draws polylines based on a list of waypoints, then outputs the newly formed arraylist of polylines.
		List<LPolyline> currentLines = getPolylines();
		for (int i = 0; i < currentLines.size(); i++) {
			map.removeComponent(currentLines.get(i));
		}
		List<LPolyline> polylines = new ArrayList<>();
		for (int i = 0; i < mapPoints.size() - 1; i++) {
			UIWayPoint current =	mapPoints.get(i);
			LPolyline polyline = new LPolyline(current.toPoint(), mapPoints.get(i + 1).toPoint());
			polyline.setId(UUID.randomUUID().toString());
			
			polyline.setWeight(current.isReached() ? 1 : 2);
			if (mode == 0)
				// Normal.
				polyline.setColor("#444");
			if (mode == 1)
				// Selected.
				polyline.setColor("#000");
			if (mode == 2)
				// Focused.
				polyline.setColor("#d87703");
			if (current.isReached()) {
				polyline.setDashArray("5 10");
				polyline.setColor("#249b09");
			}
			// Sets style based on the status of the polyline.
			
			else if (i > 0 && mapPoints.get(i - 1).isReached()) {
				polyline.setColor("#249b09");
			}
			else if (i == 0 && fromActive) {
				polyline.setColor("#249b09");
			}
			
			map.addComponent(polyline);

			polyline.addClickListener(polylineClickListener);
			polylines.add(polyline);
		}
		return polylines;
	}
	public void removeAllLines(List<LPolyline> polylines) {
		// Removes all lines from the map and the polylines arraylist.
		for (int i = polylines.size() - 1; i >= 0; i--) {
			map.removeComponent(polylines.get(i));
		}
		polylines.clear();
	}
	// Enables route editing. This include adding of the the listeners to the markers and polyline while making the table editable and the map responsive.
	public void enableRouteEditing () {
		map.setEnabled(true);
		isEditable = true;
		
		List<LMarker> pins = getPins();
		for (int i = 0; i < pins.size(); i++) {
			pins.get(i).addDragEndListener(markerDragEndListener);
			pins.get(i).addMouseOverListener(markerMouseOverListener);
			pins.get(i).addMouseOutListener(markerMouseOutListener);
		}
		// Adds listeners to all of the pins.
		
		List<LPolyline> polylines = getPolylines();
		
		for (int i = 0; i < polylines.size(); i++) {
			polylines.get(i).addClickListener(polylineClickListener);
		}
		// Adds listeners to all of the polylines.
		
		map.addClickListener(mapAddMarkerListener);
		// Adds a click listener to the map.
		
		mapComponent.getTableDisplay().makeEditable(this);
	}
	// Disables route editing by removing the click listeners and making the table uneditable.
	public void disableRouteEditing () {
		isEditable = false;
		// Deletes all listeners in the arraylist of registered listeners.
		
		mapComponent.getTableDisplay().makeUneditable(this);
		
		List<LMarker> storedPins = getPins();
		List<LMarker> pins = getPins();
			
		for (int i = 0; i < pins.size(); i++) {
			LMarker newPin = new LMarker(pins.get(i).getPoint().getLat(), pins.get(i).getPoint().getLon());
			newPin.setData(pins.get(i));
			storedPins.add(newPin);
		}
		// Makes a new arraylist of pins as a way to remove drag listeners. 
		
		for (int i = 0; i < storedPins.size(); i++) {
			storedPins.get(i).addMouseOverListener(markerMouseOverListener);
			storedPins.get(i).addMouseOutListener(markerMouseOutListener);
		}
		// Adds mouse over and mouse out listeners back to the pins, as those listeners should be there both in and out of edit mode.
	}
	//removes all of the markers from the map
	public void removeAllMarkers(List<LMarker> markers) {
		for (int i = markers.size() - 1; i >= 0; i--) {
			map.removeComponent(markers.get(i));
		}
	}
	//clears all of the waypoints from the mapPoints list
	public void clearMapPoints(){
		mapPoints.clear();
	}
	// Clears the waypoints from mapPoints starting at a certain index (but does not delete waypoints at lower indices).
	public void clearMapPointsIndex(int index) {
		mapPoints.subList(index, mapPoints.size()).clear();
	}
	public void addPinListeners(LMarker pin) {
		pin.addMouseOverListener(markerMouseOverListener);
		pin.addMouseOutListener(markerMouseOutListener);
	}
	// Returns whether or not edit mode has been enabled.
	public boolean isEditable() {
		return isEditable;
	}
	// Returns the mapPoints list.
	public List<UIWayPoint> getMapPoints() {
		return mapPoints;
	}
	public UIWayPoint getWayPointById (String id) {
		for (int i = 0; i < mapPoints.size(); i++) {
	    		if (mapPoints.get(i).getId().equals(id)) {
	    			return mapPoints.get(i);
	    		}
	    	}
		return null;
	}
	// Gets all of the polylines that are on the map.
	public List<LPolyline> getPolylines() {
		List<LPolyline> polylines = new ArrayList<>();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LPolyline.class) {
				polylines.add((LPolyline)c);
			}
		}
		return polylines;
	}
	// Gets all of the pins that are on the map.
	public List<LMarker> getPins() {
		List<LMarker> pins = new ArrayList<>();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LMarker.class)
				pins.add((LMarker)c);
		}
		return pins;
	}
	// Returns the map.
	public LMap getMap() {
		return map;
	}
	// Takes a list of waypoints and sets it as the stored mapPoints.
	public void setMapPoints(List<UIWayPoint> waypoints) {
		mapPoints = waypoints;
	}
	// Takes a list of waypoints and sets the altitude so that they match the altitude of the waypoint in the parameter at the same index.
	public void setMapPointsAltitude(List<UIWayPoint> wayPoints) {
		for (int i = 0; i < wayPoints.size(); i++) {
			mapPoints.get(i).setAltitude(wayPoints.get(i).getAltitude());
		}
	}
	// Takes a list of waypoints and sets the transit speed so that they match the altitude of the waypoint in the parameter at the same index.
	public void setMapPointsTransit(List<UIWayPoint> wayPoints) {
		for (int i = 0; i < wayPoints.size(); i++) {
			mapPoints.get(i).setTransitSpeed(wayPoints.get(i).getAltitude());		
		}
	}
	// Refreshes the map and grid by removing lines, redrawing them, and then setting the map again.
	public void refreshMapAndGrid() {
		removeAllLines(getPolylines());
		drawLines(mapPoints, 0, false);
		this.getMapComponent().getTableDisplay().getGrid().setItems(mapPoints);
	}
	// Sets the id of the selected waypoint (defined elsewhere).
	public void setSelectedWayPointId(String selectedWayPointId) {
		this.selectedWayPointId = selectedWayPointId;
	}
	// Gets the id of the selected waypoint (defined elsewhere).
	public String getSelectedWayPointId() {
		return selectedWayPointId;
	}
	// Returns the mapComponent (use if the functions in FRMapComponent are needed).
	public FRMapComponent getMapComponent() {
		return mapComponent;
	}
	
	public void updateMarkerWayPointData(LMarker pin) {
		if (pin.getData().getClass().equals(UIWayPoint.class)) {
			UIWayPoint w = (UIWayPoint)pin.getData();
			w.setLatitude(Double.toString(pin.getPoint().getLat()));
			w.setLongitude(Double.toString(pin.getPoint().getLon()));
		}
	}
	public MapAddMarkerListener getMapAddMarkerListener() {
		return mapAddMarkerListener;
	}
	public PolylineClickListener getPolylineClickListener() {
		return polylineClickListener;
	}
}
