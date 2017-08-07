package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
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
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This is the list of selectable flight routes.
 * 
 * @author James Holland
 *
 */

public class FRInfoPanel extends CustomComponent {

	private static final long serialVersionUID = -2505608328159312876L;

	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout routeListLayout = new VerticalLayout();
	private HorizontalLayout buttons = new HorizontalLayout();
	private int numberRoutes;
	private int index;
	private Panel topPanel = new Panel();
	private String routeInputName;
	private Collection<FlightRouteInfo> allFlights;
	private List<FlightRouteInfo> routeList;
	private FlightRouteInfo flight;
	private FlightRouteInfo addedRoute;
	private boolean isRouteSelected = false;
	private Button drawButton;
	private Button cancelButton;
	private FRNewRoute newRouteDisplay;
	private TextField nameField;
	private FRInfoBox routeBox;
	private ArrayList<FRInfoBox> boxList = new ArrayList<>();
	private FRControlsComponent controlComponent;
	private Button newRoute;
	private Window newRouteWindow;
	private TextArea descriptionField;
	private String routeDescription;
	private FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
	private ByteArrayInputStream inStream;
	
	public FRInfoPanel(FRControlsComponent controls) {
		controlComponent = controls;
		
		// Gets service and provider from Dronology.
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			// Gets and creates an ArrayList of FlightRouteInfo items.
			allFlights = service.getItems();
			routeList = new ArrayList<>(allFlights);

			String id;
			String name;
			String dateModified;
			String dateCreated;
			String length;

			// Iterates through the FlightRouteInfo objects and gets the fields from each.
			for (FlightRouteInfo e : allFlights) {
				id = e.getId();
				name = e.getName();
				dateModified = String.valueOf(e.getDateCreated());
				dateCreated = String.valueOf(e.getDateModified());
				length = String.valueOf(e.getLenght());
				
				// Uses byte stream to load from server.
				byte[] information = service.requestFromServer(id);
				inStream = new ByteArrayInputStream(information);
				try {
					routePersistor.loadItem(inStream);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}

				// Adds the correct infoBox to the infoPanel.
				addRoute(name, id, dateCreated, dateModified, length);
				refreshRoutes();
			}

		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
		}
		
		// Top bar of panel.
		topPanel.setCaption(numberRoutes + " Routes in database");
		topPanel.setContent(totalLayout);
		topPanel.addStyleName("fr_info_panel");
		topPanel.addStyleName("control_panel");

		newRoute = new Button("+ Add a new route");
		newRoute.addStyleName("fr_new_route_button");

		// Box to input new route info.
		newRouteDisplay = new FRNewRoute();
		newRouteWindow = new Window();
		
		newRouteWindow.addStyleName("confirm_window");
		
		newRouteWindow.setContent(newRouteDisplay);
		newRouteWindow.setPosition(200, 80);
		newRouteWindow.setResizable(false);
		newRouteWindow.setClosable(false);
		
		// Gets the buttons on the new route window.
		drawButton = newRouteDisplay.getDrawButton();
		nameField = newRouteDisplay.getInputField();
		cancelButton = newRouteDisplay.getCancelButton();
		descriptionField = newRouteDisplay.getDescriptionField();
		
		// Click listener for when the user creates a new route.
		drawButton.addClickListener(e -> {
			routeInputName = nameField.getValue();
			routeDescription = descriptionField.getValue();
			
			if(!routeInputName.isEmpty()){	
				// Sends route to dronology.
				addedRoute = addRouteDronology(routeInputName, routeDescription);
				
				// This timer is here because dronology takes some time to create a new route.
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				refreshRoutes();		
	
				// Shows the newly created route as selected.
				index = getRouteNumber(addedRoute);
				routeListLayout.getComponent(index).addStyleName("info_box_focus");	
				
				nameField.clear();
				descriptionField.clear();
				UI.getCurrent().removeWindow(newRouteWindow);
				controls.getMainLayout().drawRoute();
				topPanel.setCaption(String.valueOf(routeListLayout.getComponentCount()) + " routes in database");
			}
		});
		// Displays the route creation window.
		newRoute.addClickListener(e -> {
			UI.getCurrent().addWindow(newRouteWindow);
		});
		// Removes route creation window on cancel.
		cancelButton.addClickListener(e -> {
			UI.getCurrent().removeWindow(newRouteWindow);
		});
		// If the vertical layout is clicked, then a route is assumed to be selected.
		routeListLayout.addLayoutClickListener(e -> {
			isRouteSelected = true;
		});
		// Iterates through the infoboxes and adds a click listener to each of the edit buttons.
		for (FRInfoBox infoBox: boxList) {
			infoBox.getEditButton().addClickListener(e -> {
				if (!controlComponent.getMainLayout().getMap().getUtilities().isEditable()) {
					controls.getMainLayout().editClick(infoBox);
				}
			});
		}
		buttons.addComponents(newRoute);
		buttons.addStyleName("fr_new_route_button_area");
		
