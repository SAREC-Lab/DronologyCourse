package edu.nd.dronology.ui.vaadin.flightroutes;

import java.awt.MouseInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
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
	private FRNewRoute display;
	private TextField inputField;
	private FRInfoBox routeBox;
	private ArrayList<FRInfoBox> boxList = new ArrayList();
	private FRControlsComponent controlComponent;
	private Button newRoute;
	private Window window;
	private TextField descriptionField;
	private String routeDescription;
	
	FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
	ByteArrayInputStream inStream;
	
	public FRInfoPanel(FRControlsComponent controls) {
		controlComponent = controls;
				
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		try {

			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			items = service.getItems();
			routeList = new ArrayList(items);

			String id;
			String name;
			String dateModified;
			String dateCreated;
			String length;

			// gets routes from dronology and requests their name/id
			for (FlightRouteInfo e : items) {
				id = e.getId();
				name = e.getName();
				dateModified = String.valueOf(e.getDateCreated());
				dateCreated = String.valueOf(e.getDateModified());
				length = String.valueOf(e.getLenght());
				
				byte[] information = service.requestFromServer(id);
				inStream = new ByteArrayInputStream(information);
				try {
					route = routePersistor.loadItem(inStream);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}

				addRoute(name, id, dateCreated, dateModified, length);
				refreshRoutes();
			}

		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
		}
		
		//top bar of panel
		panel.setCaption(numberRoutes + " Routes in database");
		panel.setContent(totalLayout);
		panel.addStyleName("fr_info_panel");
		panel.addStyleName("control_panel");

		newRoute = new Button("+ Add a new route");
		newRoute.addStyleName("fr_new_route_button");

		VerticalLayout popupContent = new VerticalLayout();

		//popup box to input new route info
		display = new FRNewRoute();
		popupContent.addComponent(display);
		PopupView popup = new PopupView(null, popupContent);
		
		window = new Window();
		window.setContent(display);
		window.setPosition(200, 80);
		window.setResizable(false);
		window.setClosable(false);
		
		drawButton = display.getDrawButton();
		inputField = display.getInputField();
		descriptionField = display.getDescriptionField();
		
		drawButton.addClickListener(e -> {		
		
			routeInputName = inputField.getValue();
			routeDescription = descriptionField.getValue();
			
			if(!routeInputName.isEmpty()){
			
				//sends route to dronology
				drone = addRouteDronology(routeInputName, routeDescription);
				
				//because dronology takes some time
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				refreshRoutes();
				
				panel.setCaption(numberRoutes + " Routes in database");
				
				index = getRouteNumber(drone);
				routes.getComponent(index).addStyleName("info_box_focus");	
				
				inputField.clear();
				descriptionField.clear();
				UI.getCurrent().removeWindow(window);
		
				controls.getLayout().drawRoute();
				panel.setCaption(String.valueOf(routes.getComponentCount()) + " routes in database");
				
			}
		});
		
		newRoute.addClickListener(e -> {
			UI.getCurrent().addWindow(window);
		});
		
		display.getCancelButton().addClickListener(e-> {
			UI.getCurrent().removeWindow(window);
		});

		routes.addLayoutClickListener(e -> {
			isRouteSelected = true;
		});
		
		for(FRInfoBox infoBox: boxList){
			infoBox.getEditButton().addClickListener(e->{
				controls.getLayout().editClick(infoBox);
			});
		}

		routeBox.getTrashButton().addClickListener(e->{
			controls.getLayout().getMap().displayNoRoute();
		});
		
		buttons.addComponents(newRoute, popup);
		buttons.addStyleName("fr_new_route_button_area");
		totalLayout.addComponents(buttons, routes);
		
		setCompositionRoot(panel);
	}

	public void addRoute() {
		routeBox = new FRInfoBox(this);
		routes.addComponent(routeBox);
		boxList.add(routeBox);
		numberRoutes += 1;
	}

	public void addRoute(String name, String ID, String created, String modified, String length) {
		
		routeBox = new FRInfoBox(name, ID, created, modified, length, this);
		routes.addComponent(routeBox);
		boxList.add(routeBox);
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
	public FlightRouteInfo addRouteDronology(String name, String description){
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
			froute.setDescription(description);
			
			
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
			
			panel.setCaption(routeList.size() + " Routes in database");
			
			for (FlightRouteInfo e : items) {
				String id = e.getId();
				String name = e.getName();
				long creationTime = e.getDateCreated();
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa");
				String creationFormatted = sdf.format(new Date(creationTime));
				
				long modifiedTime = e.getDateModified();
				String modifiedFormatted = sdf.format(new Date(modifiedTime));
				
				String length = String.valueOf(e.getLenght());
				addRoute(name, id, creationFormatted, modifiedFormatted, length);
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
	public FRNewRoute getDisplay(){
		return display;
	}
	public TextField getInputField(){
		return inputField;
	}
	public ArrayList getRouteList(){
		return routeList;
	}
	public FRInfoBox getInfoBox(){
		return routeBox;
	}
	public FRInfoBox getInfoBoxIndex(int index){
		
		return (FRInfoBox) routes.getComponent(index);
	}
	public ArrayList<FRInfoBox> getBoxList(){
		return boxList;
	}
	public FRControlsComponent getControls(){
		return controlComponent;
	}
	public HorizontalLayout getButtonLayout(){
		return buttons;
	}
	public VerticalLayout getTotalLayout(){
		return totalLayout;
	}
	public Button getNewRouteButton(){
		return newRoute;
	}
	public void removeWindow(){
		UI.getCurrent().removeWindow(window);
	}
	
}