package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletMouseOverEvent;
import org.vaadin.addon.leaflet.LeafletMouseOverListener;

import com.vaadin.ui.UI;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;

import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRWayPointPopupView;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

// Sets the values for the popup views shown when hovering the mouse over a waypoint. Window is created in the FRMapComponent class.
public class MarkerMouseOverListener implements LeafletMouseOverListener {
	private MapMarkerUtilities mapUtilities;
	private String selectedWayPointId = "";

	public MarkerMouseOverListener (MapMarkerUtilities mapUtilities) {
		this.mapUtilities = mapUtilities;
	}
	
	@Override
	public void onMouseOver(LeafletMouseOverEvent event) {
		FRWayPointPopupView wayPointPopupView = mapUtilities.getMapComponent().getWaypointPopupView();
		wayPointPopupView.setVisible(false);
		wayPointPopupView.setPopupVisible(false);
		// Closes popup while the values are being changed.
		
		LMarker leafletMarker = (LMarker)event.getSource();
		// Determines which marker the mouse was hovering over.
		
		UIWayPoint w = (UIWayPoint)leafletMarker.getData();
		selectedWayPointId = leafletMarker.getId();
		// Retrieves the id of the waypoint of interest.
		
		wayPointPopupView.setLatitute(w.getLatitude());
		wayPointPopupView.setLongitude(w.getLongitude());
		wayPointPopupView.setAltitude(w.getAltitude());
		wayPointPopupView.setTransitSpeed(w.getTransitSpeed());
		wayPointPopupView.setDeleteButtonVisible(mapUtilities.isEditable());
		
		// Math related to positioning the popup view follows.
		double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
		double mapHeight = mapUtilities.getMapComponent().getMetaInfo().isTableViewChecked() ?
				UI.getCurrent().getPage().getBrowserWindowHeight() * 0.55 :
					UI.getCurrent().getPage().getBrowserWindowHeight() - 66;
				
		double xDegreeDifference = mapUtilities.getMap().getBounds().getNorthEastLon() - mapUtilities.getMap().getBounds().getSouthWestLon();
		double yDegreeDifference = mapUtilities.getMap().getBounds().getSouthWestLat() - mapUtilities.getMap().getBounds().getNorthEastLat();
		double xMarkerPixelLocation = 
				(leafletMarker.getPoint().getLon() - mapUtilities.getMap().getBounds().getSouthWestLon()) * mapWidth / xDegreeDifference;
		double yMarkerPixelLocation = 
				(leafletMarker.getPoint().getLat() - mapUtilities.getMap().getBounds().getNorthEastLat()) * mapHeight / yDegreeDifference;
		double xOffset = mapUtilities.isEditable() ?
				xMarkerPixelLocation + 93 : xMarkerPixelLocation + 85;
		double yOffset = mapUtilities.isEditable() ?
				yMarkerPixelLocation + 80 : yMarkerPixelLocation + 60;
		
		ComponentPosition position = mapUtilities.getMapComponent().getWaypointPopupViewPosition();
		position.setCSSString("top:" + String.valueOf(yOffset) + "px;left:" + String.valueOf(xOffset) + "px;");
		// Math related to finding the proper positioning of the popup view given any screen dimensions/resolutions.
		
		mapUtilities.getMapComponent().setWaypointPopupViewPosition(position);
		// Applies the position to the popup view.

		wayPointPopupView.setVisible(true);
		wayPointPopupView.setPopupVisible(true);
		// Puts the popup view on the screen once all of its new parameters are set.
	}

	public String getSelectedWayPointId() {
		return selectedWayPointId;
	}
}
