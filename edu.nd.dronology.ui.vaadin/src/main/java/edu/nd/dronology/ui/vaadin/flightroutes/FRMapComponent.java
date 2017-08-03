package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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
import edu.nd.dronology.ui.vaadin.utils.WayPoint;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the map component for the Flight Routes UI. It has the code for creating waypoint windows and popup views, the functions related to
 * displaying the board in its various states, methods related to entering and exiting edit mode, click listener constructors for the edit bar,
 * and functions related to the route description.
 * 
 * @author Jinghui Cheng
 */
public class FRMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	private FRMainLayout mainLayout;
	private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout mapAndPopup = new AbsoluteLayout();
	private AbsoluteLayout layout = new AbsoluteLayout();
	private MapMarkerUtilities utilities;
	private FRTableDisplay tableDisplay = new FRTableDisplay();
	private FRMetaInfo bar = new FRMetaInfo();
	private FReditBar editBar = new FReditBar(this);
	private FRMetaInfo selectedBar;
	private List<WayPoint> storedPoints = new ArrayList<>();
	private FRDeleteRoute delete = new FRDeleteRoute(this);
	private PopupView popup;
	private FlightRouteInfo selectedRoute;
	private boolean zoomRoute = true;
	private LTileLayer tiles;
	private LMap leafletMap;

	public FRMapComponent(String tileDataURL, String name, String satelliteTileDataURL, String satelliteLayerName, FRMainLayout layout, boolean zoomRoute) {
		this.setWidth("100%");
		addStyleName("map_component");
		addStyleName("fr_map_component");

		leafletMap = new LMap();
		leafletMap.addStyleName("fr_leaflet_map");
	
		Window window = createWayPointWindow();
		/* Creates a window that allows the user to input altitude and transit speed information about a newly created waypoint. 
		 * Values are read in and used in the MapMarkerUtilties class. */
		
		PopupView popup = createWayPointPopupView();
		/* Creates a popup view that shows information about a waypoint once a mouse over listener is activated corresponding to a waypoint.
		 * Values are set in the MapMarkerUtilities class. */
		
		mapAndPopup.addComponent(popup);
		utilities = new MapMarkerUtilities(mapAndPopup, leafletMap, tableDisplay, popup, this, window);

		mapAndPopup.addStyleName("fr_mapabsolute_layout");
		mapAndPopup.addComponent(leafletMap);
		tableDisplay.setUtilities(utilities);

		tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);

		LTileLayer satelliteTiles = new LTileLayer();
		satelliteTiles.setUrl(satelliteTileDataURL);

		leafletMap.addBaseLayer(tiles, name);
		leafletMap.addOverlay(satelliteTiles, satelliteLayerName);
		content.addComponent(mapAndPopup);

		utilities.disableRouteEditing();

		setCompositionRoot(content);
		content.addComponents(tableDisplay.getGrid());
		mainLayout = layout;
		
		this.zoomRoute = zoomRoute;
		this.setRouteCenter(zoomRoute);
	}
	@SuppressWarnings("static-method")
	private Window createWayPointWindow() {
		// Window that allows the user to enter altitude and transit speed information.
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);

		//creates vertical layout and then uses it to instantiate window
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
	// Popup that displays on pin mouse-over. Contains button allowing for the waypoint to be deleted.
	public PopupView createWayPointPopupView() {
		VerticalLayout popupContent = new VerticalLayout();
		popupContent.removeAllComponents();
		
		Label latitudeLabel = new Label();
		latitudeLabel.setId("latitude");
		
		Label longitudeLabel = new Label();
		longitudeLabel.setId("longitude");
		
		Label altitudeLabel = new Label();
		altitudeLabel.setId("altitude");
		
		Label transitSpeedLabel = new Label();
		transitSpeedLabel.setId("transitSpeed");
		
		popupContent.addComponents(latitudeLabel, longitudeLabel, altitudeLabel, transitSpeedLabel);
	
		//uses popupContent vertical layout to instantiate popup	
		popup = new PopupView(null, popupContent);
		
		Button toDelete = new Button("Remove Waypoint");
		toDelete.addClickListener(event -> {
			popup.setPopupVisible(false);
			
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				if (utilities.getMapPoints().get(i).getId().equals(utilities.getSelectedWayPointId())) {
					utilities.getMapPoints().remove(utilities.getMapPoints().get(i));
				}
			}
			// Finds and removes the waypoint of interest from the mapPoints ArrayList in MapMarkerUtilities using its id.
			
			utilities.getMap().removeComponent(utilities.getLeafletMarker());
			// Removes the marker from the map.
			
			utilities.removeAllLines(utilities.getPolylines());
			utilities.updatePinColors();
			utilities.drawLines(utilities.getMapPoints(), true, 1, false);
			// Redraws the polylines given the updated mapPoints.
			
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				utilities.getMapPoints().get(i).setOrder(i + 1);
			}
			// Sets the order of the waypoints for display purposes.
			
			utilities.getGrid().setItems(utilities.getMapPoints());
			// Update the grid to reflect the updated mapPoints.
			
			onMapEdited(storedPoints);
			// Sets the number of waypoints and updates the displays the map in edit mode.
		});
		
		toDelete.setId("toDelete");
		popupContent.addComponent(toDelete);
		
		popup.addStyleName("bring_front");
		popupContent.addStyleName("fr_waypoint_popup");
		popup.setVisible(false);
		popup.setPopupVisible(false);
		
		return popup;
	}
	// Displays when no route is selected.
	public void display() {
		content.addComponent(bar);
		content.addComponents(mapAndPopup, tableDisplay.getGrid());
	}
	// Displays with the map and table empty. Called when a route is deleted so that its waypoints are no longer displayed.
	public void displayNoRoute() {
		content.removeAllComponents();
		content.addComponent(bar);
		content.addComponents(mapAndPopup, tableDisplay.getGrid());
			
		utilities.getMapPoints().clear();
		utilities.getGrid().setItems(utilities.getMapPoints());
		
		utilities.removeAllMarkers(utilities.getPins());
		utilities.removeAllLines(utilities.getPolylines());
	}
	@WaypointReplace
	public void displayByName(FlightRouteInfo info, String routeName, int numCoords, boolean whichName, boolean zoomRoute) {
		// Displays with selected route info based on either the FlightRouteInfo object or the name and coordinate number passed in.
		selectedRoute = info;
		utilities.disableRouteEditing();

		layout = new AbsoluteLayout();
		layout.addStyleName("fr_mapabsolute_layout");
		
		if (whichName) {
			selectedBar = new FRMetaInfo(routeName, numCoords, this, zoomRoute);
		} else {
			selectedBar = new FRMetaInfo(info, this, zoomRoute);
		}
		
		getMetaInfo().getAutoZooming().addValueChangeListener(event -> {
			this.zoomRoute = getMetaInfo().getAutoZooming().getValue();
		});
		
		editBar = new FReditBar(this);
		CheckBox tableBox = selectedBar.getCheckBox();
		
		// Hides the table.
		tableBox.addValueChangeListener(event -> {
			if (tableBox.getValue()) {
				displayTable();
			} else {
				displayNoTable();
			}
		});

		leafletMap.setStyleName("bring_back");

		layout.addComponent(mapAndPopup, "top:5px; left:5px");

		content.removeAllComponents();
		content.addComponent(selectedBar);
		content.addComponents(layout, tableDisplay.getGrid());
		
		tableDisplay.setGrid(utilities.getMapPoints());	
	}
	// Displays the route determined by the FlightRouteInfo object and then re-enables edit mode.
	public void displayStillEdit(FlightRouteInfo info, String routeName, int numCoords, boolean whichName, boolean zoomRoute) {
		displayByName(info, routeName, numCoords, whichName, zoomRoute);
		
		utilities.enableRouteEditing();
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
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
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");

		utilities.enableRouteEditing();
	}
	// Disables editing, removes the edit bar, and changes the component styles accordingly.
	public void exitEditMode() {
		utilities.disableRouteEditing();

		layout.removeComponent(editBar);
		leafletMap.setStyleName("fr_leaflet_map");
		leafletMap.addStyleName("bring_back");
		tableDisplay.getGrid().setStyleName("fr_table_component");
	}
	// Called when the edit button is clicked. Stores the current contents of route.getMapPoints() in case the changes are reverted.
	public void editButton() {
		storedPoints.clear();

		for (int i = 0; i < utilities.getMapPoints().size(); i++) {
			storedPoints.add(utilities.getMapPoints().get(i));
		}
		
		utilities.enableRouteEditing();
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
	}
	// Called when the cancel button is clicked. Disables editing and reverts changes back to the contents of storedPoints.
	public void cancelClick() {
		utilities.disableRouteEditing();
		
		if (storedPoints.size() != 0) {
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				utilities.getMapPoints().remove(i);
			}
			utilities.getMapPoints().clear();
				
			for (int i = 0; i < storedPoints.size(); i++) {
				utilities.getMapPoints().add(storedPoints.get(i));
			}
			//adds the stored points
			
			utilities.getTableDisplay().setGrid(utilities.getMapPoints());
			utilities.removeAllMarkers(utilities.getPins());
			utilities.removeAllLines(utilities.getPolylines());
				
			for (int i = 0; i < storedPoints.size(); i++) {
				WayPoint point = storedPoints.get(i);
				utilities.addPinForWayPoint(point, true);
			}
			
			utilities.updatePinColors();
			utilities.drawLines(storedPoints, true, 0, false);
			
		} else {
			displayByName(selectedRoute, selectedRoute.getName(), selectedRoute.getWaypoints().size(), false, zoomRoute);
		}
		
		layout.removeComponent(editBar);
		leafletMap.addStyleName("bring_back");
		leafletMap.removeStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().removeStyleName("fr_table_component_edit_mode");
	}
	// Called when the delete button is clicked. It passes the correct FlightRouteInfo object to the FRDeleteRoute class.
	public void deleteClick() {
		delete.setRouteInfoTobeDeleted(selectedRoute);
		UI.getCurrent().addWindow(delete.getWindow());
	}
	// Called when the save button on the edit bar is clicked. It exits edit mode, sends the points to dronology, and uses stored points to display the correct waypoints on the map.
	public void saveClick() {
		exitEditMode();
		
		List<WayPoint> newWaypoints = utilities.getMapPoints();
		
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

			// Gets routes from dronology and requests their name/id.
			id = selectedRoute.getId();

			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

			ArrayList<Waypoint> oldCoords = new ArrayList<>(froute.getWaypoints());
			for (Waypoint cord : oldCoords) {
				froute.removeWaypoint(cord);
			}
			
			//the old waypoints are of type "Waypoint." We are converting to "WayPoint" as this is what we need later, and then adding it back to froute
			for (WayPoint way : newWaypoints) {
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
		if(storedPoints.size() < utilities.getMapPoints().size()) {
			for (int i = 0; i < storedPoints.size(); i++) {
				storedPoints.remove(i);
			}
			storedPoints.clear();
			
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				String alt = utilities.getMapPoints().get(i).getAltitude();
				String lon = utilities.getMapPoints().get(i).getLongitude();
				String lat = utilities.getMapPoints().get(i).getLatitude();
				String trans = utilities.getMapPoints().get(i).getTransitSpeed();
				
				Point pt = new Point();
				
				pt.setLat(Double.valueOf(lat));
				pt.setLon(Double.valueOf(lon));
				
				WayPoint way = new WayPoint(pt, false);
				way.setAltitude(alt);
				way.setTransitSpeed(trans);
				
				storedPoints.add(way);
			}
			
			for (int i = 0; i < utilities.getMapPoints().size(); i++) {
				utilities.getMapPoints().remove(i);
			}
			utilities.getMapPoints().clear();
			// Waypoints are re-loaded into mapPoints, but without click listeners.
			for (int i = 0; i < storedPoints.size(); i++) {
				storedPoints.get(i).setId(UUID.randomUUID().toString());
				utilities.getMapPoints().add(storedPoints.get(i));
			}
			
			// Then, the map has the points and lines redrawn.
			utilities.getGrid().setItems(utilities.getMapPoints());
			
			utilities.removeAllMarkers(utilities.getPins());
			utilities.removeAllLines(utilities.getPolylines());
			
			for (int i = 0; i < storedPoints.size(); i++) {
				WayPoint point = storedPoints.get(i);
				utilities.addPinForWayPoint(point, true);
			}
			
			utilities.drawLines(storedPoints, true, 0, false);
		}
		utilities.disableRouteEditing();
		for (int i = 0; i < utilities.getMapPoints().size(); i++) {
			utilities.getMapPoints().get(i).setOrder(i+1);
		}
	}
	// Displays the waypoints in edit mode depending on whether or not the route is new.
	public void onMapEdited(List<WayPoint> waypoints) {
		bar.setNumWaypoints(waypoints.size());
		if (mainLayout.isNew()) {
			displayStillEdit(mainLayout.getNewRoute(), mainLayout.getNewRouteName(), utilities.getMapPoints().size(), true, zoomRoute);
		} else {
			displayStillEdit(mainLayout.getFlightInfo(), mainLayout.getFlightInfo().getName(), utilities.getMapPoints().size(), true, zoomRoute);
		}
	}
	//gets the route description using the currently selected route stored by "selectedRoute"
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

			String id = selectedRoute.getId();			
			
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
	//sets the center of the route based on the stored waypoints such that the map is as visible as possible
	public void setRouteCenter(boolean zoomRoute) {
		if (zoomRoute) {
			// Calculates the mean point and sets the route.
			double meanLat = 0;
			double meanLon = 0;
			int numberPoints;
			double farthestLat = 0;
			double farthestLon = 0;
			double zoom;
			
			List<WayPoint> currentWayPoints = utilities.getMapPoints();
			numberPoints = utilities.getMapPoints().size();
			
			for (WayPoint p: currentWayPoints) {
					meanLat += Double.valueOf(p.getLatitude());
					meanLon += Double.valueOf(p.getLongitude());
			}
			
			meanLat /= (numberPoints * 1.0);
			meanLon /= (numberPoints * 1.0);
			
			// Finds farthest latitude and longitude from mean.
			for (WayPoint p: currentWayPoints) {
				if ((Math.abs(Double.valueOf(p.getLatitude()) - meanLat) > farthestLat)) {
					farthestLat = (Math.abs((Double.valueOf(p.getLatitude())) - meanLat));
				}
				if ((Math.abs(Double.valueOf(p.getLongitude()) - meanLon) > farthestLon)) {
					farthestLon = (Math.abs((Double.valueOf(p.getLongitude()) - meanLon)));
				}
			}  
			
			//used to calculate zoom level
			Point centerPoint = new Point(meanLat, meanLon);
			if (farthestLat == 0 && farthestLon == 0) {
				zoom = 17;
			} else {
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
			
			leafletMap.setCenter(centerPoint, zoom+1);
		}
		if (selectedBar != null)
			selectedBar.getAutoZooming().setValue(zoomRoute);
	}
	//sets either the name or description of the selected route based on the boolean passed in 
	public void setRouteNameDescription(String input, boolean whichOne) {
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		// Sends the information to dronology to be saved.
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			//gets id of the selected route, requests the corresponding froute object from the server, sets the name or description, and sends it back
			String id = selectedRoute.getId();
			
			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

			if(whichOne == true) {
				froute.setName(input);
			} else {
				froute.setDescription(input);
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
	//gets the route information bar above the map 
	public FRMetaInfo getMetaInfo() {
		return selectedBar;
	}
	//gets whether or not the map zooms to the route
	public boolean getZoomRoute() {
		return zoomRoute;
	}
	//gets the window that asks the user to delete a route. This class also contains the function that deletes a route
	public FRDeleteRoute getDeleteBar() {
		return delete;
	}
	//returns FlightRouteInfo object representing the currently selected route
	public FlightRouteInfo getSelectedRoute() {
		return selectedRoute;
	}
	//gets the centered latitude
	public double getCenterLat() {
		return leafletMap.getCenter().getLat();
	}
	//gets the centered longitude
	public double getCenterLon() {
		return leafletMap.getCenter().getLon();
	}
	//gets the appropriate zoom level for a certain route
	public double getZoomLevel() {
		return leafletMap.getZoomLevel();
	}
	//gets an instance of the map
	public LMap getMapInstance() {
		return leafletMap;
	}
	//gets the class that represents the utilities
	public MapMarkerUtilities getUtilities() {
		return utilities;
	}
	//gets the table beneath the map
	public FRTableDisplay getTableDisplay() {
		return tableDisplay;
	}
	//gets the main layout (passed into constructor)
	public FRMainLayout getMainLayout() {
		return mainLayout;
	}
	//gets the edit button on the meta bar
	public Button getEditButton() {
		return selectedBar.getEditButton();
	}
	//allows the longitude and latitude to be set manually
	public void setCenter(double centerLat, double centerLon) {
		leafletMap.setCenter(centerLat, centerLon);
	}
	//allows the zoomLevel to be set manually
	public void setZoomLevel(double zoomLevel) {
		leafletMap.setZoomLevel(zoomLevel);
	}
	//gets the map tiles (note that the satellite tiles are separate from the other map tiles)
	public LTileLayer getTiles(){
		return tiles;
	}
}
