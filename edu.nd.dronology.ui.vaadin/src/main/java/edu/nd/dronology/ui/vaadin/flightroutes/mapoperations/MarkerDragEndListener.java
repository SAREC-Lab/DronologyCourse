package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LMarker.DragEndEvent;
import org.vaadin.addon.leaflet.LMarker.DragEndListener;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

/* Allows a pin to be dragged around. Once the pin is "dropped", its information is updated in both the grid and its corresponding popup views. 
 * Note: Once this listener is added to a pin, it can always be dragged around. However, the following code is only run once the pin is
 * "dropped" - hence drag end - and this listener is added to it. 
 */
public class MarkerDragEndListener implements DragEndListener {
	private MapMarkerUtilities mapUtilities;

	public MarkerDragEndListener (MapMarkerUtilities mapUtilities) {
		this.mapUtilities = mapUtilities;
	}
	
	@Override
	public void dragEnd(DragEndEvent event) {
		if (!mapUtilities.isEditable())
			return;
		
		LMarker leafletMarker = (LMarker)event.getSource();
	    	updateMarkerWayPointData(leafletMarker);
	    	mapUtilities.getMapComponent().updateLinesAndGrid();
	}
	
	public void updateMarkerWayPointData(LMarker pin) {
		if (pin.getData().getClass().equals(UIWayPoint.class)) {
			UIWayPoint w = (UIWayPoint)pin.getData();
			w.setLatitude(Double.toString(pin.getPoint().getLat()));
			w.setLongitude(Double.toString(pin.getPoint().getLon()));
		}
	}
}
