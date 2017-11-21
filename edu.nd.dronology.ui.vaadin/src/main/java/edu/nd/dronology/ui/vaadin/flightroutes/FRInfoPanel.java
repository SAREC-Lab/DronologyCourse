package edu.nd.dronology.ui.vaadin.flightroutes;

import java.rmi.RemoteException;
import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRNewRouteWindow;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRUnsavedChangesConfirmation.ChangeType;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This is the list of selectable flight routes.
 * 
 * @author James Holland
 *
 */

@SuppressWarnings("serial")
public class FRInfoPanel extends CustomComponent {
	private Panel mainPanel = new Panel();
	private VerticalLayout routeListLayout = new VerticalLayout();
	
	private FRNewRouteWindow newRouteWindow;
	private FRControlsComponent controlComponent;
	private Button newRouteButton;
	
	public FRInfoPanel(FRControlsComponent controls) {
		controlComponent = controls;
		
		VerticalLayout totalLayout = new VerticalLayout();
		// Top bar of panel.
		mainPanel.setCaption("0 Routes in database");
		mainPanel.addStyleName("fr_info_panel");
		mainPanel.addStyleName("control_panel");

		newRouteButton = new Button("+ Add a new route");
		newRouteButton.addStyleName("fr_new_route_button");

		// Displays the route creation window.
		newRouteButton.addClickListener(e -> {
			UI.getCurrent().addWindow(newRouteWindow);
			if (controls.getMainLayout().getMapComponent().getMapUtilities().isEditable()) {
				controls.getMainLayout().getUnsavedChangesConfirmation().showWindow(
						this.getHighlightedFRInfoBox().getFlightRouteInfo().getName(), ChangeType.NEW_ROUTE, e);
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(newRouteButton);
		buttons.addStyleName("fr_new_route_button_area");
		
		totalLayout.addComponents(buttons, routeListLayout);
		mainPanel.setContent(totalLayout);
		setCompositionRoot(mainPanel);

		// Box to input new route info.
		newRouteWindow = new FRNewRouteWindow(this);

		refreshRoutes();
		
		routeListLayout.addLayoutClickListener(e -> {
			if (controls.getMainLayout().getMapComponent().getMapUtilities().isEditable()) {
				controls.getMainLayout().getUnsavedChangesConfirmation().showWindow(
					this.getHighlightedFRInfoBox().getFlightRouteInfo().getName(), ChangeType.SWITCH_ROUTE, e);
			} else {
				// If map is not in edit mode, then just switch to the other route.
				Component childComponent = e.getChildComponent();
				if (childComponent != null && childComponent.getClass().equals(FRInfoBox.class)) {
					controls.getMainLayout().switchRoute((FRInfoBox)childComponent);
				}
			}
		});
	}
	// Ensures routes are updated by removing and re-adding routes.
	public void refreshRoutes() {
		FRInfoBox highlightedBox = this.getHighlightedFRInfoBox();
		String highlightedId = highlightedBox == null? "" : highlightedBox.getId();
		
		routeListLayout.removeAllComponents();
		Collection<FlightRouteInfo> allFlights = getRoutesFromDronology();
		mainPanel.setCaption(allFlights.size() + " Routes in database");
	
		// Iterates through the routes, gets the fields of each, and creates an infobox.
		for (FlightRouteInfo info : allFlights) {
			FRInfoBox routeBox = addRoute(info);
			//To preserve previous route selection
			if (highlightedId.equals(routeBox.getId())) {
				this.getControls().getMainLayout().switchRoute(routeBox);
			}
		}
	}
	// Fetch routes information from dronology
	public Collection<FlightRouteInfo> getRoutesFromDronology() {
		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
			
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> allFlights = service.getItems();
				
			return allFlights;
		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
		return null;
	}
	// Gets FlightRouteInfo based on route index.
	public FlightRouteInfo getFlightRouteInfo(int index) {
		return ((FRInfoBox)routeListLayout.getComponent(index)).getFlightRouteInfo();
	}
	// Gets the route index based on the FlightRouteInfo.
	public int getRouteIndex(FlightRouteInfo info) {
		for (int i = 0; i < routeListLayout.getComponentCount(); i++) {
			if (info.equals(((FRInfoBox)routeListLayout.getComponent(i)).getFlightRouteInfo()))
				return i;
		}
		return -1;
	}
	//Gets the route info box based on the FlightRouteInfo.
	public FRInfoBox getRouteInfoBox(FlightRouteInfo info) {
		for (int i = 0; i < routeListLayout.getComponentCount(); i++) {
			if (info.equals(((FRInfoBox)routeListLayout.getComponent(i)).getFlightRouteInfo()))
				return (FRInfoBox)routeListLayout.getComponent(i);
		}
		return null;
	}

	//Gets the route info box based on the FlightRouteInfo id.
	public FRInfoBox getRouteInfoBox(String id) {
		for (int i = 0; i < routeListLayout.getComponentCount(); i++) {
			if (id.equals(((FRInfoBox)routeListLayout.getComponent(i)).getFlightRouteInfo().getId()))
				return (FRInfoBox)routeListLayout.getComponent(i);
		}
		return null;
	}
	
	// Adds a route to the infobox based on parameters.
	public FRInfoBox addRoute(FlightRouteInfo flightRouteInfo) {
		FRInfoBox routeBox = new FRInfoBox(this, flightRouteInfo);
		routeListLayout.addComponent(routeBox);
		return routeBox;
	}

	public void unhighlightAllInfoBoxes() {
		for (int i = 0; i < routeListLayout.getComponentCount(); i++) {
			routeListLayout.getComponent(i).removeStyleName("info_box_focus");
		}
	}
	public void highlightInfoBox(int index) {
		routeListLayout.getComponent(index).addStyleName("info_box_focus");
	}
	
	public FRInfoBox getHighlightedFRInfoBox() {
		for (int i = 0; i < routeListLayout.getComponentCount(); i++) {
			if (routeListLayout.getComponent(i).getStyleName().contains("info_box_focus"))
				return (FRInfoBox)routeListLayout.getComponent(i);
		}
		return null;
	}
	
	// Gets the route layout.
	public VerticalLayout getRoutes() {
		return routeListLayout;
	}
	// Gets the controls component that was passed in through the constructor.
	public FRControlsComponent getControls() {
		return controlComponent;
	}
	// Gets the button used to display the route creation window.
	public Button getNewRouteButton() {
		return newRouteButton;
	}
	// Removes the current window (used to remove route creation window).
	public void removeNewRouteWindow() {
		UI.getCurrent().removeWindow(newRouteWindow);
	}
}
