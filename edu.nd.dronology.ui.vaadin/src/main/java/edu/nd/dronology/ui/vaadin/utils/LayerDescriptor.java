package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayerDescriptor {

	private final List<LayerData> layers = new ArrayList<>();

	public List<LayerData> getLayers() {
		return Collections.unmodifiableList(layers);
	}

	public void addLayer(String layerName, String layerURL, boolean isOverlay) {
		layers.add(new LayerData(layerName, layerURL, isOverlay));

	}

	public void addLayer(String layerName, String layerURL) {
		addLayer(layerName, layerURL, false);

	}

	/**
	 * Contains information on the the url, the name, and the overlay flag for each layer displayed in the map.
	 * 
	 * @author Michael
	 *
	 */
	public final class LayerData {

		private final String layerName;
		private final String layerURL;
		private final boolean isOverlay;

		public LayerData(String layerName, String layerURL, boolean isOverlay) {
			this.layerName = layerName;
			this.layerURL = layerURL;
			this.isOverlay = isOverlay;
		}

		public String getLayerURL() {
			return layerURL;
		}

		public String getLayerName() {
			return layerName;
		}

		public boolean isOverlay() {
			return isOverlay;
		}

	}

}
