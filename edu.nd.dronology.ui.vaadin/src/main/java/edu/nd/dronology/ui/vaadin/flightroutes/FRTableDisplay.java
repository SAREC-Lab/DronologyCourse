package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import edu.nd.dronology.ui.vaadin.utils.WayPoint;

/**
 * This is the class that contains all logic for displaying the latitude and longitude
 * locations of the pins on the map.
 * 
 * @author Michelle Galbavy
 */

public class FRTableDisplay {
	private static final int HasValue = 0;
	private Grid<WayPoint> grid;
	
	public FRTableDisplay(Grid<WayPoint> grid) {
		this.grid = grid;
	}
	
	public void makeEditable() {
//		Binder<Point> binder = grid.getEditor().getBinder();

		TextField latitude = new TextField();
		TextField longitude = new TextField();
		
		grid.getColumn("latitude").setEditorComponent(latitude);
		grid.getColumn("longitude").setEditorComponent(longitude);
//		int Point;
//		Binding<Point, Double> latitudeBinding = binder.bind(HasValue<Point>, Point::getLat, Point::setLat);
		
//		binder.bind(latitude, Point::getLat, Point::setLat);
//		binder.bind(longitude, Point::getLon, Point::setLon); 
		
//		TextField taskField = new TextField();
//		Binding<Point, Double> doneBinding = binder.bind(
//			    taskField, Point.getLat(), Point.setLat());
		
		grid.getEditor().setEnabled(true);
		grid.asSingleSelect();
	}
}