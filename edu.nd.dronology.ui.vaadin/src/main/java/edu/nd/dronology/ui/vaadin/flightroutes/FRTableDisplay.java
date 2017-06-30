package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
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
	ArrayList<WayPoint> mapPoints = new ArrayList<>();
	MapMarkerUtilities route;
	
	public FRTableDisplay() {
		grid.setColumnOrder("id", "latitude", "longitude", "altitude", "transitSpeed");
		grid.addColumn(event -> "Delete",
			new ButtonRenderer<WayPoint> (clickEvent -> {
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
					
				yes.addClickListener(event -> {
					WayPoint w = clickEvent.getItem();
			    	for (int i = 0; i < route.getMapPoints().size(); i++) {
			    		if (route.getMapPoints().get(i).getId().equals(w.getId())) {
			    			route.getMapPoints().remove(route.getMapPoints().get(i));
			    			route.getMap().removeComponent(route.getPins().get(i));
			    			route.getPins().remove(route.getPins().get(i));
			    		}
			    	}

				   	route.removeAllLines(route.getPolylines());
				   	route.setPolylines(route.drawLines(route.getMapPoints()));
				   	for(int i = 0; i < route.getPolylines().size(); i++){
						route.getMap().addComponent(route.getPolylines().get(i));
					}
				   	grid.setItems(this.route.getMapPoints());
				   	
				   	UI.getCurrent().removeWindow(deletePanel);
				});
					
				no.addClickListener(event -> {
					UI.getCurrent().removeWindow(deletePanel);
				});
					
				deletePanelContent.addComponent(buttons);
				
				UI.getCurrent().addWindow(deletePanel);
			})
		);
	}
	
	public Grid<WayPoint> getGrid() {
		return grid;
	}
	
	public void makeEditable(MapMarkerUtilities mapMarkers) {
		TextField latitude = new TextField();
		TextField longitude = new TextField();
		TextField altitude = new TextField();
		TextField transitSpeed = new TextField();
		
		grid.getColumn("latitude").setEditorComponent(latitude);
		grid.getColumn("longitude").setEditorComponent(longitude);
		grid.getColumn("altitude").setEditorComponent(altitude);
		grid.getColumn("transitSpeed").setEditorComponent(transitSpeed);
		grid.getEditor().setEnabled(true);
		grid.getEditor().addSaveListener(event -> {
			mapMarkers.updatePinForWayPoint(event.getBean());
		});
		
		
		//grid.asSingleSelect();
		
	}
	
	public void makeUneditable(MapMarkerUtilities mapMarkers) {
		grid.getEditor().cancel();
		grid.getEditor().setEnabled(false);
	}
	
	public void setRoute(MapMarkerUtilities route) {
		this.route = route;
	}
}