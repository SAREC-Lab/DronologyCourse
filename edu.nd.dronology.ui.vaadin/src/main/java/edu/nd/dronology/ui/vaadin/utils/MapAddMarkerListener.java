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
		if (route.isEditable()) {
			processOnClick(e.getPoint(), -1);
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
			altitude = altitudeField.getValue();
			transitSpeed = transitSpeedField.getValue();
			String caption = "";
			if (altitude.isEmpty())
				caption = "Altitude is the empty string.";
			else if (transitSpeed.isEmpty()) {
				if (altitude.isEmpty())
					caption = caption + "\n" + "Approaching speed is the empty string.";
				else
					caption = "Approaching speed is the empty string.";
			}
	    	if (!altitude.isEmpty() && !transitSpeed.isEmpty()) {
	    		UI.getCurrent().removeWindow(window);
	    		for (int i = 0; i < route.getMapPoints().size(); i++) {
	    			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
	    				route.getMapPoints().get(i).setAltitude(altitude);
	    				route.getMapPoints().get(i).setTransitSpeed(transitSpeed);
	    				route.getGrid().setItems(route.getMapPoints());
	    			}
	    		}
	    	}
	    	else {
	    		Notification.show(caption);
	    	}
		});
		
		cancelButton.addClickListener(event -> {
			removeCurrentWayPoint();

			UI.getCurrent().removeWindow(window);

			route.drawLines(route.getMapPoints(), true, 0);
		});
	}

	public void removeCurrentWayPoint() {
		for (int i = 0; i < route.getMapPoints().size(); i++) {
			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
				route.getMapPoints().remove(route.getMapPoints().get(i));
				route.getGrid().setItems(route.getMapPoints());
				route.removeAllLines(route.getPolylines());
				route.drawLines(route.getMapPoints(), false, 1);
			}
		}
		for (int i = 0; i < route.getPins().size(); i++) {
			if (route.getPins().get(i).getId().equals(currentWayPoint.getId())) {
				route.getMap().removeComponent(route.getPins().get(i));
			}
		}
		for(int i = 0; i < route.getPolylines().size(); i++){
			route.getMap().addComponent(route.getPolylines().get(i));
		}
	}

	private Component getComponentByCaption(HasComponents component, String caption) {
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
