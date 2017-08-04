package edu.nd.dronology.ui.vaadin.flightroutes;

import java.rmi.RemoteException;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This class defines the window that asks the user if they want to delete a specified route.
 * 
 * @author James Holland
 */

public class FRDeleteRoute extends CustomComponent{
	private static final long serialVersionUID = 6787319301316969492L;

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private VerticalLayout totalLayout = new VerticalLayout();
	private Window window = new Window();
	private Label question = new Label("Are you sure you want to delete this route?");
	private Button yesButton = new Button("Yes");
	private Button noButton = new Button("No");
	private FlightRouteInfo infoTobeDeleted = null;
	
	public FRDeleteRoute(FRMapComponent mapComp){
	
		buttonLayout.addComponents(yesButton, noButton);
		totalLayout.addComponents(question, buttonLayout);
		
		window.addStyleName("confirm_window");
		buttonLayout.addStyleName("confirm_button_area");
		yesButton.addStyleName("btn-danger");
		
		window.setContent(totalLayout);
		window.setResizable(false);
		window.setClosable(false);
		window.setPosition(800, 200);
		
		// Click listeners for yes and no buttons on window.
		yesButton.addClickListener(e -> {
			window.close();
			mapComp.exitEditMode();
			
			// Only delete if the route to be deleted has been set.
			if (infoTobeDeleted != null) {
				deleteRoute(infoTobeDeleted);
				infoTobeDeleted = null;
			}
			mapComp.displayNoRoute();	
			mapComp.getMainLayout().deleteRouteUpdate();
		});
		
		noButton.addClickListener(e -> {
			window.close();
		});
	}
	// Used when the delete button in one of the infoboxes is clicked, as there is no mapComponent object to be passed (route to be deleted is set in FRMapComponent).
	public FRDeleteRoute(){
		buttonLayout.addComponents(yesButton, noButton);
		totalLayout.addComponents(question, buttonLayout);
		
		window.addStyleName("confirm_window");
		buttonLayout.addStyleName("confirm_button_area");
		yesButton.addStyleName("btn-danger");
		
		window.setContent(totalLayout);
		window.setResizable(false);
		window.setClosable(false);
		window.setPosition(800, 200);

		yesButton.addClickListener(e -> {
			window.close();
			if (infoTobeDeleted != null) {	
				deleteRoute(infoTobeDeleted);
				infoTobeDeleted = null;
			}	
		});	
		
		noButton.addClickListener(e -> {
			window.close();
		});
	}
	// Deletes a route from Dronology based on the FlightRouteInfo.
	public void deleteRoute(FlightRouteInfo routeinfo) {
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
			
			try {
				service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
						.getService(IFlightRouteplanningRemoteService.class);
				service.deleteItem(routeinfo.getId());
			} catch (RemoteException | DronologyServiceException e) {
				e.printStackTrace();
			}
	}
	// Deletes the currently selected route.
	public void deleteAnyway() {
		if(infoTobeDeleted != null) {
			deleteRoute(infoTobeDeleted);
		}
	}
	// Allows for FlightRouteInfo to be passed in from other files and deleted (FlightRouteInfo object may not be available from where click listener is defined).
	public void setRouteInfoTobeDeleted (FlightRouteInfo infoTobeDeleted) {
		this.infoTobeDeleted = infoTobeDeleted;
	}
	public Button getYesButton() {
		return yesButton;
		// Returns the yes button so a click listener can be added to it based on the route with which it is associated.
	}
	public Button getNoButton() {
		return noButton;
		// Returns the no button so a click listener can be added to it based on the route with which it is associated.
	}
	public Window getWindow() {
		return window;
		// Returns the window so it can be added to/deleted from the current UI.
	}
}
