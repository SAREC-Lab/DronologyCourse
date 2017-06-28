package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;

/**
 * This is the class that contains all logic for displaying the latitude and longitude
 * locations of the pins on the map.
 * 
 * @author Michelle Galbavy
 */

public class FRTableDisplay {
	private Grid<WayPoint> grid = new Grid<>(WayPoint.class);
	
	public FRTableDisplay() {
		grid.setColumnOrder("id", "latitude", "longitude", "altitude", "transitSpeed");
	}
	
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	
	public void makeEditable(MapMarkerUtilities mapMarkers) {
		TextField latitude = new TextField();
		TextField longitude = new TextField();
		
		grid.getColumn("latitude").setEditorComponent(latitude);
		grid.getColumn("longitude").setEditorComponent(longitude);
		grid.getEditor().setEnabled(true);
		grid.getEditor().addSaveListener(event -> {
			mapMarkers.updatePinForWayPoint(event.getBean());
		});
		grid.asSingleSelect();
	}
	
	public void makeUneditable(MapMarkerUtilities mapMarkers) {
		grid.getEditor().cancel();
		grid.getEditor().setEnabled(false);
	}
}