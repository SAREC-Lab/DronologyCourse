package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRDeleteRouteConfirmation;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRDeleteWayPointConfirmation;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRUnsavedChangesConfirmation;
import edu.nd.dronology.ui.vaadin.utils.WaitingWindow;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the main layout for the Flight Routes UI.
 * 
 * @author Jinghui Cheng
 */

@SuppressWarnings("serial")
public class FRMainLayout extends CustomComponent {
	private FRControlsComponent controls = new FRControlsComponent(this);
	private FRMapComponent mapComponent;
	
	private FRDeleteRouteConfirmation deleteRouteConfirmation;
	private FRUnsavedChangesConfirmation unsavedChangesConfirmation;
	private FRDeleteWayPointConfirmation deleteWayPointConfirmation;
	private WaitingWindow waitingWindow = new WaitingWindow();

	@WaypointReplace
	public FRMainLayout() {
		addStyleName("main_layout");
		CssLayout content = new CssLayout();
		content.setSizeFull();

		mapComponent = new FRMapComponent("VAADIN/sbtiles/{z}/{x}/{y}.png", "South Bend",
				"VAADIN/sateltiles/{z}/{x}/{y}.png", "Satellite", this);
				
		mapComponent.setCenter(41.68, -86.25);
		mapComponent.setZoomLevel(13);
		
		deleteRouteConfirmation = new FRDeleteRouteConfirmation(this);
		unsavedChangesConfirmation = new FRUnsavedChangesConfirmation(this);
		deleteWayPointConfirmation = new FRDeleteWayPointConfirmation(this);
		
		content.addComponents(controls, mapComponent);
		setCompositionRoot(content);
	}
	
	public FRDeleteRouteConfirmation getDeleteRouteConfirmation() {
		return deleteRouteConfirmation;
	}

	public FRUnsavedChangesConfirmation getUnsavedChangesConfirmation() {
		return unsavedChangesConfirmation;
	}

	public FRDeleteWayPointConfirmation getDeleteWayPointConfirmation() {
		return deleteWayPointConfirmation;
	}

	public WaitingWindow getWaitingWindow() {
		return waitingWindow;
	}
	
	// Displays the route that is clicked. Passes in the click event, map, and infobox that was clicked.
	public void switchRoute(FRInfoBox switchToInfoBox) {
		// When one route is clicked, the others go back to default background color.
		controls.getInfoPanel().unhighlightAllInfoBoxes();
		controls.getInfoPanel().highlightInfoBox(
				controls.getInfoPanel().getRouteIndex(switchToInfoBox.getFlightRouteInfo()));
		
		// Displays the route on map.
		mapComponent.displayFlightRoute(switchToInfoBox.getFlightRouteInfo());
	}
	// Gets the controls component that holds the infoPanel and mainLayout.
	public FRControlsComponent getControls() {
		return controls;
	}
	// Gets the currently displayed map.
	public FRMapComponent getMapComponent() {
		return mapComponent;
	}
	// Describes what should happen when the user clicks on the edit button of a specific box (basically switches to that window and enables editing).
	public void switchAndEdit(FRInfoBox infoBox) {
		switchRoute(infoBox);
		mapComponent.enableEdit();
	}
}