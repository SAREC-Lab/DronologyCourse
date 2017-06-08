package edu.nd.dronology.ui.vaadin.activeflights;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class AFMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	private LMap leafletMap;

	public AFMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("af_map_component");
		
		VerticalLayout content = new VerticalLayout();
		
		leafletMap = new LMap();
		
		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);
		
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