package edu.nd.dronology.ui.vaadin.flightroutes;

import java.rmi.RemoteException;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

public class FRDeleteRoute extends CustomComponent{

	private static final long serialVersionUID = 6787319301316969492L;

	Button yesButton = new Button("Yes");
	Button noButton = new Button("No");
	Label question = new Label("Are you sure you want to delete this route?");
	HorizontalLayout buttonLayout = new HorizontalLayout();
	VerticalLayout totalLayout = new VerticalLayout();
	Window window = new Window();
	FlightRouteInfo infoTobeDeleted = null;
	int counter = 0;
	
	FRMapComponent mapComp;
	
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
			
			System.out.println(String.valueOf(counter));
			counter++;
			
			if (infoTobeDeleted != null) {
				deleteRoute(infoTobeDeleted);
				infoTobeDeleted = null;
			}

			mapComp.displayNoRoute();
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
}
