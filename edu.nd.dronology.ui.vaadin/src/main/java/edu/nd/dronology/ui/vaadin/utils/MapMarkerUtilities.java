package edu.nd.dronology.ui.vaadin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
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
//TODO: Need to split this class and put the responsibilities related to the FR view to the FR package
public class MapMarkerUtilities {	
	LMap map;
	private FRMapComponent mapComponent;
	
	private MapAddMarkerListener mapAddMarkerListener;
	private MarkerMouseOverListener markerMouseOverListener;
	private MarkerMouseOutListener markerMouseOutListener;
	private MarkerDragEndListener markerDragEndListener;
	private PolylineClickListener polylineClickListener;
	
	private boolean isEditable = false;
	
	public MapMarkerUtilities(FRMapComponent mapComponent) {
		this.mapComponent = mapComponent;
		this.map = mapComponent.getMap();

		this.mapAddMarkerListener = new MapAddMarkerListener(this, mapComponent.getNewWayPointWindow());
		this.markerMouseOverListener = new MarkerMouseOverListener(this);
		this.markerMouseOutListener = new MarkerMouseOutListener(this);
		this.markerDragEndListener = new MarkerDragEndListener(this);
		this.polylineClickListener = new PolylineClickListener(this);	
		
		map.addClickListener(mapAddMarkerListener);
	}
	public MapMarkerUtilities(LMap map) {
		this.map = map;
	}
	
	//adds a new pin at a specified point and at a certain index in the list of waypoints (index is relevant when adding a waypoint between two other waypoints)
	//-1 signals that a waypoint was added to the end
	public LMarker addNewPin(Point point, int index) {
		if (index > this.getPins().size())
			index = this.getPins().size();
		
		// Creates a waypoint at the given point, and assigns it a random id.
		UIWayPoint p = new UIWayPoint(point, false);
		return addNewPin(p, index);
	}
	
	public LMarker addNewPin(UIWayPoint p, int index) {
		if (index > this.getPins().size())
			index = this.getPins().size();
		
		// Assign the order
		if (index == -1) {
			p.setOrder(this.getPins().size());
		} else {
			p.setOrder(index);
			List<LMarker> pins = this.getPins();
			for (int i = 0; i < pins.size(); i++) {
				UIWayPoint pInMap = (UIWayPoint)pins.get(i).getData();
				if (pInMap.getOrder() >= index)
					pInMap.setOrder(pInMap.getOrder() + 1);
			}
		}
		
		LMarker newPin = null;
		//if a marker is added in the middle of a route, then the colors will not be updated, as the first and last markers are the same		
		if (index == -1) {
			newPin = addPinForWayPoint(p, true);
		} else {
			newPin = addPinForWayPoint(p, false);
		}
		
		mapComponent.updateLinesAndGrid();		
		return newPin;
	}
	
	//adds a pin in a location designated by the wayPoints. Also takes an argument determining whether or not to update marker colors when called
	private LMarker addPinForWayPoint(UIWayPoint wayPoint, boolean updateColors) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setData(wayPoint);
		leafletMarker.setId(wayPoint.getId());
		
		leafletMarker.addMouseOverListener(markerMouseOverListener);
		leafletMarker.addMouseOutListener(markerMouseOutListener);
		leafletMarker.addDragEndListener(markerDragEndListener);

		map.addComponent(leafletMarker);
		
