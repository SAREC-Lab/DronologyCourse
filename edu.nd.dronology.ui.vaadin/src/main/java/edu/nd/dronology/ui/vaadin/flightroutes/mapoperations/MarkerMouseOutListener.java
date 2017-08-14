package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import java.awt.MouseInfo;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletMouseOutEvent;
import org.vaadin.addon.leaflet.LeafletMouseOutListener;

import com.vaadin.ui.UI;

import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRWayPointPopupView;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;

/* Closes the popup view created in the mouse over listener above if the mouse leaves the waypoint unless it leaves in the bottom right hand quadrant when
 * the map is in edit mode. This is because the popup view is supposed to show up in the bottom right hand quadrant, and the "Remove Waypoint" button needs
 * to be accessible when the map is in edit mode. 
 */
public class MarkerMouseOutListener implements LeafletMouseOutListener {
	private MapMarkerUtilities mapUtilities;

	public MarkerMouseOutListener (MapMarkerUtilities mapUtilities) {
		this.mapUtilities = mapUtilities;
	}

	@Override
	public void onMouseOut(LeafletMouseOutEvent event) {
		FRWayPointPopupView wayPointPopupView = mapUtilities.getMapComponent().getWaypointPopupView();
		
		// If the map is editable, allow the user to move the mouse to right and down in order to click the Delete button
		if (mapUtilities.isEditable()) {
			LMarker leafletMarker = (LMarker)event.getSource();
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
			
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			boolean mouseMovedRight = mouseX >= xMarkerPixelLocation + 365;
			boolean mouseMovedDown = mouseY >= yMarkerPixelLocation + 180;
			if (!(mouseMovedRight && mouseMovedDown)) {
				wayPointPopupView.setVisible(false);
				wayPointPopupView.setPopupVisible(false);
			}	
		} else {
			wayPointPopupView.setVisible(false);
			wayPointPopupView.setPopupVisible(false);
		}
	}
}