		totalLayout.addComponents(buttons, routeListLayout);
		
		setCompositionRoot(topPanel);
	}
	// Creates a new route in Dronology and returns the FlightRouteInfo object.
	public FlightRouteInfo addRouteDronology(String name, String description) {
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
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);
			froute.setName(name);
			froute.setDescription(description);
				
			// Loads the information back to Dronology.
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
	// Ensures routes are updated by removing and re-adding routes.
	public void refreshRoutes() {
		routeListLayout.removeAllComponents();
			
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
			
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> allFlights = service.getItems();
			routeList = new ArrayList<>(allFlights);
				
			topPanel.setCaption(routeList.size() + " Routes in database");
				
			// Iterates through the routes, gets the fields of each, and creates an infobox.
			for (FlightRouteInfo e : allFlights) {
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
	// Gets FlightRouteInfo from Dronology based on route index.
	public FlightRouteInfo getFlightRouteInfo(int index) {
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
			
		Collection<FlightRouteInfo> allFlights;
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			allFlights = service.getItems();
			routeList = new ArrayList<>(allFlights);
				
			if (index == routeList.size()) {
				index--;
			}
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		// Ensures that an infobox was clicked rather than inbetween the boxes.
		if (index != -1) {
			flight = routeList.get(index);
		}
		return flight;
	}
	// Gets the route number based on the FlightRouteInfo.
	public int getRouteNumber(FlightRouteInfo info) {
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
			
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
						.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> allFlights = service.getItems();
			routeList = new ArrayList<>(allFlights);
				
			int counter = 0;
			for (FlightRouteInfo e : allFlights) {
				if (info.equals(e)) {
					return counter;		
				}
				counter++;
			}	
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		return 0;
	}
	// Adds a generic infobox to the infopanel.
	public void addRoute() {
		routeBox = new FRInfoBox(this);
		routeListLayout.addComponent(routeBox);
		boxList.add(routeBox);
		numberRoutes += 1;
	}
	// Adds a route to the infobox based on parameters.
	public void addRoute(String name, String ID, String created, String modified, String length) {
		routeBox = new FRInfoBox(name, ID, created, modified, length, this);
		routeListLayout.addComponent(routeBox);
		boxList.add(routeBox);
		numberRoutes += 1;
	}
	// Gets the route layout.
	public VerticalLayout getRoutes() {
		return routeListLayout;
	}
	// Return whether or not a route is selected.
	public boolean getIsRouteSelected() {
		return isRouteSelected;
	}
	// Sets whether a route is selected.
	public void setIsRouteSelected(boolean selected) {
		isRouteSelected = selected;
	}
	// Gets button that creates route.
	public Button getDrawButton() {
		return drawButton;
	}
	// Gets the route that was added.
	public FlightRouteInfo getRoute() {
		return addedRoute;		
	}
	// Gets name of route that was created.
	public String getName() {
		return routeInputName;
	}
	// Gets the new route window.
	public FRNewRoute getDisplay() {
		return newRouteDisplay;
	}
	// Gets the name field from the new route window.
	public TextField getNameField() {
		return nameField;
	}
	// Gets the list of FlightRouteInfo objects.
	public List<FlightRouteInfo> getRouteList() {
		return routeList;
	}
	// Gets the route box that was just added.
	public FRInfoBox getInfoBox() {
		return routeBox;
	}
	// Gets the infobox at a certain index.
	public FRInfoBox getInfoBoxIndex(int index) {
		return (FRInfoBox) routeListLayout.getComponent(index);
	}
	// Gets the arraylist of info boxes.
	public ArrayList<FRInfoBox> getBoxList() {
		return boxList;
	}
	// Gets the controls component that was passed in through the constructor.
	public FRControlsComponent getControls() {
		return controlComponent;
	}
	// Gets the buttons on the top of the info panel.
	public HorizontalLayout getButtonLayout() {
		return buttons;
	}
	// Gets the entire info panel layout.
	public VerticalLayout getTotalLayout() {
		return totalLayout;
	}
	// Gets the button used to display the route creation window.
	public Button getNewRouteButton() {
		return newRoute;
	}
	// Removes the current window (used to remove route creation window).
	public void removeNewRouteWindow() {
		UI.getCurrent().removeWindow(newRouteWindow);
	}
}
