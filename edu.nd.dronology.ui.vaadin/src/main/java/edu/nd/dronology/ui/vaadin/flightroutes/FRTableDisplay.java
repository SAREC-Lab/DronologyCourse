package edu.nd.dronology.ui.vaadin.flightroutes;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;

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
	private MapMarkerUtilities route;
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
		
		binder.forField(longitude)
			.withConverter(
				new StringToFloatConverter("Must enter a number."))
			.withValidator(longitude -> longitude >= -180 && longitude <= 180, "Must be between -180 and 180.")
			.bind(WayPoint::getLongitudeFloat, WayPoint::setLongitudeFloat);
		
		binder.forField(altitude)
			.withConverter(
				new StringToFloatConverter("Must enter a number."))
			.withValidator(altitude -> altitude > 0 && altitude <= 100, "Must be between 0 and 100.")
			.bind(WayPoint::getAltitudeFloat, WayPoint::setAltitudeFloat);
		
		binder.forField(transitSpeed)
			.withConverter(
				new StringToFloatConverter("Must be a number."))
			.withValidator(transitSpeed -> transitSpeed > 0, "Must be greater than zero.")
			.bind(WayPoint::getTransitSpeedFloat, WayPoint::setTransitSpeedFloat);
		
		grid.addStyleName("fr_table_component");
		grid.getColumns().stream().forEach(c -> c.setSortable(false));
		grid.getColumns().stream().forEach(c -> {
			if (/*c.getCaption().equals("Id") || */c.getCaption().equals("Reached")) {
				grid.removeColumn(c);
			}
			else if (c.getCaption().equals("Order")) {
				c.setCaption("#");
			}
		});
		
		grid.setColumnOrder("order", "latitude", "longitude", "altitude", "transitSpeed");
		addButtonColumn();
		grid.setColumnResizeMode(null);
		grid.setSelectionMode(SelectionMode.NONE);
	}
	
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	
	public void makeEditable(MapMarkerUtilities mapMarkers) {
		grid.getColumn("latitude").setEditorComponent(latitude);
		grid.getColumn("longitude").setEditorComponent(longitude);
		grid.getColumn("altitude").setEditorComponent(altitude);
		grid.getColumn("transitSpeed").setEditorComponent(transitSpeed);
		grid.getEditor().setEnabled(true);
		grid.getEditor().addSaveListener(event -> {
			mapMarkers.updatePinForWayPoint(event.getBean());
			grid.getEditor().cancel();
			route.removeAllLines(route.getPolylines());
			grid.setItems(route.getMapPoints());
			grid.getEditor().cancel();
		});
		addButtonColumn();
	}
	
	public void makeUneditable(MapMarkerUtilities mapMarkers) {
		grid.getEditor().setEnabled(false);
		removeButtonColumn();
	}
	
	public void setRoute(MapMarkerUtilities route) {
		this.route = route;
	}
	public void setGrid(List<WayPoint> points){
		grid.setItems(points);
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setOrder(i + 1);
		}
	}
	public void addButtonColumn() {
		if (!hasDeleteColumn) {
			hasDeleteColumn = true;
			grid.addColumn(event -> "Delete",
				new ButtonRenderer<WayPoint> (clickEvent -> {
					if (route.isEditable()) {
						Window deletePanel = new Window(" ");
						VerticalLayout deletePanelContent = new VerticalLayout();
						HorizontalLayout buttons = new HorizontalLayout();
						Button yes = new Button("Yes");
						Button no = new Button("No");
						deletePanel.setContent(deletePanelContent);
						
						deletePanelContent.addComponent(new Label("Are you sure you want to delete this waypoint?"));
						deletePanel.setWidth("425px");
						
						buttons.addComponent(yes);
						buttons.addComponent(no);
						
						deletePanel.setModal(true);
						deletePanel.setClosable(false);
						deletePanel.setResizable(false);
						
						WayPoint w = clickEvent.getItem();
						
						yes.addClickListener(event -> {
							route.removeAllLines(route.getPolylines());
							
							for (int i = 0; i < route.getMapPoints().size(); i++) {
								if (route.getMapPoints().get(i).getId().equals(w.getId())) {
									route.getMapPoints().remove(route.getMapPoints().get(i));
									route.getMap().removeComponent(route.getPins().get(i));
								}
							}
							
							route.drawLines(route.getMapPoints(), true, 1, false);
	
							for (int i = 0; i < this.route.getMapPoints().size(); i++) {
								this.route.getMapPoints().get(i).setOrder(i + 1);
							}
						
							grid.setItems(this.route.getMapPoints());
							grid.setItems(route.getMapPoints());
							UI.getCurrent().removeWindow(deletePanel);				   	
						});
							
						no.addClickListener(event -> {
							UI.getCurrent().removeWindow(deletePanel);
						});
							
						deletePanelContent.addComponent(buttons);
						
						UI.getCurrent().addWindow(deletePanel);
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
				grid.removeColumn(c);
			}
		});
	}
}