package edu.nd.dronology.ui.vaadin.flightroutes;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletLayer;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.utils.Configuration;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;

/**
 * This is the map component for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	private LMap leafletMap;

	FRTableDisplay tableDisplay = new FRTableDisplay();
	VerticalLayout content = new VerticalLayout();
	FRMetaInfo bar = new FRMetaInfo();
	MapMarkerUtilities route;
	FReditBar edit = new FReditBar();

	public FRMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("map_component");
		
		leafletMap = new LMap();
		leafletMap.addStyleName("fr_leaflet_map");
		
		Configuration configuration = Configuration.getInstance();
		leafletMap.setCenter(configuration.getMapCenterLat(), configuration.getMapCenterLon());
		leafletMap.setZoomLevel(configuration.getMapDefaultZoom());

		Window popup = createWayPointWindow();
		route = new MapMarkerUtilities(leafletMap, tableDisplay, popup);
		
		tableDisplay.getGrid().addStyleName("fr_table_component");
		
		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);
				
		leafletMap.addBaseLayer(tiles, name);
		leafletMap.zoomToContent();
		
		route.disableRouteEditing();
		
		setCompositionRoot(content);
		content.addComponents(leafletMap, tableDisplay.getGrid());
	}
	
	private Window createWayPointWindow() {
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);
		
		VerticalLayout popupContent = new VerticalLayout();
		TextField altitudeField = new TextField("Altitude: ");
		TextField transitSpeedField = new TextField("Transit Speed: ");
		
		popupContent.addComponent(altitudeField);
		popupContent.addComponent(transitSpeedField);
		popupContent.addComponent(buttons);
		
		Window popup;
		popup = new Window(null, popupContent);
		
		popup.setModal(true);
		popup.setClosable(false);
		popup.setResizable(false);
		
		return popup;
	}
	
	public void display(){
		//set bar fields
		
		content.addComponent(bar);
		content.addComponents(leafletMap, tableDisplay.getGrid());
	}
	public void display(FlightRouteInfo info){
		
		route.disableRouteEditing();
		
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("510px");
		layout.setWidth("1075px");
		
		
		FRMetaInfo selectedBar = new FRMetaInfo(info);
		
		FReditBar editBar = new FReditBar();
		editBar.setStyleName("edit_bar");
		CheckBox tempBox = selectedBar.getCheckBox();
		Button edit = selectedBar.getEditButton();
		
		tempBox.addValueChangeListener(event->{
			
			if(tempBox.getValue()){
				displayTable();
			}
			else{
				displayNoTable();
			}
		});
		
		leafletMap.setStyleName("bring_back");
		
		//enable editing
		edit.addClickListener(event -> {
			//Notification.show("run");
			route.enableRouteEditing();
			leafletMap.setEnabled(true);
			editBar.addStyleName("bring_front");
			editBar.setWidth("880px");
			layout.addComponent(editBar, "top: 5px; left:95px");
			
			
			leafletMap.addStyleName("fr_leaflet_map_edit_mode");
			tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
		});
		
		
		
		Button cancel = editBar.getCancelButton();
		cancel.addClickListener(event -> {
			
			route.disableRouteEditing();
			
			route.removeAllMarkers(route.getPins());
			route.removeAllLines(route.getPolylines());
			
			int numberMapPoints = route.getMapPoints().size();
			
			
			route.clearMapPointsIndex(info.getCoordinates().size());
			route.getGrid().setItems(route.getMapPoints());
			
			//content.removeComponent(tableDisplay.getGrid());
			//content.addComponent(tableDisplay.getGrid());
			
			layout.removeComponent(editBar);
			leafletMap.setStyleName("fr_leaflet_map");
			leafletMap.addStyleName("bring_back");
			tableDisplay.getGrid().setStyleName("fr_table_component");
			leafletMap.setEnabled(false);
		});
		
		Button save = editBar.getSaveButton();
		save.addClickListener(event -> {
			
			//send info to dronology
			
			route.disableRouteEditing();
			
			layout.removeComponent(editBar);
			leafletMap.setStyleName("fr_leaflet_map");
			leafletMap.addStyleName("bring_back");
			tableDisplay.getGrid().setStyleName("fr_table_component");
			leafletMap.setEnabled(false);
			
			//route.removeAllMarkers(route.getPins());
		});
		layout.addComponent(leafletMap, "top:5px; left:5px");
	
		
		
		content.removeAllComponents();
		//content.addComponent(editBar);
		content.addComponent(selectedBar);
		content.addComponents(layout, tableDisplay.getGrid());
		
	}
	public void displayNoTable(){
		content.removeComponent(tableDisplay.getGrid());
	}
	public void displayTable(){
		content.addComponent(tableDisplay.getGrid());
	}
	
	public void setCenter(double centerLat, double centerLon) {
		leafletMap.setCenter(41.68, -86.25);
	}

	public void setZoomLevel(double zoomLevel) {
		leafletMap.setZoomLevel(zoomLevel);
	}
	
	public double getCenterLat() {
		return leafletMap.getCenter().getLat();
	}
	
	public double getCenterLon() {
		return leafletMap.getCenter().getLon();
	}
	
	public double getZoomLevel() {
		return leafletMap.getZoomLevel();
	}
	
	public LMap getMapInstance() {
		return leafletMap;
	}
	public MapMarkerUtilities getUtils(){
		return route;
	}
	public FRTableDisplay getTableDisplay(){
		return tableDisplay;
	}

}
