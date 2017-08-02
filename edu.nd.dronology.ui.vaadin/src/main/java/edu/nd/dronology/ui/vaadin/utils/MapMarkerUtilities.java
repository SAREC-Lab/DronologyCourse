package edu.nd.dronology.ui.vaadin.utils;

import java.awt.MouseInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LMarker.DragEndEvent;
import org.vaadin.addon.leaflet.LMarker.DragEndListener;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.LeafletMouseOutEvent;
import org.vaadin.addon.leaflet.LeafletMouseOutListener;
import org.vaadin.addon.leaflet.LeafletMouseOverEvent;
import org.vaadin.addon.leaflet.LeafletMouseOverListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMapComponent;
import edu.nd.dronology.ui.vaadin.flightroutes.FRTableDisplay;

/**
 * This is the class that contains all logic for plotting flight routes. It contains logic for all listeners assigned to waypoints and polylines, the addition
 * of waypoints, updating waypoints, updating pins on the map, entering and exiting edit mode, setting altitude and transit speed from input values, and
 * related functions.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {
	private class MarkerMouseOverListener implements LeafletMouseOverListener {
		// Sets the values for the popup views shown when hovering the mouse over a waypoint. Window is created in the FRMapComponent class.

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {
			popup.setVisible(false);
			popup.setPopupVisible(false);
			// Closes popup while the values are being changed.
			
			leafletMarker = (LMarker)event.getSource();
			// Determines which marker the mouse was hovering over.
			
    	for (int i = 0; i < mapPoints.size(); i++) {
    		if (mapPoints.get(i).getId().equals(leafletMarker.getId())) {
    			w = mapPoints.get(i);
    		}
    	}
    	selectedWayPointId = w.getId();
    	// Retrieves the id of the waypoint of interest.

			VerticalLayout popupContent = (VerticalLayout)popup.getContent().getPopupComponent();
			Iterator<Component> it = popupContent.iterator();
			while(it.hasNext()) {
				Component c = it.next();
				if (c.getId() != null && c.getId().equals("latitude")) {
					Label l = (Label)c;
					l.setValue("Latitude: " + w.getLatitude());
				}
				if (c.getId() != null && c.getId().equals("longitude")) {
					Label l = (Label)c;
					l.setValue("Longitude: " + w.getLongitude());
				}
				if (c.getId() != null && c.getId().equals("altitude")) {
					Label l = (Label)c;
					l.setValue("Altitude: "  + w.getAltitude());
				}
				if (c.getId() != null && c.getId().equals("transitSpeed")) {
					Label l = (Label)c;
					l.setValue("Transit Speed: " + w.getTransitSpeed());
				}
				// Sets the strings in the popup view from the values of the waypoint of interest.
				if (c.getId() != null && c.getId().equals("toDelete")) {
					if (isEditable) {
						c.setVisible(true);
					}
					else {
						c.setVisible(false);
					}
				}
				// Sets whether or not the column of buttons is visible based on whether or not the map is editable.
			}
			
			// Math related to positioning the popup view follows.
			double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
			double mapHeight = UI.getCurrent().getPage().getBrowserWindowHeight() * 0.55;
			
			double xDegreeDifference = -(map.getCenter().getLon() - leafletMarker.getPoint().getLon());
			double yDegreeDifference = map.getCenter().getLat() - leafletMarker.getPoint().getLat();
			double degreePerZoom = (360.0/(Math.pow(2, map.getZoomLevel())));
			double degreePerPixel = degreePerZoom / mapWidth;
			double xPixelDifference = (xDegreeDifference / degreePerPixel) / 3.0;
			double yPixelDifference = (yDegreeDifference / degreePerPixel) / 3.0;

			xPixelDifference = xPixelDifference * 0.55;
			yPixelDifference = yPixelDifference * 0.6;
			
			double pixelsToLeftBorder = (mapWidth / 2.0) + xPixelDifference;
			double pixelsToTopBorder = (mapHeight / 2.0) + yPixelDifference;
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			double mapTopLeftX = mouseX - pixelsToLeftBorder;
			double mapTopLeftY = mouseY - pixelsToTopBorder;
			
			double xAdjust = 0;
			if (mouseX - mapTopLeftX > mapWidth / 2)
				xAdjust = 140;
			else 
				xAdjust = 90;
			
			double yAdjust = 0;
			if (mouseY - mapTopLeftY > mapHeight / 2)
				yAdjust = 140;
			else
				yAdjust = 90;
			
			double adjustedXLocation = mouseX - mapTopLeftX + xAdjust;
			double adjustedYLocation = mouseY - mapTopLeftY + yAdjust;

			ComponentPosition position = layout.getPosition(popup);
			x = (int) MouseInfo.getPointerInfo().getLocation().getX();
			y = (int) MouseInfo.getPointerInfo().getLocation().getY();
			position.setCSSString("top:" + String.valueOf(adjustedYLocation) + "px;left:" + String.valueOf(adjustedXLocation) + "px;");
			// Math related to finding the proper positioning of the popup view given any screen dimensions/resolutions.
			
			layout.setPosition(popup, position);
			// Applies the position to the popup view.

			popup.setVisible(true);
			popup.setPopupVisible(true);
			// Puts the popup view on the screen once all of its new parameters are set.
		}
	}
	private class MarkerMouseOutListener implements LeafletMouseOutListener {
		/* Closes the popup view created in the mouse over listener above if the mouse leaves the waypoint unless it leaves in the bottom right hand quadrant when
		 * the map is in edit mode. This is because the popup view is supposed to show up in the bottom right hand quadrant, and the "Remove Waypoint" button needs
		 * to be accessible when the map is in edit mode. 
		 */

		@Override
		public void onMouseOut(LeafletMouseOutEvent event) {
			if (!(isEditable && (int) MouseInfo.getPointerInfo().getLocation().getX() >= x + popup.getWidth() &&
					(int) MouseInfo.getPointerInfo().getLocation().getY() >= y + popup.getHeight())) {
				popup.setPopupVisible(false);
			}
		}
	}
	private class MarkerDragEndListener implements DragEndListener {
		/* Allows a pin to be dragged around. Once the pin is "dropped", its information is updated in both the grid and its corresponding popup views. 
		 * Note: Once this listener is added to a pin, it can always be dragged around. However, the following code is only run once the pin is
		 * "dropped" - hence drag end - and this listener is added to it. 
		 */
		
		@Override
		public void dragEnd(DragEndEvent event) {
			LMarker leafletMarker = (LMarker)event.getSource();
    	int index = -1;
    	for (int i = 0; i < mapPoints.size(); i++) {
    		if (mapPoints.get(i).getId().equals(leafletMarker.getId()))
  				index = i;
    	}
    	mapPoints.get(index).setLatitude(Double.toString(leafletMarker.getPoint().getLat()));
    	mapPoints.get(index).setLongitude(Double.toString(leafletMarker.getPoint().getLon()));
    	refreshMapAndGrid();
		}
	}
	private class PolylineClickListener implements LeafletClickListener {
		// Adds a pin in the middle of a polyline when that polyline is clicked.

		@Override
		public void onClick(LeafletClickEvent event) {
			if (isEditable) {
				// Ensures the map is editable before doing anything. This way, polyline listeners never have to be deleted.
				
				isPolyline = true;
				LPolyline polyline = (LPolyline)event.getSource();
				List<LPolyline> polylines = getPolylines();
				
				for (int j = 0; j < polylines.size(); j++) {
					if (polylines.get(j).getId().equals(polyline.getId())) {
						int index = j + 1;
						mapAddMarkerListener.processOnClick(event.getPoint(), index);
						// Opens the window to enter altitude and transit speed for the newly added waypoint.
					}
				}
			}
		}
	}
	
	private AbsoluteLayout layout;
	private LMap map;
	private FRTableDisplay tableDisplay;
	private PopupView popup;
	private FRMapComponent mapComponent;
	private MapAddMarkerListener mapAddMarkerListener;
	
	
	private Grid<WayPoint> grid;
	private List<WayPoint> mapPoints = new ArrayList<>();
	private List<Registration> registeredListeners = new ArrayList<>();
	private boolean isEditable = false;
	private boolean isPolyline = false;
	private int x = 0;
	private int y = 0;
	private WayPoint w = null;
	private String selectedWayPointId = "";
	private LMarker leafletMarker;
	
	public MapMarkerUtilities(AbsoluteLayout layout, LMap map, FRTableDisplay tableDisplay, PopupView popup, FRMapComponent mapComponent, Window window) {
		this.layout = layout;
		this.map = map;
		this.tableDisplay = tableDisplay;
		this.popup = popup;
		this.mapComponent = mapComponent;
		this.mapAddMarkerListener = new MapAddMarkerListener(this, window);
		
		this.grid = tableDisplay.getGrid();
		grid.getColumn("latitude").setCaption("Latitude");
		grid.getColumn("longitude").setCaption("Longitude");
	}
	public MapMarkerUtilities(LMap map){
		this.map = map;
	}
	public WayPoint addNewPin(Point point, int index) {
		WayPoint p = new WayPoint(point, false);
		p.setId(UUID.randomUUID().toString());
		// Creates a waypoint at the given point that has not been reached yet, and assigns it a random id.
		
		//if a marker is added in the middle of a route, then the colors will not be updated, as the first and last markers are the same
		if(index < mapPoints.size() && index != -1){
			//-1 signals that a waypoint was added to the end
			addPinForWayPoint(p, false);
			
		} else {
			addPinForWayPoint(p, true);
		}
		// Adds a pin to the map for the waypoint of interest.
		
		if (index == -1) {
			mapPoints.add(p);
		} 
		else {
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
	public WayPoint addNewPinRemoveOld(Point point, boolean first) {
		// Called in a loop in the FRMainLayout class, this function clears mapPoints the first time, then adds all of the pins back from Dronology.
		
		WayPoint p = new WayPoint(point, false);
		p.setId(UUID.randomUUID().toString());
		addPinForWayPoint(p, true);
		// Adds the new pin.
		
		if (first) {
			mapPoints.clear();
		}
		// Clears mapPoints the first time.
		
		mapPoints.add(p);
		refreshMapAndGrid();
		
		for (int i = 0; i < mapPoints.size(); i++) {
			mapPoints.get(i).setOrder(i + 1);
		}
		// Redraws and resets the map to reflect the new changes.
		
		return p;
	}
	public void addPinForWayPoint(WayPoint wayPoint, boolean updateColors) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setId(wayPoint.getId());
		// Creates a new marker to show the position of the input waypoint.
		
		if (isEditable) {
			leafletMarker.addMouseOverListener(new MarkerMouseOverListener());
			leafletMarker.addDragEndListener(new MarkerDragEndListener());
		}
		// Adds listeners to markers if the map is editable.

		map.addComponent(leafletMarker);
		
		//only updates marker colors if directed
		if(updateColors){
			updatePinColors();	
		}
	}
	public void updatePinColors(){
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource greenIcon = new FileResource(new File(basepath+"/VAADIN/img/green-icon-with-shadow.png"));
		FileResource redIcon = new FileResource(new File(basepath+"/VAADIN/img/red-icon-with-shadow.png"));
		FileResource blueIcon = new FileResource(new File(basepath+"/VAADIN/img/blue-icon-with-shadow.png"));
		
		List<LMarker> storedPins = getPins();
		
		//re-adds the tiles
		LTileLayer tiles = mapComponent.getTiles();
		map.removeAllComponents();
		map.addComponent(tiles);
		
		//resets the colors so that the first and last are green and red respectively - all others are blue
		for(int i = 0; i < storedPins.size(); i++){
			if(i == 0){
				storedPins.get(i).setIcon(greenIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			}
			else if(i == storedPins.size() - 1){
				storedPins.get(i).setIcon(redIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			}else{
				storedPins.get(i).setIcon(blueIcon);
				storedPins.get(i).setIconSize(new Point(41, 41));
				storedPins.get(i).setIconAnchor(new Point(13, 41));
			}
			map.addComponent(storedPins.get(i));
		}
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
					// Updates latitude and longitude values of a waypoint.
					break;
				}
			}
		}
	}
	/**
	 * 
	 * @param mapPoints
	 * @param drawOnMap
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
	public List<LPolyline> drawLines(List<WayPoint> mapPoints, boolean drawOnMap, int mode, boolean fromActive) {
		// Draws polylines based on a list of waypoints, then outputs the newly formed arraylist of polylines.
		List<LPolyline> polylines = new ArrayList<>();
		for (int i = 0; i < mapPoints.size() - 1; i++) {
			WayPoint current =	mapPoints.get(i);
			LPolyline polyline = new LPolyline(current.toPoint(), mapPoints.get(i + 1).toPoint());
			polyline.setId(UUID.randomUUID().toString());
			
			polyline.setWeight(current.isReached() ? 1 : 2);
			if (mode == 0) //normal
				polyline.setColor("#444");
			if (mode == 1) //selected
				polyline.setColor("#000");
			if (mode == 2) //focused
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
			if (drawOnMap)
				map.addComponent(polyline);

			polyline.addClickListener(new PolylineClickListener());
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
	public void enableRouteEditing () {
		map.setEnabled(true);
		isEditable = true;
		
		List<LMarker> pins = getPins();
		for (int i = 0; i < pins.size(); i++) {
			registeredListeners.add(pins.get(i).addDragEndListener(new MarkerDragEndListener()));
			pins.get(i).addMouseOverListener(new MarkerMouseOverListener());
			pins.get(i).addMouseOutListener(new MarkerMouseOutListener());
		}
		// Adds listeners to all of the pins.

		List<LPolyline> polylines = getPolylines();
		
		for (int i = 0; i < polylines.size(); i++) {
			registeredListeners.add(polylines.get(i).
					addListener(LeafletClickEvent.class, new PolylineClickListener(), LeafletClickListener.METHOD));
		}
		// Adds listeners to all of the polylines.
		
		registeredListeners.add(map.addClickListener(mapAddMarkerListener));
		// Adds a click listener to the map.
		
		tableDisplay.makeEditable(this);
	}
	public void disableRouteEditing () {
		isEditable = false;
		for (int i = 0; i < registeredListeners.size(); i++) {
			registeredListeners.get(i).remove();
		}
		registeredListeners.clear();
		// Deletes all listeners in the arraylist of registered listeners.
		
		tableDisplay.makeUneditable(this);
		
		List<LMarker> storedPins = getPins();
		List<LMarker> pins = getPins();
			
		for (int i = 0; i < pins.size(); i++) {
			LMarker newPin = new LMarker(pins.get(i).getPoint().getLat(), pins.get(i).getPoint().getLon());
			storedPins.add(newPin);
		}
		// Makes a new arraylist of pins as a way to remove drag listeners. 
		
		for (int i = 0; i < storedPins.size(); i++) {
			storedPins.get(i).addMouseOverListener(new MarkerMouseOverListener());
			storedPins.get(i).addMouseOutListener(new MarkerMouseOutListener());
		}
		// Adds mouse over and mouse out listeners back to the pins, as those listeners should be there both in and out of edit mode.
	}
	public void removeAllMarkers(List<LMarker> markers) {
		for (int i = markers.size() - 1; i >= 0; i--) {
			map.removeComponent(markers.get(i));
		}
	}
	public void clearMapPoints(){
		mapPoints.clear();
	}
	public void clearMapPointsIndex(int index){
		mapPoints.subList(index, mapPoints.size()).clear();
	}
	public boolean isEditable () {
		return isEditable;
	}
	public List<WayPoint> getMapPoints() {
		return mapPoints;
	}
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	public List<LPolyline> getPolylines() {
		List<LPolyline> polylines = new ArrayList<>();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LPolyline.class)
				polylines.add((LPolyline)c);
		}
		return polylines;
	}
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
	public LMap getMap() {
		return map;
	}
	public void setAllItems(ArrayList<WayPoint> dronologyPoints){
		grid.setItems(dronologyPoints);
	}
	public void setMapPoints(List<WayPoint> waypoints){
		mapPoints = waypoints;
	}
	public void setMapPointsAltitude(List<WayPoint> wayPoints){
		for(int i = 0; i < wayPoints.size(); i++){
			mapPoints.get(i).setAltitude(wayPoints.get(i).getAltitude());
		}
	}
	public void setMapPointsTransit(List<WayPoint> wayPoints){
		for(int i = 0; i < wayPoints.size(); i++){
			mapPoints.get(i).setTransitSpeed(wayPoints.get(i).getAltitude());		
		}
	}
	public void refreshMapAndGrid() {
  	removeAllLines(getPolylines());
  	drawLines(mapPoints, true, 1, false);
  	grid.setItems(mapPoints);
	}
	public String getSelectedWayPointId() {
		return selectedWayPointId;
	}
	public LMarker getLeafletMarker() {
		return leafletMarker;
	}
	public List<Registration> getRegisteredListeners() {
		return registeredListeners;
	}
	public void setRegisteredListeners(List<Registration> registeredListeners) {
		this.registeredListeners = registeredListeners;
	}
	public FRMapComponent getMapComponent() {
		return mapComponent;
	}
	public boolean isPolyline() {
		return isPolyline;
	}
	public void setIsPolyline(boolean isPolyline) {
		this.isPolyline = isPolyline;
	}
	public FRTableDisplay getTableDisplay() {
		return tableDisplay;
	}
}
