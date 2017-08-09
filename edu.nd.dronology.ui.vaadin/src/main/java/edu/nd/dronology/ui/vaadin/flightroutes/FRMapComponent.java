package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the map component for the Flight Routes UI. It has the code for creating waypoint windows and popup views, the functions related to
 * displaying the board in its various states, methods related to entering and exiting edit mode, click listener constructors for the edit bar,
 * and functions related to the route description.
 * 
 * @author Jinghui Cheng
 */
@SuppressWarnings("serial")
public class FRMapComponent extends CustomComponent {
	private FRMainLayout mainLayout;
	private MapMarkerUtilities utilities;
	
	private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout mapAndPopup = new AbsoluteLayout();
	private FRTableDisplay tableDisplay = new FRTableDisplay();
	private FREditBar editBar = new FREditBar(this);
	private FRMetaInfo metaInfo = new FRMetaInfo(this);
	private List<UIWayPoint> storedPoints = new ArrayList<>();
	private LMap leafletMap;

	public FRMapComponent(String tileDataURL, String name, String satelliteTileDataURL, String satelliteLayerName, FRMainLayout mainLayout) {
		this.mainLayout = mainLayout;
		this.setWidth("100%");

		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);
		LTileLayer satelliteTiles = new LTileLayer();
		satelliteTiles.setUrl(satelliteTileDataURL);

		leafletMap = new LMap();
		leafletMap.addBaseLayer(tiles, name);
		leafletMap.addOverlay(satelliteTiles, satelliteLayerName);
	
		Window newWayPointWindow = createNewWayPointWindow();
		/* Creates a window that allows the user to input altitude and transit speed information about a newly created waypoint. 
		 * Values are read in and used in the MapMarkerUtilties class. */
		
		PopupView waypointPopup = createWayPointPopupView();
		/* Creates a popup view that shows information about a waypoint once a mouse over listener is activated corresponding to a waypoint.
		 * Values are set in the MapMarkerUtilities class. */
		
		mapAndPopup.addComponents(waypointPopup, leafletMap);
		content.addComponents(metaInfo, mapAndPopup, tableDisplay.getGrid());
		setCompositionRoot(content);

		this.addStyleName("map_component");
		this.addStyleName("fr_map_component");
		mapAndPopup.addStyleName("fr_mapabsolute_layout");
		leafletMap.addStyleName("fr_leaflet_map");
		leafletMap.addStyleName("bring_back");
		editBar.addStyleName("bring_front");

		utilities = new MapMarkerUtilities(mapAndPopup, leafletMap, tableDisplay, waypointPopup, this, newWayPointWindow);
		tableDisplay.setUtilities(utilities);
		utilities.disableRouteEditing();
		
