package edu.nd.dronology.ui.vaadin.utils;

import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LMarker.DragEndEvent;
import org.vaadin.addon.leaflet.LMarker.DragEndListener;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.shared.Registration;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.flightroutes.FRTableDisplay;

/**
 * This is the class that contains all logic for plotting flight routes.
 * 
 * @author Michelle Galbavy
 */

public class MapMarkerUtilities {
	private class MarkerClickListener implements LeafletClickListener {

		@Override
		public void onClick(LeafletClickEvent event) {	
			LMarker leafletMarker = (LMarker)event.getSource();
			WayPoint w = null;
			
	    	for (int i = 0; i < mapPoints.size(); i++) {
	    		if (mapPoints.get(i).getId().equals(leafletMarker.getId())) {
	    			w = mapPoints.get(i);
	    		}
	    	}
	    	
			Button toDelete = new Button("Remove Waypoint");
			VerticalLayout content = new VerticalLayout();
			PopupView popup = new PopupView(null, content);
			
			toDelete.addClickListener(e -> {
				popup.setPopupVisible(false);
		    	for (int i = 0; i < mapPoints.size(); i++) {
		    		if (mapPoints.get(i).getId().equals(leafletMarker.getId())) {
		    			mapPoints.remove(mapPoints.get(i));
		    			pins.remove(pins.get(i));
		    		}
		    	}
		    	
		    	map.removeComponent(leafletMarker);
				removeAllLines(polylines);
				polylines = drawLines(mapPoints, true);
				grid.setItems(mapPoints);
			});
			
			content.addComponent(new Label("Latitude: " + w.getLatitude()));
			content.addComponent(new Label("Longitude: " + w.getLongitude()));
			content.addComponent(new Label("Altitude: "  + w.getAltitude()));
			content.addComponent(new Label("Transit Speed: " + w.getTransitSpeed()));
			content.addComponent(toDelete);
			
			popup.setPopupVisible(true);
			popup.addStyleName("bring_front");
			
			layout.addComponent(popup, "top:" + String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getY() - 100) + "px;left:" 
					+ String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getX() - 240) + "px");

			map.addComponent(popup);

		}		
	}
	private class MarkerDragEndListener implements DragEndListener {

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
	    	removeAllLines(polylines);
	    	polylines = drawLines(mapPoints, false);
	    	grid.setItems(mapPoints);
	    	for(int i = 0; i < polylines.size(); i++){
				map.addComponent(polylines.get(i));
			}
		}
		
	}
	private class PolylineClickListener implements LeafletClickListener{

		@Override
		public void onClick(LeafletClickEvent event) {
			LPolyline polyline = (LPolyline)event.getSource();
			lineClicked = true;
			for (int j = 0; j < polylines.size(); j++) {
				if (polylines.get(j).getId().equals(polyline.getId())) {
					lineIndex = j+1;
				}
			}
			removeAllLines(polylines);
			
		}
	}
	private LMap map;
	private FRTableDisplay tableDisplay;
	private Grid<WayPoint> grid;
	private Window popup;
	private ArrayList<WayPoint> mapPoints = new ArrayList<>();
	private ArrayList<LPolyline> polylines = new ArrayList<>();
	private ArrayList<LMarker> pins = new ArrayList<>();
	private ArrayList<Registration> registeredListeners = new ArrayList<>();
	private boolean lineClicked = false;
	private int lineIndex = -1;
	private boolean isEditable = false;
	private AbsoluteLayout layout;
	
	public MapMarkerUtilities(AbsoluteLayout layout, LMap map, FRTableDisplay tableDisplay, Window popup) {
		this.map = map;
		this.tableDisplay = tableDisplay;
		this.grid = tableDisplay.getGrid();
		this.popup = popup;
		this.layout = layout;
		grid.getColumn("latitude").setCaption("Latitude");
		grid.getColumn("longitude").setCaption("Longitude");
	}
	
	public MapMarkerUtilities(LMap map){
		this.map = map;
	}

	public WayPoint addNewPin(Point point, boolean lineClicked) {
		WayPoint p = new WayPoint(point, false);
		p.setId(UUID.randomUUID().toString());
		
		addPinForWayPoint(p);
		
		if (!lineClicked) {
			mapPoints.add(p);
		} 
		else {
			mapPoints.add(lineIndex, p);
		}
		
		removeAllLines(polylines);
		polylines = drawLines(mapPoints, true);
		grid.setItems(mapPoints);
		
		return p;
	}
	public WayPoint addNewPinRemoveOld(Point point, boolean first) {
		WayPoint p = new WayPoint(point, false);
		p.setId(UUID.randomUUID().toString());
		addPinForWayPoint(p);
		
		if(first){
			mapPoints.clear();
		}
		mapPoints.add(p);
		removeAllLines(polylines);
		polylines = drawLines(mapPoints, false);
		grid.setItems(mapPoints);
		
		return p;
	}
	
	public void addPinForWayPoint(WayPoint wayPoint) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setId(wayPoint.getId());
		pins.add(leafletMarker);
		
		if (isEditable) {
			leafletMarker.addClickListener(new MarkerClickListener());
    
			/**
			 * Drag End Listener is a listener that updates the path if a waypoint is moved.
			 * The leafletMarker description is used to keep track of the previous point.
			 * @author Patrick Falvey
			 */
			
			leafletMarker.addDragEndListener(new MarkerDragEndListener());
		}

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
	}
	public ArrayList<LPolyline> drawLines(ArrayList<WayPoint> mapPoints, boolean drawOnMap) {
		ArrayList<LPolyline> polylines = new ArrayList<>();

		for (int i = 0; i < mapPoints.size() - 1; i++) {
		WayPoint current =	mapPoints.get(i);
			LPolyline polyline = new LPolyline(current.toPoint(), mapPoints.get(i + 1).toPoint());
			polyline.setId(UUID.randomUUID().toString());
			polyline.setWeight(current.isReached() ? 1 : 2);
			polyline.setColor("#000");
			if (current.isReached()) {
				polyline.setDashArray("5 10");
				polyline.setColor("#249b09");
			}
			else if(i>0 && mapPoints.get(i - 1).isReached()){
				polyline.setColor("#249b09");
			}
			polylines.add(polyline);
			if (drawOnMap)
				map.addComponent(polyline);

			polyline.addClickListener(event -> {
				lineClicked = true;
				for (int j = 0; j < polylines.size(); j++) {
					if (polylines.get(j).getId().equals(polyline.getId())) {
						lineIndex = j + 1;
					}
				}
				removeAllLines(polylines);
			});
		}
		return polylines;
	}
