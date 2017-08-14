package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import java.rmi.RemoteException;

import com.vaadin.ui.AbstractComponent;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.YesNoWindow;

/**
 * This class defines the window that asks the user if they want to delete a specified route.
 * 
 * @author James Holland
 */

@SuppressWarnings("serial")
public class FRDeleteRouteConfirmation extends YesNoWindow {
	private FRMainLayout mainLayout = null;
	
	public FRDeleteRouteConfirmation(FRMainLayout mainLayout){
		this.mainLayout = mainLayout;
	}
	
	public void showWindow (FlightRouteInfo routeTobeDeleted, Event externalEvent){
		this.initForNewMessage(
				"Are you sure you want to delete the route <b>" + routeTobeDeleted.getName() + "</b>?");
		
		//Switch to the to be deleted route before showing the window
		if (externalEvent != null) {
			AbstractComponent c = (AbstractComponent)externalEvent.getComponent();
			if (c.findAncestor(FRInfoBox.class) != null) {
				mainLayout.switchRoute((FRInfoBox)c.findAncestor(FRInfoBox.class));
			}
		}
		
		// Click listeners for yes and no buttons on window.
		this.addYesButtonClickListener(e -> {
			this.close();
			
			// Only delete if the route to be deleted has been set.
			if (routeTobeDeleted != null) {
				deleteRoute(routeTobeDeleted);
			}
			
			if (routeTobeDeleted.getId().equals(mainLayout.getControls().getInfoPanel().getHighlightedFRInfoBox().getId())) {
				mainLayout.getMapComponent().exitEditMode();
				mainLayout.getMapComponent().displayNoRoute();	
				mainLayout.getControls().getInfoPanel().refreshRoutes();
			}

			// Refreshes routes immediately after the "yes" on window is clicked.
			mainLayout.getControls().getInfoPanel().refreshRoutes();
		});
		
		this.addNoButtonClickListener(e -> {
			this.close();
		});
		
		this.showWindow();
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
}