		this.setRouteCenter();
	}

	private Window createNewWayPointWindow() {
		// Window that allows the user to enter altitude and transit speed information.
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);

		// Creates a vertical layout, which is then used to instantiate a window.
		VerticalLayout windowContent = new VerticalLayout();
		TextField altitudeField = new TextField("Altitude: ");
		TextField transitSpeedField = new TextField("Transit Speed: ");

		windowContent.addComponent(altitudeField);
		windowContent.addComponent(transitSpeedField);
		windowContent.addComponent(buttons);

		Window window;
		window = new Window(null, windowContent);
		
		window.addStyleName("confirm_window");
		buttons.addStyleName("confirm_button_area");
		saveButton.addStyleName("btn-okay");
		
		window.setModal(true);
		window.setClosable(false);
		window.setResizable(false);

		return window;
	}
	// Popup that displays on pin mouse-over. Contains a button, which allows the waypoint to be deleted.
	public PopupView createWayPointPopupView() {
		VerticalLayout popupContent = new VerticalLayout();
		popupContent.removeAllComponents();
		// Remove all components before adding new ones because only one set of waypoint information should be shown at a time.
		
		Label latitudeLabel = new Label();
		latitudeLabel.setId("latitude");
		
		Label longitudeLabel = new Label();
		longitudeLabel.setId("longitude");
		
		Label altitudeLabel = new Label();
		altitudeLabel.setId("altitude");
		
		Label transitSpeedLabel = new Label();
		transitSpeedLabel.setId("transitSpeed");
		
		popupContent.addComponents(latitudeLabel, longitudeLabel, altitudeLabel, transitSpeedLabel);
	
		// Uses popupContent vertical layout to instantiate popup.
		PopupView waypointPopup = new PopupView(null, popupContent);
		
		Button toDelete = new Button("Remove Waypoint");
		toDelete.addClickListener(event -> {
			waypointPopup.setPopupVisible(false);
			this.getMainLayout().getDeleteWayPointConfirmation().showWindow(event);
		});
		
		toDelete.setId("toDelete");
		popupContent.addComponent(toDelete);
		
		waypointPopup.addStyleName("bring_front");
		popupContent.addStyleName("fr_waypoint_popup");
		waypointPopup.setVisible(false);
		waypointPopup.setPopupVisible(false);
		
		return waypointPopup;
	}
	// Displays with the map and table empty. Called when a route is deleted so that its waypoints are no longer displayed.
	public void displayNoRoute() {
		metaInfo.showInfoWhenNoRouteIsSelected();
		
		utilities.disableRouteEditing();
		utilities.getMapPoints().clear();
		utilities.getGrid().setItems(utilities.getMapPoints());
		
		utilities.removeAllMarkers(utilities.getPins());
		utilities.removeAllLines(utilities.getPolylines());
	}
	@WaypointReplace
	public void displayFlightRoute(FlightRouteInfo info) {
		metaInfo.showInfoForSelectedRoute(info);
		
		utilities.disableRouteEditing();
		
		// Removes old pins, polylines, and style when switching routes.
		utilities.removeAllMarkers(this.getUtilities().getPins());
		utilities.removeAllLines(this.getUtilities().getPolylines());
		utilities.clearMapPoints();
		
		// Iterates through the flight info and adds to internal waypoints list.
		List<UIWayPoint> wayPoints = new ArrayList<>();
		for (Waypoint waypoint : info.getWaypoints()) {
			UIWayPoint way = new UIWayPoint(waypoint);
			utilities.addNewPinRemoveOld(way.toPoint());
			wayPoints.add(way);
		}
		utilities.setMapPointsAltitude(wayPoints);
		utilities.setMapPointsTransit(wayPoints);

		// Adds the lines to the map.
		utilities.drawLines(wayPoints, true, 1, false);
		
		setRouteCenter();
		
		// Sets grid.
		tableDisplay.setGrid(utilities.getMapPoints());
	}
	// Removes the grid and changes the style of the map accordingly.
	public void displayNoTable() {
		content.removeComponent(tableDisplay.getGrid());
		this.addStyleName("fr_map_component_no_table");
	}
	// Adds the grid to the UI.
	public void displayTable() {
		content.addComponent(tableDisplay.getGrid());
		this.removeStyleName("fr_map_component_no_table");
	}
	// Enables editing, adds the edit bar, and calls the enableRouteEditing function from MapMarkerUtilities.
	public void enableEdit() {
		mapAndPopup.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");

		utilities.enableRouteEditing();
	}
	// Disables editing, removes the edit bar, and changes the component styles accordingly.
	public void exitEditMode() {
		utilities.disableRouteEditing();

		mapAndPopup.removeComponent(editBar);
		leafletMap.setStyleName("fr_leaflet_map");
		tableDisplay.getGrid().setStyleName("fr_table_component");
	}
	// Called when the edit button is clicked. Stores the current contents of route.getMapPoints() in case the changes are reverted.
	public void processEditButtonClicked() {
		storedPoints.clear();

		for (int i = 0; i < utilities.getMapPoints().size(); i++) {
			storedPoints.add(utilities.getMapPoints().get(i));
		}
		
		utilities.enableRouteEditing();
		editBar.addStyleName("bring_front");
		mapAndPopup.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
	}
	// Called when the cancel button is clicked. Disables editing and reverts changes back to the contents of storedPoints.
	public void cancelClick() {
		utilities.disableRouteEditing();
		
		utilities.getMapPoints().clear();
		for (int i = 0; i < storedPoints.size(); i++) {
			utilities.getMapPoints().add(storedPoints.get(i));
		}
		utilities.getTableDisplay().setGrid(utilities.getMapPoints());
		// Reverts the changes by clearing mapPoints and adding storedPoints.

		utilities.removeAllMarkers(utilities.getPins());
		utilities.removeAllLines(utilities.getPolylines());
		
		for (int i = 0; i < storedPoints.size(); i++) {
			UIWayPoint point = storedPoints.get(i);
			utilities.addPinForWayPoint(point, true);
		}
		
		utilities.updatePinColors();
		utilities.drawLines(storedPoints, true, 0, false);
	
		mapAndPopup.removeComponent(editBar);
		leafletMap.addStyleName("bring_back");
		leafletMap.removeStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().removeStyleName("fr_table_component_edit_mode");
	}
	/* Called when the save button on the edit bar is clicked. It exits edit mode, sends the points to dronology, and uses stored points to display the correct
	 * waypoints on the map. */
	public void saveClick() {
		exitEditMode();
		
		List<UIWayPoint> newWaypoints = utilities.getMapPoints();
		
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		// Sends the information to dronology to be saved.
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			String id;

			// Gets routes from dronology and requests their id.
			id = this.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getId();

			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

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
		// Tests if points were added or deleted. If added, an identical ArrayList of waypoints is created (this is a workaround to remove the click-listeners).

		storedPoints.clear();
		
		for (int i = 0; i < utilities.getMapPoints().size(); i++) {			
			storedPoints.add(utilities.getMapPoints().get(i));
		}
		
		utilities.getMapPoints().clear();
		
		// Waypoints are re-loaded into mapPoints, but without click listeners.
		for (int i = 0; i < storedPoints.size(); i++) {
			utilities.getMapPoints().add(storedPoints.get(i));
		}
		
		// Then, the map has the points and lines redrawn.
		utilities.getGrid().setItems(utilities.getMapPoints());
		
		utilities.removeAllMarkers(utilities.getPins());
		utilities.removeAllLines(utilities.getPolylines());
		
		for (int i = 0; i < storedPoints.size(); i++) {
			UIWayPoint point = storedPoints.get(i);
			utilities.addPinForWayPoint(point, true);
		}
		
		utilities.drawLines(storedPoints, true, 0, false);

		// Adds mouse over and mouse out listeners to the pins.
		List<LMarker> oldPins = utilities.getPins();
		List<LMarker> newPins = new ArrayList<>();
		
		for (int i = 0; i < oldPins.size(); i++) {
			LMarker newPin = new LMarker(oldPins.get(i).getPoint());
			utilities.addPinListeners(newPin);
			newPins.add(newPin);
		}
		
		utilities.getPins().clear();
		
		for (int i = 0; i < newPins.size(); i++) {
			utilities.getPins().add(newPins.get(i));
		}

		utilities.disableRouteEditing();
		
		for (int i = 0; i < utilities.getMapPoints().size(); i++) {
			utilities.getMapPoints().get(i).setOrder(i+1);
		}
	}
	// Displays the waypoints in edit mode depending on whether or not the route is new.
	public void onMapEdited(List<UIWayPoint> waypoints) {
		metaInfo.setNumWaypoints(waypoints.size());
	}
	// Gets the route description using the currently selected route stored by "selectedRoute".
	public String getRouteDescription() {
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		
		String description = "didnt get description"; 
		// Sends the information to dronology to be saved.
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			String id = this.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getId();			
			
			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

			description = froute.getDescription();
			if(description == null) {
				description = "No Description";
			}
		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}
		return description;
		
	}
	// Sets the center of the route based on the stored waypoints such that the map is as visible as possible.
	public void setRouteCenter() {
		if (getMetaInfo().getAutoZooming().getValue() == true) {
			// Calculates the mean point and sets the route.
			double meanLat = 0;
			double meanLon = 0;
			int numberPoints;
			double farthestLat = 0;
			double farthestLon = 0;
			double zoom;
			
			List<UIWayPoint> currentWayPoints = utilities.getMapPoints();
			numberPoints = utilities.getMapPoints().size();
			
			for (UIWayPoint p: currentWayPoints) {
					meanLat += Double.valueOf(p.getLatitude());
					meanLon += Double.valueOf(p.getLongitude());
			}
			
			meanLat /= (numberPoints * 1.0);
			meanLon /= (numberPoints * 1.0);
			
			// Finds farthest latitude and longitude from mean.
			for (UIWayPoint p: currentWayPoints) {
				if ((Math.abs(Double.valueOf(p.getLatitude()) - meanLat) > farthestLat)) {
					farthestLat = (Math.abs((Double.valueOf(p.getLatitude())) - meanLat));
				}
				if ((Math.abs(Double.valueOf(p.getLongitude()) - meanLon) > farthestLon)) {
					farthestLon = (Math.abs((Double.valueOf(p.getLongitude()) - meanLon)));
				}
			}  
			
			// Used to calculate zoom level.
			Point centerPoint = new Point(meanLat, meanLon);
			if (farthestLat == 0 && farthestLon == 0) {
				zoom = 17;
			} else {
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
			
			leafletMap.setCenter(centerPoint, zoom+1);
		}
	}
	// Sets either the name or description of the selected route based on the boolean passed in.
	public void setRouteNameDescription(String name, String description) {
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		// Sends the information to dronology to be saved.
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			// Gets id of the selected route, requests the corresponding froute object from the server, sets the name or description, and sends it back.
			String id = this.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getId();
			
			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

			if (name != null) {
				froute.setName(name);
			}
			if (description != null) {
				froute.setDescription(description);
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
	}
	// Gets the route information bar above the map.
	public FRMetaInfo getMetaInfo() {
		return metaInfo;
	}
	// Gets the class that represents the utilities.
	public MapMarkerUtilities getUtilities() {
		return utilities;
	}
	// Gets the table beneath the map.
	public FRTableDisplay getTableDisplay() {
		return tableDisplay;
	}
	// Gets the main layout (passed into constructor).
	public FRMainLayout getMainLayout() {
		return mainLayout;
	}
	// Allows the longitude and latitude to be set manually.
	public void setCenter(double centerLat, double centerLon) {
		leafletMap.setCenter(centerLat, centerLon);
	}
	// Allows the zoomLevel to be set manually.
	public void setZoomLevel(double zoomLevel) {
		leafletMap.setZoomLevel(zoomLevel);
	}
}
