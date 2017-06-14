package edu.nd.dronology.ui.vaadin.flightroutes;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

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

	public FRMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("af_map_component");
		
		leafletMap = new LMap();
		
		Configuration configuration = Configuration.getInstance();
		leafletMap.setCenter(configuration.getMapCenterLat(), configuration.getMapCenterLon());
		leafletMap.setZoomLevel(configuration.getMapDefaultZoom());
		
		VerticalLayout content = new VerticalLayout();
		
		MapMarkerUtilities route = new MapMarkerUtilities();
		
		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);
		
		leafletMap.addClickListener(e -> {
			route.addPin(e.getPoint(), this);
		});
		
		leafletMap.addBaseLayer(tiles, name);
		leafletMap.zoomToContent();
		
		setCompositionRoot(content);  
		content.addComponents(leafletMap);
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

}