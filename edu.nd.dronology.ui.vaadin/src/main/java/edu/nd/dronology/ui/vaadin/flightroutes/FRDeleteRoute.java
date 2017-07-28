package edu.nd.dronology.ui.vaadin.flightroutes;

import java.rmi.RemoteException;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This class defines the window that asks the user if they want to delete a specified route
 * 
 * @author James Holland
 */

public class FRDeleteRoute extends CustomComponent{

	private static final long serialVersionUID = 6787319301316969492L;

	private Button yesButton = new Button("Yes");
	private Button noButton = new Button("No");
	private Label question = new Label("Are you sure you want to delete this route?");
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private VerticalLayout totalLayout = new VerticalLayout();
	private Window window = new Window();
	private FlightRouteInfo infoTobeDeleted = null;
	private FRMapComponent mapComp;
	
	public FRDeleteRoute(FRMapComponent mapComp){
	
		buttonLayout.addComponents(yesButton, noButton);
		totalLayout.addComponents(question, buttonLayout);
		
		window.setContent(totalLayout);
		window.setResizable(false);
		window.setClosable(false);
		window.setPosition(800, 200);
		
		noButton.addClickListener(e->{
			window.close();
		});
		yesButton.addClickListener(e->{
			window.close();
			mapComp.exitEditMode();
			
			if (infoTobeDeleted != null) {
				deleteRoute(infoTobeDeleted);
				infoTobeDeleted = null;
			}
			
			mapComp.displayNoRoute();
			
			mapComp.getMainLayout().deleteRouteUpdate();
		});
		
	}
	public FRDeleteRoute(){
		buttonLayout.addComponents(yesButton, noButton);
		totalLayout.addComponents(question, buttonLayout);
		
		//setCompositionRoot(totalLayout);
		
		window.setContent(totalLayout);
		window.setResizable(false);
		window.setClosable(false);
		window.setPosition(800, 200);
		
		noButton.addClickListener(e->{
			window.close();
		});
		
		yesButton.addClickListener(e->{
			window.close();
			if (infoTobeDeleted != null) {	
				deleteRoute(infoTobeDeleted);
				infoTobeDeleted = null;
			}
			
		});	
	}
	public Button getYesButton(){
		return yesButton;
	}
	public Button getNoButton(){
		return noButton;
	}
	public Window getWindow() {
		return window;
	}
	
	public void setRouteInfoTobeDeleted (FlightRouteInfo infoTobeDeleted) {
		this.infoTobeDeleted = infoTobeDeleted;
	}

	public void deleteRoute(FlightRouteInfo routeinfo){
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
	
			try {
				service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
						.getService(IFlightRouteplanningRemoteService.class);
				//String id = routeinfo.getId();
				service.deleteItem(routeinfo.getId());
			} catch (RemoteException | DronologyServiceException e) {
				e.printStackTrace();
			}
	}
	public void deleteAnyway(){
		if(infoTobeDeleted != null){
			deleteRoute(infoTobeDeleted);
		}
	}
}
