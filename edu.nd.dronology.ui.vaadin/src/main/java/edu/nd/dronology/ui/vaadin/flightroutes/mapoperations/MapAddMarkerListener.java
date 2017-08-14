package edu.nd.dronology.ui.vaadin.flightroutes.mapoperations;

import java.awt.MouseInfo;
import java.util.Iterator;

import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

/**
 * This is the class that contains all logic for the map click listener. It contains code for adding a waypoint and popping up a window for the user to
 * input the altitude and transit speed for the newly created waypoint. It also contains code for deleting a waypoint if the user decides they do not want
 * to add a waypoint after all. Lastly, it contains a helper function, which returns a component given its id.
 * 
 * @author Michelle Galbavy
 */

public class MapAddMarkerListener implements LeafletClickListener {

	private String altitude = "";
	private String transitSpeed = "";
	private UIWayPoint currentWayPoint;
	private MapMarkerUtilities mapUtilities;
	private Window window;
	
	public MapAddMarkerListener(MapMarkerUtilities route, Window window) {
		this.mapUtilities = route;
		this.window = window;
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
		currentWayPoint = mapUtilities.addNewPin(p, index);
		
		window.setPosition((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY() - 45);

		UI.getCurrent().addWindow(window);
		Button saveButton = (Button) getComponentByCaption(window, "Save");
		Button cancelButton = (Button) getComponentByCaption(window, "Cancel");
		TextField altitudeField = (TextField) getComponentByCaption(window, "Altitude: ");
		TextField transitSpeedField = (TextField) getComponentByCaption(window, "Transit Speed: ");
		
		saveButton.addClickListener(event -> {
			boolean canSave = true;
			boolean transitSpeedInvalid = false;
			String caption = "";
			
			altitude = altitudeField.getValue();
			try {
				Float.valueOf(altitude);
			} catch (NumberFormatException ex) {
				caption = "Altitude must be a number.";
				canSave = false;
			}
			
			if (altitude.isEmpty()) {
				caption = "Altitude must be a number.";
				canSave = false;
			}
			
			transitSpeed = transitSpeedField.getValue();
			try {
				Float.valueOf(transitSpeed);
			} catch (NumberFormatException ex) {
				if (caption.isEmpty()) {
					caption = "Transit speed must be a number.";
				} else {
					caption = caption + "\n" + "Transit speed must be a number.";
				}
				canSave = false;
				transitSpeedInvalid = true;
			}
			
			if (transitSpeed.isEmpty() && !transitSpeedInvalid) {
				if (caption.isEmpty()) {
					caption = "Transit speed must be a number.";
				} else {
					caption = caption + "\n" + "Transit speed must be a number.";
				}
				canSave = false;
			}
			
		    	if (canSave) {
		    		UI.getCurrent().removeWindow(window);
		    		for (int i = 0; i < mapUtilities.getMapPoints().size(); i++) {
		    			if (mapUtilities.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
		    				mapUtilities.getMapPoints().get(i).setAltitude(altitude);
		    				mapUtilities.getMapPoints().get(i).setTransitSpeed(transitSpeed);
		    				mapUtilities.getMapComponent().getTableDisplay().getGrid().setItems(mapUtilities.getMapPoints());
		    			}
		    		}
		    		mapUtilities.getMapComponent().onMapEdited(mapUtilities.getMapPoints());
		    	}
		    	else {
		    		Notification.show(caption);
		    	}
		    	// Checks to make sure the input altitude and transit speed are valid floats. If they are not, an error is output in the form of a Notification.
		});
		
		cancelButton.addClickListener(event -> {
			removeCurrentWayPoint();
			UI.getCurrent().removeWindow(window);
			mapUtilities.drawLines(mapUtilities.getMapPoints(), 0, false);
			mapUtilities.updatePinColors();
		});
	}
	public void removeCurrentWayPoint() {
		for (int i = 0; i < mapUtilities.getMapPoints().size(); i++) {
			if (mapUtilities.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
				// Removes the point from mapPoints, updates the grid, then redraws all of the lines.
				mapUtilities.getMapPoints().remove(mapUtilities.getMapPoints().get(i));
				mapUtilities.getMapComponent().getTableDisplay().getGrid().setItems(mapUtilities.getMapPoints());
				mapUtilities.removeAllLines(mapUtilities.getPolylines());
				mapUtilities.drawLines(mapUtilities.getMapPoints(), 1, false);
			}
		}
		for (int i = 0; i < mapUtilities.getPins().size(); i++) {
			if (mapUtilities.getPins().get(i).getId().equals(currentWayPoint.getId())) {
				// Removes the pin from the map.
				mapUtilities.getMap().removeComponent(mapUtilities.getPins().get(i));
			}
		}
		for(int i = 0; i < mapUtilities.getPolylines().size(); i++){
			mapUtilities.getMap().addComponent(mapUtilities.getPolylines().get(i));
			// Adds lines back to the polyline arraylist.
		}
	}
	private Component getComponentByCaption(HasComponents component, String caption) {
		// Takes in a component and a caption. The component is returned based on the caption input.
		Iterator<Component> it = component.iterator();
		while (it.hasNext()) {
			Component l = it.next();
			if (caption.equals(l.getCaption()))
				return l;
			else if (l instanceof HasComponents)
				return getComponentByCaption((HasComponents)l, caption);
			else
				continue;
		}
		return null;
	}
}
