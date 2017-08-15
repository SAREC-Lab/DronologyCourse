package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMapComponent;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;
import edu.nd.dronology.ui.vaadin.utils.YesNoWindow;

@SuppressWarnings("serial")
public class FRDeleteWayPointConfirmation extends YesNoWindow {
	private FRMapComponent mapComponent = null;
	public FRDeleteWayPointConfirmation(FRMapComponent mapComponent){
		this.mapComponent = mapComponent;
	}
	
	public void showWindow (Event deleteWaypointClickEvent) {
		this.initForNewMessage(
				"Are you sure you want to delete this waypoint?");
		
		// Click listeners for yes and no buttons on window.
		this.addYesButtonClickListener(e -> {
			this.close();
			MapMarkerUtilities mapUtilities = mapComponent.getMapUtilities();
			
			String waypointID = "";
			
			if (deleteWaypointClickEvent.getClass().equals(RendererClickEvent.class)) {
				//clicked remove waypoint on the table
				RendererClickEvent<?> event = (RendererClickEvent<?>)deleteWaypointClickEvent;
				waypointID = ((UIWayPoint)event.getItem()).getId();
			} else {
				//clicked remove waypoint from the mouse over popup view
				waypointID = mapUtilities.getMarkerMouseOverListener().getSelectedWayPointId();
			}
			
			mapUtilities.removePinById(waypointID);
			mapComponent.updateWayPointCount(mapUtilities);
		});
		
		this.addNoButtonClickListener(e -> {
			this.close();
		});
		
		this.showWindow();
	}

}
