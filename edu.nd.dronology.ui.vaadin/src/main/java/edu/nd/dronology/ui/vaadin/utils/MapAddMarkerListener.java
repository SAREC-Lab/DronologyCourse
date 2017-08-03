package edu.nd.dronology.ui.vaadin.utils;

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
	private WayPoint currentWayPoint;
	private MapMarkerUtilities route;
	private Window window;
	
	public MapAddMarkerListener(MapMarkerUtilities route, Window window) {
		this.route = route;
		this.window = window;
	}
	@Override
	public void onClick(LeafletClickEvent e) {
		if (route.isEditable() && !route.isPolyline() && e.getPoint().getLat() >= -90 && e.getPoint().getLat() <= 90 &&
				e.getPoint().getLon() >= -180 && e.getPoint().getLon() <= 180) {
			// Adds a pin if the user clicks on the map in a valid place while the map is editable.
			processOnClick(e.getPoint(), -1);
		}
		else {
			route.setIsPolyline(false);
			/* If the user clicked on a polyline, isPolyline in the MapMarkerUtilities class is set to false, as the PolylineClickListener will correctly add
			 * the waypoint to the map. */
		}
	}
	public void processOnClick(Point p, int index) {
		currentWayPoint = route.addNewPin(p, index);
		
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
    		for (int i = 0; i < route.getMapPoints().size(); i++) {
    			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
    				route.getMapPoints().get(i).setAltitude(altitude);
    				route.getMapPoints().get(i).setTransitSpeed(transitSpeed);
    				route.getGrid().setItems(route.getMapPoints());
    			}
    		}
    		route.getMapComponent().onMapEdited(route.getMapPoints());
    	}
    	else {
    		Notification.show(caption);
    	}
    	// Checks to make sure the input altitude and transit speed are valid floats. If they are not, an error is output in the form of a Notification.
		});
		
		cancelButton.addClickListener(event -> {
			removeCurrentWayPoint();
			UI.getCurrent().removeWindow(window);
			route.drawLines(route.getMapPoints(), true, 0, false);
			route.updatePinColors();
		});
	}
	public void removeCurrentWayPoint() {
		for (int i = 0; i < route.getMapPoints().size(); i++) {
			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
				// Removes the point from mapPoints, updates the grid, then redraws all of the lines.
				route.getMapPoints().remove(route.getMapPoints().get(i));
				route.getGrid().setItems(route.getMapPoints());
				route.removeAllLines(route.getPolylines());
				route.drawLines(route.getMapPoints(), false, 1, false);
			}
		}
		for (int i = 0; i < route.getPins().size(); i++) {
			if (route.getPins().get(i).getId().equals(currentWayPoint.getId())) {
				// Removes the pin from the map.
				route.getMap().removeComponent(route.getPins().get(i));
			}
		}
		for(int i = 0; i < route.getPolylines().size(); i++){
			route.getMap().addComponent(route.getPolylines().get(i));
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
