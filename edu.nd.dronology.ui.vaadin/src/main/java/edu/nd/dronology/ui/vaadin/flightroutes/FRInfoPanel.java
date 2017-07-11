package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
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
	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout routes = new VerticalLayout();
	private HorizontalLayout buttons = new HorizontalLayout();
	private String routeInputName;
	private IFlightRoute route;
	private int index;
	private ArrayList routeList;
	private FlightRouteInfo flight;
	private boolean isRouteSelected = false;
	private Button drawButton;
	private Collection<FlightRouteInfo> items;
	private FlightRouteInfo drone;
	
	FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
	ByteArrayInputStream inStream;
	
	public FRInfoPanel() {

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		try {

			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			items = service.getItems();
			routeList = new ArrayList(items);

			String id;
			String name;

			// gets routes from dronology and requests their name/id
			for (FlightRouteInfo e : items) {
				id = e.getId();
				name = e.getName();
				
				byte[] information = service.requestFromServer(id);
				inStream = new ByteArrayInputStream(information);
				try {
					route = routePersistor.loadItem(inStream);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}

				addRoute(name, id, "Jun 5, 2017, 2:04AM", "Jun 7, 2017, 3:09AM", "10mi");
			}

		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
		}
		
		//top bar of panel
		panel.setCaption(numberRoutes + " Routes in database");
		panel.setContent(totalLayout);
		panel.addStyleName("fr_info_panel");
		panel.addStyleName("control_panel");

		Button newRoute = new Button("+ Add a new route");
		newRoute.addStyleName("fr_new_route_button");

		VerticalLayout popupContent = new VerticalLayout();

		//popup box to input new route info
		FRNewRoute display = new FRNewRoute();
		popupContent.addComponent(display);
		PopupView popup = new PopupView(null, popupContent);
		
		drawButton = display.getDrawButton();
		TextField inputField = display.getInputField();
		drawButton.addClickListener(e -> {
			
			routeInputName = inputField.getValue();
			addRoute(routeInputName, "41323", "Mar 19, 2015, 4:32PM", "Jul 12, 2016, 7:32AM", "5.1mi"); 
			
			//sends route to dronology
			drone = addRouteDronology(routeInputName);
			
			//because dronology takes some time
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			refreshRoutes();
			//Notification.show(String.valueOf(numberRoutes));
			panel.setCaption(numberRoutes + " Routes in database");
			
			index = getRouteNumber(drone);
			routes.getComponent(index).addStyleName("info_box_focus");			
						
		});

		newRoute.addClickListener(e -> {
			popup.setPopupVisible(true);
		});

		routes.addLayoutClickListener(e -> {
			isRouteSelected = true;
		});

		buttons.addComponents(newRoute, popup);
		buttons.addStyleName("fr_new_route_button_area");
		totalLayout.addComponents(buttons, routes);

		setCompositionRoot(panel);
	}

	public void addRoute() {
		FRInfoBox route = new FRInfoBox();
		routes.addComponent(route);
		numberRoutes += 1;
	}

	public void addRoute(String name, String ID, String created, String modified, String length) {
		FRInfoBox route = new FRInfoBox(name, ID, created, modified, length);
		routes.addComponent(route);
		numberRoutes += 1;
	}
	public boolean removeBox(String name) {
		for (int i = 0; i < numberRoutes; i++) {
			FRInfoBox route = (FRInfoBox) routes.getComponent(i);
			if (route.getName().equals(name)) {
				routes.removeComponent(route);
				return true;
			}
		}
		return false;
	}
	public VerticalLayout getRoutes() {
		return routes;
	}
	public FlightRouteInfo getFlight(int index) {
		//gets arrayList of different routes and returns the one specified by index
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		
		Collection<FlightRouteInfo> items;
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			items = service.getItems();
			routeList = new ArrayList(items);
			
			if(index == routeList.size()){
				index--;
			}
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		if(index != -1){
			flight = (FlightRouteInfo) routeList.get(index);
		}
		
		return flight;
	}

	public boolean getIsRouteSelected() {
		return isRouteSelected;
	}

	public void setIsRouteSelected(boolean selected) {
		isRouteSelected = selected;
	}
	public FlightRouteInfo addRouteDronology(String name){
		//sends a route to dronology to be saved, and returns the flight route info
		
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		FlightRouteInfo routeInformation = null;
		
		try {
			
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			
			FlightRouteInfo newRoute = service.createItem();
			String id = newRoute.getId();
			IFlightRoute froute;
				
			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);
			froute.setName(name);
			
			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			routePersistor.saveItem(froute, outs);
			byte[] bytes = outs.toByteArray();
			service.transmitToServer(froute.getId(), bytes);	
			routeInformation = newRoute;	
			
		} catch (RemoteException | DronologyServiceException | PersistenceException e) {
			e.printStackTrace();
		}
		
		return routeInformation;
	}
	public Button getDrawButton(){
		return drawButton;
	}
	public void refreshRoutes(){
		//makes sure routes are updated by removing and re-adding routes
		routes.removeAllComponents();
		
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> items = service.getItems();
			routeList = new ArrayList(items);
			
			for (FlightRouteInfo e : items) {
				String id = e.getId();
				String name = e.getName();
				addRoute(name, id, "Jun 5, 2017, 2:04AM", "Jun 7, 2017, 3:09AM", "10mi");
				numberRoutes--;
			}
			
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		

	}
	public int getRouteNumber(FlightRouteInfo info){
		//given the route information, returns the index of the route
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> items = service.getItems();
			routeList = new ArrayList(items);
			
			int counter = 0;
			for (FlightRouteInfo e : items) {
				if(info.equals(e)){
					return counter;		
				}
				counter++;
			}	
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		return 0;
	}
public FlightRouteInfo getRouteByName(String name){
		//given the name of the route, returns the route info
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> items = service.getItems();
			routeList = new ArrayList(items);			
			
			for (FlightRouteInfo e : items) {
				if(e.getName().equals(name)){
					return e;			
				}			
			}
			
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		FlightRouteInfo empty = null;
		return empty;
	}
	public FlightRouteInfo getRoute(){
		return drone;		
	}
	public String getName(){
		return routeInputName;
	}
	
}