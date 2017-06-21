package edu.nd.dronology.ui.vaadin.flightroutes;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.ui.vaadin.utils.Configuration;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;

/**
 * This is the map component for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	private LMap leafletMap;
	private String altitude = "";
	private String approachingSpeed = "";
	private WayPoint currentWayPoint;
	private Panel panel = new Panel();
	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout routes = new VerticalLayout();
	private HorizontalLayout buttons = new HorizontalLayout();
	private HorizontalLayout buttons2 = new HorizontalLayout();
	private PopupView popup;
	private TextField altitudeField = new TextField("Altitude: ");
	private TextField approachingSpeedField = new TextField("Approaching Speed: ");
	private Button saveButton = new Button("Save");
	private Button cancelButton = new Button("Cancel");
	private boolean atEnd = false;
	private boolean buttonSelected = false;
	FRTableDisplay tableDisplay = new FRTableDisplay();
	MapMarkerUtilities route;

	public FRMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("map_component");
		
		leafletMap = new LMap();
		leafletMap.addStyleName("fr_leaflet_map");
		
		Configuration configuration = Configuration.getInstance();
		leafletMap.setCenter(configuration.getMapCenterLat(), configuration.getMapCenterLon());
		leafletMap.setZoomLevel(configuration.getMapDefaultZoom());
		
		route = new MapMarkerUtilities(leafletMap, tableDisplay.getGrid());
		
		VerticalLayout content = new VerticalLayout();
		
		tableDisplay.getGrid().addStyleName("fr_table_component");
		
		tableDisplay.makeEditable(route);
		
		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);
		
		leafletMap.addClickListener(e -> {
			if (atEnd && !buttonSelected) {
		    	for (int i = 0; i < route.getMapPoints().size(); i++) {
		    		if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
		    			route.getMapPoints().remove(route.getMapPoints().get(i));
		    			route.getGrid().setItems(route.getMapPoints());
		    			route.removeAllLines(route.getPolylines());
		    			route.setPolylines(route.drawLines(route.getMapPoints()));
		    		}
		    	}
		    	for (int i = 0; i < route.getPins().size(); i++) {
		    		if (route.getPins().get(i).getId().equals(currentWayPoint.getId())) {
		    			route.getMap().removeComponent(route.getPins().get(i));
		    			route.getPins().remove(route.getPins().get(i));
		    		}
		    	}
			}
			
			atEnd = false;
			
			panel.setContent(totalLayout);
			panel.addStyleName("fr_info_panel");
			panel.addStyleName("control_panel");
			
			VerticalLayout popupContent = new VerticalLayout();
			
			popupContent.addComponent(altitudeField);
			popupContent.addComponent(approachingSpeedField);
			buttons2.addComponents(saveButton, cancelButton);
			popupContent.addComponent(buttons2);
			
			popup = new PopupView(null, popupContent);
			buttons.addComponents(popup); 
			
			totalLayout.addComponents(buttons, routes);
			
			content.addComponent(panel);
			popup.setPopupVisible(true);
			popup.setHideOnMouseOut(false);
			
			currentWayPoint = route.addNewPin(e.getPoint());
			
			buttonSelected = false;
			
			altitudeField.addValueChangeListener(event -> {
				altitude = (String) altitudeField.getValue();
			});
			
			approachingSpeedField.addValueChangeListener(event -> {
				approachingSpeed = (String) approachingSpeedField.getValue();
			});

			altitudeField.setRequiredIndicatorVisible(true);
			approachingSpeedField.setRequiredIndicatorVisible(true);
			
			saveButton.addClickListener(event -> {
				buttonSelected = true;
				String caption = "";
				if (altitude.isEmpty())
					caption = "Altitude is the empty string.";
				if (approachingSpeed.isEmpty()) {
					if (altitude.isEmpty())
						caption = caption + "\n" + "Approaching speed is the empty string.";
					else
						caption = "Approaching speed is the empty string.";
				}
		    	if (!altitude.isEmpty() && !approachingSpeed.isEmpty()) {
		    		popup.setPopupVisible(false);
		    		for (int i = 0; i < route.getMapPoints().size(); i++) {
		    			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
		    				route.getMapPoints().get(i).setAltitude(altitude);
		    				route.getMapPoints().get(i).setApproachingSpeed(approachingSpeed);
		    				route.getGrid().setItems(route.getMapPoints());
		    			}
		    		}
		    	}
		    	else {
		    		Notification.show(caption);
		    	}
			});
			
			cancelButton.addClickListener(event -> {
				buttonSelected = true;
		    	for (int i = 0; i < route.getMapPoints().size(); i++) {
		    		if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
		    			route.getMapPoints().remove(route.getMapPoints().get(i));
		    			route.getGrid().setItems(route.getMapPoints());
		    			route.removeAllLines(route.getPolylines());
		    			route.setPolylines(route.drawLines(route.getMapPoints()));
		    		}
		    	}
		    	for (int i = 0; i < route.getPins().size(); i++) {
		    		if (route.getPins().get(i).getId().equals(currentWayPoint.getId())) {
		    			route.getMap().removeComponent(route.getPins().get(i));
		    			route.getPins().remove(route.getPins().get(i));
		    		}
		    	}
		    	popup.setPopupVisible(false);
			});
			atEnd = true;
		});
		
		leafletMap.addBaseLayer(tiles, name);
		leafletMap.zoomToContent();
		
		setCompositionRoot(content);  
		content.addComponents(leafletMap, tableDisplay.getGrid());
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

}