package edu.nd.dronology.ui.vaadin.flightroutes;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ButtonRenderer;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;

/**
 * This is the class that contains all logic for displaying the latitude and longitude locations of the pins on the map. There is code in its constructor
 * that ensures the values input into the table are usable. It contains functions that cause the grid to enter and exit edit mode, functions to add and
 * delete a column of Delete buttons, and it contains a grid that displays information about waypoints.
 * 
 * @author Michelle Galbavy
 */

public class FRTableDisplay {
	private Grid<WayPoint> grid = new Grid<>(WayPoint.class);
	private MapMarkerUtilities utilities;
	private boolean hasDeleteColumn;
	private TextField latitude = new TextField();
	private TextField longitude = new TextField();
	private TextField altitude = new TextField();
	private TextField transitSpeed = new TextField();
	private Binder<WayPoint> binder = new Binder<>();
	
	public FRTableDisplay() {
		binder = grid.getEditor().getBinder();

		binder.forField(latitude)
			.withConverter(
				new StringToFloatConverter("Must enter a number."))
			.withValidator(latitude -> latitude >= -90 && latitude <= 90, "Must be between -90 and 90.")
			.bind(WayPoint::getLatitudeFloat, WayPoint::setLatitudeFloat);
		// Check that the input for latitude is a float between -90 and 90.
		
		binder.forField(longitude)
			.withConverter(
				new StringToFloatConverter("Must enter a number."))
			.withValidator(longitude -> longitude >= -180 && longitude <= 180, "Must be between -180 and 180.")
			.bind(WayPoint::getLongitudeFloat, WayPoint::setLongitudeFloat);
		// Check that the input for longitude is a float between -180 and 180.
		
		binder.forField(altitude)
			.withConverter(
				new StringToFloatConverter("Must enter a number."))
			.withValidator(altitude -> altitude > 0 && altitude <= 100, "Must be between 0 and 100.")
			.bind(WayPoint::getAltitudeFloat, WayPoint::setAltitudeFloat);
		// Check that the input for altitude is a float between 0 and 100.
		
		binder.forField(transitSpeed)
			.withConverter(
				new StringToFloatConverter("Must be a number."))
			.withValidator(transitSpeed -> transitSpeed > 0, "Must be greater than zero.")
			.bind(WayPoint::getTransitSpeedFloat, WayPoint::setTransitSpeedFloat);
		// Check that the input for transit speed is a float greater than 0.
		
		grid.addStyleName("fr_table_component");
		grid.getColumns().stream().forEach(c -> c.setSortable(false));
		grid.getColumns().stream().forEach(c -> {
			if (c.getCaption().equals("Id") || c.getCaption().equals("Reached")) {
				grid.removeColumn(c);
			}
			else if (c.getCaption().equals("Order")) {
				c.setCaption("#");
			}
		});
		grid.setColumnOrder("order", "latitude", "longitude", "altitude", "transitSpeed");
	  // Sets grid styling (doesn't show Id or Reached columns, and renames Order column to #, and sets the order in which the columns appear).
		
		grid.setColumnResizeMode(null);
		grid.setSelectionMode(SelectionMode.NONE);
	}
	public void makeEditable(MapMarkerUtilities mapMarkers) {
		grid.getColumn("latitude").setEditorComponent(latitude);
		grid.getColumn("longitude").setEditorComponent(longitude);
		grid.getColumn("altitude").setEditorComponent(altitude);
		grid.getColumn("transitSpeed").setEditorComponent(transitSpeed);
		// Makes all columns editable.
		
		grid.getEditor().setEnabled(true);
		grid.getEditor().addSaveListener(event -> {
			mapMarkers.updatePinForWayPoint(event.getBean());
			grid.getEditor().cancel();
			utilities.removeAllLines(utilities.getPolylines());
			grid.setItems(utilities.getMapPoints());
			grid.getEditor().cancel();
		});
		addButtonColumn();
		// Adds a column of Delete buttons to the grid when in edit mode.
	}
	public void makeUneditable(MapMarkerUtilities mapMarkers) {
		grid.getEditor().setEnabled(false);
		removeButtonColumn();
		// Removes the column of Delete buttons from the grid when in edit mode.
	}
	public void addButtonColumn() {
		if (!hasDeleteColumn) {
			// This check ensures that only one column of Delete buttons will be added to the grid at a time.
			hasDeleteColumn = true;
			grid.addColumn(event -> "Delete",
				new ButtonRenderer<WayPoint> (clickEvent -> {
					if (utilities.isEditable()) {
						utilities.getMapComponent().getMainLayout().getDeleteWayPointConfirmation().showWindow(clickEvent);;
					}
				})
			);
		}
	}
	public void removeButtonColumn() {
		hasDeleteColumn = false;
		grid.getColumns().stream().forEach(c -> {
			if (!c.getCaption().equals("Id") && !c.getCaption().equals("Reached") && !c.getCaption().equals("Latitude") && 
					!c.getCaption().equals("Longitude") && !c.getCaption().equals("#") && !c.getCaption().equals("Altitude") &&
							!c.getCaption().equals("Transit Speed")) {
				// This round-about checking method is necessary because the column of Delete buttons has an empty caption.
				grid.removeColumn(c);
			}
		});
	}
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	public void setGrid(List<WayPoint> points) {
		// Takes in waypoints, sets their order, then adds them to the grid.
		grid.setItems(points);
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setOrder(i + 1);
			points.get(i).setId(utilities.getMapPoints().get(i).getId());
		}
	}
	public void setUtilities(MapMarkerUtilities utilities) {
		this.utilities = utilities;
	}
}