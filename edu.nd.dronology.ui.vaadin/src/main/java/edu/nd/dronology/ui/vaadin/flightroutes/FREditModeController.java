package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

/**
 * This class defines the bar overlaid on the map that signals when a route is in edit mode.
 * 
 * @author James Holland
 */

@SuppressWarnings("serial")
public class FREditModeController extends CustomComponent{
	private FRMapComponent mapComponent;
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private Button cancelButton = new Button("Cancel");
	private Button saveButton = new Button("Save");	
	private Label textLabel = new Label("Editing Route");
	private Label smallText = new Label("Left click to add a new waypoint. Drag waypoints to move.");
	
	private List<UIWayPoint> storedPoints = new ArrayList<>();
	private String storedName = "";
	private String storedDescription = "";
	
	public FREditModeController(FRMapComponent mapComponent) {
		this.mapComponent = mapComponent;
		
		setStyleName("fr_edit_bar");
		textLabel.setStyleName("large_text");
		smallText.setStyleName("small_text");
		
		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		totalLayout.addComponents(textLabel, smallText, cancelButton, saveButton);
		setCompositionRoot(totalLayout);
		
		// Click listeners for the cancel and saves buttons on edit bar - function are defined in FRMapComponent.
		cancelButton.addClickListener(e -> {
			cancelClick();
		});
		saveButton.addClickListener(e -> {
			saveClick();
		});
	}

	// Called when the cancel button is clicked. Disables editing and reverts changes back to the contents of storedPoints.
	private void cancelClick() {		
		// Reverts the changes by clearing mapPoints and adding storedPoints.
		mapComponent.getMapUtilities().removeAllPins();		
		for (int i = 0; i < storedPoints.size(); i++) {
			UIWayPoint point = storedPoints.get(i);
			mapComponent.getMapUtilities().addNewPin(point, -1);
		}
		
		mapComponent.getMetaInfo().setRouteName(storedName);
		mapComponent.getMetaInfo().setRouteDescription(storedDescription);
				
		mapComponent.updateLinesAndGrid();
		exitEditMode();
	}
	
	/* Called when the save button on the edit bar is clicked. It exits edit mode, sends the points to dronology, and uses stored points to display the correct
	 * waypoints on the map. */
	private void saveClick() {
		List<UIWayPoint> newWaypoints = mapComponent.getMapUtilities().getOrderedWayPoints();
		
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute = null;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		String id = mapComponent.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getId();
		
		// Sends the information to dronology to be saved.
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);
			
			froute.setName(mapComponent.getMetaInfo().getRouteName());
			froute.setDescription(mapComponent.getMetaInfo().getRouteDescription());

			ArrayList<Waypoint> oldCoords = new ArrayList<>(froute.getWaypoints());
			for (Waypoint cord : oldCoords) {
				froute.removeWaypoint(cord);
			}
			
			// The old waypoints are of type "Waypoint." We are converting to "WayPoint" as this is what we need later, and then adding it back to froute.
			for (UIWayPoint way : newWaypoints) {
				double alt = 0;
				double lon = 0;
				double lat = 0;
				double approach = 0;
			
				try {
					lon = Double.parseDouble(way.getLongitude());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				try {
					lat = Double.parseDouble(way.getLatitude());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				try {
					alt = Double.parseDouble(way.getAltitude());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				try{
					approach = Double.parseDouble(way.getTransitSpeed());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				
				Waypoint toSend = new Waypoint(new LlaCoordinate(lat, lon, alt));
				toSend.setApproachingspeed(approach);
				froute.addWaypoint(toSend);
			}

			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			routePersistor.saveItem(froute, outs);
			byte[] bytes = outs.toByteArray();

			service.transmitToServer(froute.getId(), bytes);
		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}
		
		List<Waypoint> newWaypointsToSave = froute.getWaypoints();
		String newRouteNameToSave = froute.getName();
		mapComponent.getMainLayout().getWaitingWindow().showWindow(
				"Saving route...",
				() -> {
					// Test if the route is updated in dronology
					Collection<FlightRouteInfo> routes = 
							mapComponent.getMainLayout().getControls().getInfoPanel().getRoutesFromDronology();
					FlightRouteInfo routeFromDronology = null;
					for (FlightRouteInfo route : routes) {
						if (route.getId().equals(id)) {
							routeFromDronology = route;
							break;
						}
					}
					
					if (routeFromDronology == null || 
							routeFromDronology.getWaypoints().size() != newWaypointsToSave.size() ||
							!newRouteNameToSave.equals(routeFromDronology.getName())) {
						//if the waypoint sizes are different, then it is not updated
						return false;
					} else {
						for (int i = 0; i < newWaypointsToSave.size(); ++i) {
							//if the waypoint info is different, then it is not updated
							if (!newWaypointsToSave.get(i).equals(routeFromDronology.getWaypoints().get(i))) {
								return false;
							}
						}
						//otherwise, it is updated
						return true;
					}
				},
				closeEvent -> {
					exitEditMode();
					mapComponent.getMainLayout().getControls().getInfoPanel().refreshRoutes();
					mapComponent.getMainLayout().switchRoute(
							mapComponent.getMainLayout().getControls().getInfoPanel().getRouteInfoBox(id));
				});
	}
	
	// Enables editing, adds the edit bar, and calls the enableRouteEditing function from MapMarkerUtilities.
	public void enterEditMode() {
		storedPoints = mapComponent.getMapUtilities().getOrderedWayPoints();
		storedName = mapComponent.getMetaInfo().getRouteName();
		storedDescription = mapComponent.getMetaInfo().getRouteDescription();
		
		mapComponent.getMapUtilities().setEditable(true);
		mapComponent.getTableDisplay().makeEditable();
		this.setVisible(true);
		
		mapComponent.getMap().addStyleName("fr_leaflet_map_edit_mode");
		mapComponent.getTableDisplay().getGrid().addStyleName("fr_table_component_edit_mode");
	}
	
	// Disables editing, removes the edit bar, and changes the component styles accordingly.
	public void exitEditMode() {
		storedPoints.clear();
		storedName = "";
		storedDescription = "";
		
		mapComponent.getMapUtilities().setEditable(false);
		mapComponent.getTableDisplay().makeUneditable();
		this.setVisible(false);
		
		mapComponent.getMap().removeStyleName("fr_leaflet_map_edit_mode");
		mapComponent.getTableDisplay().getGrid().removeStyleName("fr_table_component_edit_mode");
	}
}
