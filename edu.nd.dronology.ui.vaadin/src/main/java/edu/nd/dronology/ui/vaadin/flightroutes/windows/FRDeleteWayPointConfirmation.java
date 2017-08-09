package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import com.vaadin.ui.Component.Event;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

public class FRDeleteWayPointConfirmation {
	private FRMainLayout mainLayout = null;
	public FRDeleteWayPointConfirmation(FRMainLayout mainLayout){
		this.mainLayout = mainLayout;
	}
	
	public void showWindow (Event deleteWaypointClickEvent) {
		MyUI.getYesNoWindow().initForNewMessage(
				"Are you sure you want to delete this waypoint?");
		
		// Click listeners for yes and no buttons on window.
		MyUI.getYesNoWindow().addYesButtonClickListener(e -> {
			MyUI.getYesNoWindow().close();
			MapMarkerUtilities utilities = mainLayout.getMapComponent().getUtilities();
			
			String waypointID = "";
			
			if (deleteWaypointClickEvent.getClass().equals(RendererClickEvent.class)) {
				//clicked remove waypoint on the table
				RendererClickEvent<?> event = (RendererClickEvent<?>)deleteWaypointClickEvent;
				waypointID = ((UIWayPoint)event.getItem()).getId();
			} else {
				//clicked remove waypoint from the popup view
				waypointID = utilities.getSelectedWayPointId();
			}
				
			utilities.removeAllLines(utilities.getPolylines());
			
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				if (utilities.getMapPoints().get(i).getId().equals(waypointID)) {
					utilities.getMapPoints().remove(utilities.getMapPoints().get(i));
					utilities.getMap().removeComponent(utilities.getPins().get(i));
				}
			}
			
			utilities.drawLines(utilities.getMapPoints(), true, 1, false);

			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				utilities.getMapPoints().get(i).setOrder(i + 1);
			}
		
			mainLayout.getMapComponent().getUtilities().getGrid().setItems(utilities.getMapPoints());
			mainLayout.getMapComponent().onMapEdited(utilities.getMapPoints());
			utilities.updatePinColors();
		});
		
		MyUI.getYesNoWindow().addNoButtonClickListener(e -> {
			MyUI.getYesNoWindow().close();
		});
		
		MyUI.getYesNoWindow().showWindow();
	}

}
