package edu.nd.dronology.ui.vaadin.map;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

import edu.nd.dronology.ui.vaadin.utils.LayerDescriptor;
import edu.nd.dronology.ui.vaadin.utils.LayerDescriptor.LayerData;

public class LeafletmapFactory {

	private static LayerDescriptor getLayerDescriptor() {

		LayerDescriptor ld = new LayerDescriptor();
		ld.addLayer(VaadinUIMapConstants.ONLINE_WORLD_IMAGERY_NAME, VaadinUIMapConstants.ONLINE_WORLD_IMAGERY_URL);
		ld.addLayer(VaadinUIMapConstants.ONLINE_CARTO_NAME, VaadinUIMapConstants.ONLINE_CARTO_URL);
		ld.addLayer(VaadinUIMapConstants.LOCAL_SB_LAYER_NAME, VaadinUIMapConstants.LOCAL_SB_LAYER_URL);
		ld.addLayer(VaadinUIMapConstants.LOCAL_SB_LAYER_SATELLITE_NAME, VaadinUIMapConstants.LOCAL_SB_LAYER_SATELLITE_URL,
				true);

		return ld;

	}

	public static LMap generateMap() {
		LMap leafletMap = new LMap();

		for (LayerData layerDescription : getLayerDescriptor().getLayers()) {
			LTileLayer tiles = new LTileLayer();
			String name = layerDescription.getLayerName();
			tiles.setUrl(layerDescription.getLayerURL());
			if (layerDescription.isOverlay()) {
				leafletMap.addOverlay(tiles, name);
			} else {
				leafletMap.addBaseLayer(tiles, name);
			}
		}

		leafletMap.setCenter(41.68, -86.25);
		leafletMap.setZoomLevel(13);
		// leafletMap.setMaxZoom(30);
		return leafletMap;

	}

}
