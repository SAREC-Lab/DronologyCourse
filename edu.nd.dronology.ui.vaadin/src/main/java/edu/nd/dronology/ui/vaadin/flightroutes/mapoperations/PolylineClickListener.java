package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import java.util.List;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;

// Adds a pin in the middle of a polyline when that polyline is clicked.
public class PolylineClickListener  implements LeafletClickListener {
	private MapMarkerUtilities mapUtilities;
	private boolean polylineIsClickedInThisEvent = false;

	public PolylineClickListener (MapMarkerUtilities mapUtilities) {
		this.mapUtilities = mapUtilities;
	}

	@Override
	public void onClick(LeafletClickEvent event) {
		polylineIsClickedInThisEvent = true;
		if (!mapUtilities.isEditable())
			return;
		
		LPolyline polyline = (LPolyline)event.getSource();
		List<LPolyline> polylines = mapUtilities.getPolylines();
		
		for (int j = 0; j < polylines.size(); j++) {
			if (polylines.get(j).getId().equals(polyline.getId())) {
				// Opens the window to enter altitude and transit speed for the newly added waypoint.
				mapUtilities.getMapAddMarkerListener().processOnClick(event.getPoint(), j+1);
				break;
			}
		}
	}

	public boolean isPolylineIsClickedInThisEvent() {
		return polylineIsClickedInThisEvent;
	}

	public void resetPolylineIsClickedInThisEvent() {
		this.polylineIsClickedInThisEvent = false;
	}
}
