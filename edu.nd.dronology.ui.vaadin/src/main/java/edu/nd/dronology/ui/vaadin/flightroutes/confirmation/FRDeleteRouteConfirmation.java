package edu.nd.dronology.ui.vaadin.flightroutes.confirmation;

import java.rmi.RemoteException;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This class defines the window that asks the user if they want to delete a specified route.
 * 
 * @author James Holland
 */

public class FRDeleteRouteConfirmation {
	private FRMainLayout mainLayout = null;
	
	public FRDeleteRouteConfirmation(FRMainLayout mainLayout){
		this.mainLayout = mainLayout;
	}
	
	public void showWindow (FlightRouteInfo routeInfoTobeDeleted){
		MyUI.getYesNoWindow().initForNewMessage(
				"Are you sure you want to delete the route <b>" + routeInfoTobeDeleted.getName() + "</b>?");
		
		// Click listeners for yes and no buttons on window.
		MyUI.getYesNoWindow().addYesButtonClickListener(e -> {
			MyUI.getYesNoWindow().close();
			
			// Only delete if the route to be deleted has been set.
			if (routeInfoTobeDeleted != null) {
				deleteRoute(routeInfoTobeDeleted);
			}
			
			if (mainLayout.getMap().getSelectedRoute().getId().equals(routeInfoTobeDeleted.getId())) {
				mainLayout.getMap().exitEditMode();
				mainLayout.getMap().displayNoRoute();	
				mainLayout.getMap().getMainLayout().deleteRouteUpdate();
			}

			// Refreshes routes immediately after the "yes" on window is clicked.
			mainLayout.getControls().getInfoPanel().refreshRoutes();
		});
		
		MyUI.getYesNoWindow().addNoButtonClickListener(e -> {
			MyUI.getYesNoWindow().close();
		});
		
		MyUI.getYesNoWindow().showWindow();
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