		//only updates marker colors if directed
		if(updateColors){
			updatePinColors();
		}
		return leafletMarker;
	}
	
	public void removePinById (String id) {
		LMarker p = this.getPinById(id);
		removePin(p);
	}
	
	public void removePin (LMarker p) {
		if (p == null)
			return;
		
		UIWayPoint w = (UIWayPoint)p.getData();
		
		List<LMarker> pins = this.getPins();
		for (int i = 0; i < pins.size(); i++) {
			UIWayPoint pInMap = (UIWayPoint)pins.get(i).getData();
			if (pInMap.getOrder() >= w.getOrder())
				pInMap.setOrder(pInMap.getOrder() - 1);
		}
		map.removeComponent(p);
		updatePinColors();
		mapComponent.updateLinesAndGrid();
	}
	
	//removes all of the pins from the map
	public void removeAllPins() {
		List<LMarker> pins = getPins();
		for (int i = pins.size() - 1; i >= 0; i--) {
			map.removeComponent(pins.get(i));
		}
	}
	
	//retrieves the pins of different colors, removes the pins currently on the map, and re-adds them as the correctly colored markers
	public void updatePinColors(){
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource greenIcon = new FileResource(new File(basepath+"/VAADIN/img/green-icon-with-shadow.png"));
		FileResource redIcon = new FileResource(new File(basepath+"/VAADIN/img/red-icon-with-shadow.png"));
		FileResource blueIcon = new FileResource(new File(basepath+"/VAADIN/img/blue-icon-with-shadow.png"));
		
		List<LMarker> pins = getPins();
		for (LMarker p : pins) {
			if (((UIWayPoint)p.getData()).getOrder() == 0) {
				//first waypoint
				p.setIcon(greenIcon);
			} else if (((UIWayPoint)p.getData()).getOrder() == pins.size()-1) {
				//last waypoint
				p.setIcon(redIcon);
			} else {
				//all others
				p.setIcon(blueIcon);
			}
			p.setIconSize(new Point(41, 41));
			p.setIconAnchor(new Point(13, 41));
		}
	}
	
	/**
	 * 
	 * @param wayPoints
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
	public List<LPolyline> drawLinesForWayPoints(List<UIWayPoint> wayPoints, int mode, boolean fromActive) {
		// Draws polylines based on a list of waypoints, then outputs the newly formed arraylist of polylines.
		List<LPolyline> polylines = new ArrayList<>();
		for (int i = 0; i < wayPoints.size() - 1; i++) {
			UIWayPoint current =	wayPoints.get(i);
			LPolyline polyline = new LPolyline(current.toPoint(), wayPoints.get(i + 1).toPoint());
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
			
			else if (i > 0 && wayPoints.get(i - 1).isReached()) {
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

	public void removeAllLines() {
		// Removes all lines from the map and the polylines arraylist.
		List<LPolyline> polylines = this.getPolylines();
		for (int i = polylines.size() - 1; i >= 0; i--) {
			map.removeComponent(polylines.get(i));
		}
		polylines.clear();
	}
	
	public List<LPolyline> redrawAllLines(int mode, boolean fromActive) {
		removeAllLines();
		List<UIWayPoint> mapPoints = getOrderedWayPoints();
		return drawLinesForWayPoints(mapPoints, mode, fromActive);
	}

	public List<UIWayPoint> getOrderedWayPoints() {
		List<UIWayPoint> wayPoints = new ArrayList<>();
		for (LMarker p : getPins()) {
			wayPoints.add((UIWayPoint)p.getData());
		}
		
		wayPoints.sort(new Comparator<UIWayPoint>() {
	        @Override
	        public int compare(UIWayPoint w1, UIWayPoint w2) {
	        		return  w1.getOrder() - w2.getOrder();
	        }
	    });
		return wayPoints;
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
	
	public LMarker getPinById(String id) {
		List<LMarker> pins = getPins();
		for (LMarker pin : pins) {
			if (pin.getId().equals(id))
				return pin;
		}
		return null;
	}
	
	// Enables route editing.
	public void enableRouteEditing () {
		isEditable = true;
		mapComponent.getTableDisplay().makeEditable();
	}
	
	// Disables route editing
	public void disableRouteEditing () {
		isEditable = false;		
		mapComponent.getTableDisplay().makeUneditable();
	}

	// Returns whether or not edit mode has been enabled.
	public boolean isEditable() {
		return isEditable;
	}
	
	// Returns the map.
	public LMap getMap() {
		return map;
	}
	
	// Returns the mapComponent (use if the functions in FRMapComponent are needed).
	public FRMapComponent getMapComponent() {
		return mapComponent;
	}
	
	public MapAddMarkerListener getMapAddMarkerListener() {
		return mapAddMarkerListener;
	}
	public MarkerMouseOverListener getMarkerMouseOverListener() {
		return markerMouseOverListener;
	}
	public PolylineClickListener getPolylineClickListener() {
		return polylineClickListener;
	}
}
