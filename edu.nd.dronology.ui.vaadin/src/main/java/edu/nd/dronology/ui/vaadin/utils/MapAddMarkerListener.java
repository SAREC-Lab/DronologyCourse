package edu.nd.dronology.ui.vaadin.utils;

import java.awt.MouseInfo;
import java.util.Iterator;
import java.util.List;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;

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
	private boolean atEnd = false;
	private boolean buttonSelected = false;
	private MapMarkerUtilities route;
	private Window popup;
	
	private static MapAddMarkerListener instance = null;
	
	public static MapAddMarkerListener getInstance(MapMarkerUtilities route, Window popup) {
		if (instance == null) {
			instance = new MapAddMarkerListener(route, popup);
		}
		return instance;
	}
	
	private MapAddMarkerListener(MapMarkerUtilities route, Window popup) {
		this.route = route;
		this.popup = popup;
	}
	
	@Override
	public void onClick(LeafletClickEvent e) {
		if (atEnd && !buttonSelected) {
	    	removeCurrentWayPoint();
		}
		
		atEnd = false;
		
		if (route.getLineClicked()) {
			//Notification.show("Line was clicked");
		}
			
		currentWayPoint = route.addNewPin(e.getPoint(), route.getLineClicked());
		popup.setPosition((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY() - 45);
		route.setLineClicked(false);
			
		buttonSelected = false;

		UI.getCurrent().addWindow(popup);
		route.disableRouteEditing();
		Button saveButton = (Button) getComponentByCaption(popup, "Save");
		Button cancelButton = (Button) getComponentByCaption(popup, "Cancel");
		TextField altitudeField = (TextField) getComponentByCaption(popup, "Altitude: ");
		TextField transitSpeedField = (TextField) getComponentByCaption(popup, "Transit Speed: ");
		
		altitudeField.setRequiredIndicatorVisible(true);
		transitSpeedField.setRequiredIndicatorVisible(true);
		
		saveButton.addClickListener(event -> {
			altitude = altitudeField.getValue();
			transitSpeed = transitSpeedField.getValue();
			buttonSelected = true;
			String caption = "";
			if (altitude.isEmpty())
				caption = "Altitude is the empty string.";
			if (transitSpeed.isEmpty()) {
				if (altitude.isEmpty())
					caption = caption + "\n" + "Approaching speed is the empty string.";
				else
					caption = "Approaching speed is the empty string.";
			}
	    	if (!altitude.isEmpty() && !transitSpeed.isEmpty()) {
	    		UI.getCurrent().removeWindow(popup);
				route.enableRouteEditing();
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
	    	
	    	//Notification.show(route.getMapPoints().get(0).getAltitude());
		});
		
		cancelButton.addClickListener(event -> {
			buttonSelected = true;
	    	removeCurrentWayPoint();

			UI.getCurrent().removeWindow(popup);

			route.enableRouteEditing();
			List<LPolyline> polylines = route.getPolylines();
			for(int i = 0; i < polylines.size(); i++){
				route.getMap().addComponent(polylines.get(i));
			}
		});
		atEnd = true;
	}
	
	public boolean isButtonSelected () {
		return buttonSelected;
	}

	public void removeCurrentWayPoint() {
		for (int i = 0; i < route.getMapPoints().size(); i++) {
			if (route.getMapPoints().get(i).getId().equals(currentWayPoint.getId())) {
				route.getMapPoints().remove(route.getMapPoints().get(i));
				route.getGrid().setItems(route.getMapPoints());
				route.removeAllLines(route.getPolylines());
				route.setPolylines(route.drawLines(route.getMapPoints(), false));
				
			}
		}
		for (int i = 0; i < route.getPins().size(); i++) {
			if (route.getPins().get(i).getId().equals(currentWayPoint.getId())) {
				route.getMap().removeComponent(route.getPins().get(i));
				route.getPins().remove(route.getPins().get(i));
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
