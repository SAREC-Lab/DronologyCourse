package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoPanel;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This is the menu that allows the user to add a new route with a specified  name and description.
 * 
 * @author James Holland
 */

@SuppressWarnings("serial")
public class FRNewRouteWindow extends Window {
	public FRNewRouteWindow(FRInfoPanel infoPanel) {
		VerticalLayout totalLayout = new VerticalLayout();
		
		Label directions = new Label("Please enter a route name");
		Label description = new Label("Route Description");
		TextArea descriptionField = new TextArea();
		TextField nameField = new TextField();

		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button cancelButton = new Button("Cancel");
		Button drawButton = new Button("Draw Route");
		buttonLayout.addComponents(cancelButton, drawButton);
		
		totalLayout.addComponents(directions, nameField, description, descriptionField, buttonLayout);
		
		this.addStyleName("fr_add_route_layout");
		buttonLayout.addStyleName("confirm_button_area");
		drawButton.addStyleName("btn-okay");
		
		this.setContent(totalLayout);
		this.addStyleName("confirm_window");
		this.setPosition(200, 80);
		this.setResizable(false);
		this.setClosable(false);
		
		// Click listener for when the user creates a new route.
		drawButton.addClickListener(e -> {
			String routeInputName = nameField.getValue();
			String routeDescription = descriptionField.getValue();
			
			if(!routeInputName.isEmpty()){
				UI.getCurrent().removeWindow(this);
				
				// Before the listener works... use this to check if the dronology have updated the route list
				Collection<FlightRouteInfo> allFlightsInDronologyBefore = infoPanel.getRoutesFromDronology();
	
				// Sends route to dronology.
				FlightRouteInfo addedRoute = addRouteToDronology(routeInputName, routeDescription);
				
				infoPanel.getControls().getMainLayout().getWaitingWindow().showWindow(
						"Creating a new route...",
						() -> {
							return allFlightsInDronologyBefore.size() != infoPanel.getRoutesFromDronology().size();
						},
						closeEvent -> {
							// Dronology finally updates the route
							infoPanel.refreshRoutes();		

							nameField.clear();
							descriptionField.clear();
							
							// Switch to the newly added route and enter edit mode
							infoPanel.getControls().getMainLayout().switchRoute(infoPanel.getRouteInfoBox(addedRoute));
							infoPanel.getControls().getMainLayout().getMapComponent().getEditModeController().enterEditMode();
						});
			}
		});
		// Removes route creation window on cancel.
		cancelButton.addClickListener(e -> {
			UI.getCurrent().removeWindow(this);
		});
	}
	
	// Creates a new route in Dronology and returns the FlightRouteInfo object.
	public FlightRouteInfo addRouteToDronology(String name, String description) {
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		FlightRouteInfo routeInformation = null;
			
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
				
			// Creates FlightRouteInfo object and gets its id.
			FlightRouteInfo newRoute = service.createItem();
			String id = newRoute.getId();
			IFlightRoute froute;
				
			// Sets IFlightRoute information based on name and description.
			byte[] information = service.requestFromServer(id);
			ByteArrayInputStream inStream = new ByteArrayInputStream(information);
			froute = FlightRoutePersistenceProvider.getInstance().loadItem(inStream);
			froute.setName(name);
			froute.setDescription(description);
				
			// Loads the information back to Dronology.
			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			FlightRoutePersistenceProvider.getInstance().saveItem(froute, outs);
			byte[] bytes = outs.toByteArray();
			service.transmitToServer(froute.getId(), bytes);	
			routeInformation = newRoute;	
		} catch (RemoteException | DronologyServiceException | PersistenceException e) {
			e.printStackTrace();
		}
		return routeInformation;
	}
}
