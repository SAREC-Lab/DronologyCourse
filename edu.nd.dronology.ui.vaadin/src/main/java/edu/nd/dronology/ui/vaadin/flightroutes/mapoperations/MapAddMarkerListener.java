package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import java.awt.MouseInfo;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;

/**
 * This is the class that contains all logic for the map click listener. It contains code for adding a waypoint and popping up a window for the user to
 * input the altitude and transit speed for the newly created waypoint. It also contains code for deleting a waypoint if the user decides they do not want
 * to add a waypoint after all. Lastly, it contains a helper function, which returns a component given its id.
 * 
 * @author Michelle Galbavy
 */

public class MapAddMarkerListener implements LeafletClickListener {
	private LMarker newMarker;
	private MapMarkerUtilities mapUtilities;
	private Window newWayPointWindow;
	
	public MapAddMarkerListener(MapMarkerUtilities mapUtilities, Window newWayPointWindow) {
		this.mapUtilities = mapUtilities;
		this.newWayPointWindow = newWayPointWindow;
	}
	@Override
	public void onClick(LeafletClickEvent e) {
		if (!mapUtilities.isEditable())
			return;
		
		if (!mapUtilities.getPolylineClickListener().isPolylineIsClickedInThisEvent()) {
			processOnClick(e.getPoint(), -1);
		}
		mapUtilities.getPolylineClickListener().resetPolylineIsClickedInThisEvent();
	}
	public void processOnClick(Point p, int index) {
		newMarker = mapUtilities.addNewPin(p, index);
		
		newWayPointWindow.setPosition((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY() - 45);
		UI.getCurrent().addWindow(newWayPointWindow);
	}
	public LMarker getNewMarker() {
		return newMarker;
	}
}
