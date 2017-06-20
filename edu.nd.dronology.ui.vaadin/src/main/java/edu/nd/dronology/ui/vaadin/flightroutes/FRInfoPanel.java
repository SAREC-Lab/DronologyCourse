package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * 
 * This is the list of selectable flight routes 
 * 
 * @author jhollan4
 *
 */

public class FRInfoPanel extends CustomComponent {

	private static final long serialVersionUID = -2505608328159312876L;
	
	private int numberRoutes;
	private Panel panel = new Panel();
	
	//total layout is made up of the title/buttons on top and the routes on the bottom
	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout routes = new VerticalLayout();
	private HorizontalLayout buttons = new HorizontalLayout();
	private String routeInputName;
	
	FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
	ByteArrayInputStream inStream;

	private IFlightRoute route;
	
	public FRInfoPanel(){
		
		  IFlightRouteplanningRemoteService service;
		  BaseServiceProvider provider = MyUI.getProvider();
		 
		  try {
				
			    service = (IFlightRouteplanningRemoteService) provider.getRemoteManager().getService(IFlightRouteplanningRemoteService.class);
				
				Collection<FlightRouteInfo> items = service.getItems();
				
				String id; 
				String name;
				
				//gets routes from dronology and requests their name/id
				for(FlightRouteInfo e: items){
					id = e.getId();
					name = e.getName();
					addRoute(name, id, "Jun 5, 2017, 2:04AM", "Jun 7, 2017, 3:09AM", "10mi");
					
				}
				
			} catch (DronologyServiceException | RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		panel.setCaption(numberRoutes + " Routes in database");
		panel.setContent(totalLayout);
		panel.addStyleName("fr_info_panel");
		panel.addStyleName("control_panel");
		
		Button newRoute = new Button("+ Add a new route");
		newRoute.addStyleName("fr_new_route_button");
		
		VerticalLayout popupContent = new VerticalLayout();
		
		TextField inputField = new TextField();
		inputField.addValueChangeListener(e -> {
			routeInputName = inputField.getValue();
			Notification.show(routeInputName);
			addRoute(routeInputName, "41342", "Mar 19, 2015, 4:32PM", "Jul 12, 2016, 7:32AM", "5.1mi");
		});
		
		popupContent.addComponent(inputField);
		
		PopupView popup = new PopupView(null, popupContent);
		buttons.addComponent(popup); 
		
		newRoute.addClickListener(e->{	
			popup.setPopupVisible(true);
			
		});
		
		buttons.addComponents(newRoute, popup); 
		
//		Button filter = new Button("Filter");
//		filter.setWidth("68px");
//		
//		buttons.addComponents(filter);
		
		totalLayout.addComponents(buttons, routes);
		
		setCompositionRoot(panel);
		
	}
	
	public void addRoute(){
		FRInfoBox route = new FRInfoBox();
		routes.addComponent(route);
		numberRoutes += 1;
	}
	
	public void addRoute(String name, String ID, String created, String modified, String length){
		FRInfoBox route = new FRInfoBox(name, ID, created, modified, length);
		routes.addComponent(route);
		numberRoutes += 1;
	}
	
	public boolean removeBox(String name){
		for(int i = 0; i < numberRoutes; i++){
			FRInfoBox route = (FRInfoBox) routes.getComponent(i);
			if(route.getName().equals(name)){
				routes.removeComponent(route);
				return true;
			}
		}
		return false;
	}

}