/*	public ArrayList<LPolyline> drawLines(ArrayList<WayPoint> mapPoints) {
		ArrayList<LPolyline> polylines = new ArrayList<>();
		
		for (int i = 0; i < mapPoints.size() - 1; i++) {
			LPolyline polyline = new LPolyline(mapPoints.get(i).toPoint(), mapPoints.get(i+1).toPoint());
			polyline.setId(UUID.randomUUID().toString());
			
			polylines.add(polyline);
			map.addComponent(polyline);
			if (isEditable) {
				polyline.addClickListener(new PolylineClickListener());
			}
		}
		return polylines;
	}*/
	
	public void removeAllLines(ArrayList<LPolyline> polylines) {
		for (int i = polylines.size() - 1; i >= 0; i--) {
			map.removeComponent(polylines.get(i));
		}
		polylines.clear();
	}

	public void enableRouteEditing () {
		isEditable = true;
		for (int i = 0; i < pins.size(); i++) {
			registeredListeners.add(pins.get(i).
					addListener(LeafletClickEvent.class, new MarkerClickListener(), LeafletClickListener.METHOD));
			registeredListeners.add(pins.get(i).
					addListener(DragEndEvent.class, new MarkerDragEndListener(), DragEndListener.METHOD));
		}
		for (int i = 0; i < polylines.size(); i++) {
			registeredListeners.add(polylines.get(i).
					addListener(LeafletClickEvent.class, new PolylineClickListener(), LeafletClickListener.METHOD));
		}
		
		registeredListeners.add(map.addClickListener(MapAddMarkerListener.getInstance(this, popup)));
		tableDisplay.makeEditable(this);
	}
	
	public void disableRouteEditing () {
		isEditable = false;
		for (int i = 0; i < registeredListeners.size(); i++) {
			registeredListeners.get(i).remove();
		}
		registeredListeners.clear();
		tableDisplay.makeUneditable(this);
		//map.setResponsive(false);
	}
	
	public boolean isEditable () {
		return isEditable;
	}
	
	public void removeAllMarkers(ArrayList<LMarker> markers) {
		for (int i = markers.size() - 1; i >= 0; i--) {
			map.removeComponent(markers.get(i));
		}
	}

	public ArrayList<WayPoint> getMapPoints() {
		return mapPoints;
	}
	
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	
	public ArrayList<LPolyline> getPolylines() {
		return polylines;
	}
	
	public void setPolylines(ArrayList<LPolyline> polylines) {
		this.polylines = polylines;
	}
	
	public ArrayList<LMarker> getPins() {
		return pins;
	}
	
	public LMap getMap() {
		return map;
	}
	
	public boolean getLineClicked () {
		return lineClicked;
	}
	public void clearMapPoints(){
		mapPoints.clear();
	}
	public void clearMapPointsIndex(int index){
		mapPoints.subList(index, mapPoints.size()).clear();
	}
	public void setLineClicked (boolean lineClicked) {
		this.lineClicked = lineClicked;
	}
	public void setAllItems(ArrayList<WayPoint> dronologyPoints){
		grid.setItems(dronologyPoints);
	}

}