package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRDeleteWayPointConfirmation;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRNewWayPointWindow;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRWayPointPopupView;
import edu.nd.dronology.ui.vaadin.map.LeafletmapFactory;
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
	
	private FRNewWayPointWindow newWayPointWindow;
	private FRWayPointPopupView waypointPopupView;
	private FRDeleteWayPointConfirmation deleteWayPointConfirmation;

	private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout mapAndComponentsLayout = new AbsoluteLayout();
	private FRTableDisplay tableDisplay;
	private FREditModeController editModeController = new FREditModeController(this);
	private FRMetaInfo metaInfo = new FRMetaInfo(this);
	private LMap leafletMap;
	
	public FRMapComponent(FRMainLayout mainLayout) {
		this.mainLayout = mainLayout;
		this.setWidth("100%");

//		LTileLayer tiles = new LTileLayer();
//		tiles.setUrl("VAADIN/sbtiles/{z}/{x}/{y}.png");
//		LTileLayer satelliteTiles = new LTileLayer();
//		satelliteTiles.setUrl("VAADIN/sateltiles/{z}/{x}/{y}.png");
//
//		leafletMap = new LMap();
//		leafletMap.addBaseLayer(tiles, "Main map");
//		leafletMap.addOverlay(satelliteTiles, "Satellite");		
//		leafletMap.setCenter(41.68, -86.25);
//		leafletMap.setZoomLevel(13);
		leafletMap = LeafletmapFactory.generateMap();
		
		
		/* Creates a window that allows the user to input altitude and transit speed information about a newly created waypoint. 
		 * Values are read in and used in the MapMarkerUtilties class. */
		newWayPointWindow = new FRNewWayPointWindow(this);

		/* Creates a popup view that shows information about a waypoint once a mouse over listener is activated corresponding to a waypoint.
		 * Values are set in the MapMarkerUtilities class. */
		waypointPopupView = new FRWayPointPopupView(this);
		
		deleteWayPointConfirmation = new FRDeleteWayPointConfirmation(this);

		mapAndComponentsLayout.addComponents(waypointPopupView, leafletMap, editModeController);
		
		tableDisplay = new FRTableDisplay(this);
		content.addComponents(metaInfo, mapAndComponentsLayout, tableDisplay.getGrid());
		setCompositionRoot(content);

		this.addStyleName("map_component");
		this.addStyleName("fr_map_component");
		mapAndComponentsLayout.addStyleName("fr_mapabsolute_layout");
		leafletMap.addStyleName("fr_leaflet_map");
		leafletMap.addStyleName("bring_back");
		editModeController.addStyleName("bring_front");
		editModeController.setVisible(false);

		utilities = new MapMarkerUtilities(this);
	}

	// Displays with the map and table empty. Called when a route is deleted so that its waypoints are no longer displayed.
	public void displayNoRoute() {
		metaInfo.showInfoWhenNoRouteIsSelected();
		utilities.removeAllPins();
		updateLinesAndGrid();
	}
	
	@WaypointReplace
	public void displayFlightRoute(FlightRouteInfo info) {
		metaInfo.showInfoForSelectedRoute(info);
				
		// Removes old pins and show the new pins
		utilities.removeAllPins();		
		for (Waypoint waypoint : info.getWaypoints()) {
			UIWayPoint way = new UIWayPoint(waypoint);
			utilities.addNewPin(way, -1);
		}

		// redraw the lines to the map.
		updateLinesAndGrid();
		setRouteCenter();
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
	
	// Displays the waypoints in edit mode depending on whether or not the route is new.
	public void updateWayPointCount(MapMarkerUtilities mapUtilities) {
		metaInfo.setNumWaypoints(mapUtilities.getOrderedWayPoints().size());
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

	//TODO: Seems to be buggy...
	// Sets the center of the route based on the stored waypoints such that the map is as visible as possible.
	public void setRouteCenter() {
		if (metaInfo.isAutoZoomingChecked()) {
			// Calculates the mean point and sets the route.
			double meanLat = 0;
			double meanLon = 0;
			int numberPoints;
			double farthestLat = 0;
			double farthestLon = 0;
			double zoom;
			
			List<UIWayPoint> currentWayPoints = utilities.getOrderedWayPoints();
			numberPoints = utilities.getOrderedWayPoints().size();
			
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
	
	// Refreshes the map and grid by removing lines, redrawing them, and then setting the map again.
	public void updateLinesAndGrid() {
		utilities.redrawAllLines(0, false);
		tableDisplay.setGrid(utilities.getOrderedWayPoints());
	}

	public ComponentPosition getWaypointPopupViewPosition() {
		return mapAndComponentsLayout.getPosition(waypointPopupView);
	}	
	public void setWaypointPopupViewPosition(ComponentPosition position) {
		mapAndComponentsLayout.setPosition(waypointPopupView, position);
	}
	
	// Gets the class that represents the utilities.
	public MapMarkerUtilities getMapUtilities() {
		return utilities;
	}	
	public LMap getMap() {
		return leafletMap;
	}
	// Gets the main layout (passed into constructor).
	public FRMainLayout getMainLayout() {
		return mainLayout;
	}
	// Gets the route information bar above the map.
	public FRMetaInfo getMetaInfo() {
		return metaInfo;
	}
	public FREditModeController getEditModeController() {
		return editModeController;
	}
	// Gets the table beneath the map.
	public FRTableDisplay getTableDisplay() {
		return tableDisplay;
	}
	public FRWayPointPopupView getWaypointPopupView () {
		return waypointPopupView;
	}
	public FRNewWayPointWindow getNewWayPointWindow () {
		return newWayPointWindow;
	}
	public FRDeleteWayPointConfirmation getDeleteWayPointConfirmation() {
		return deleteWayPointConfirmation;
	}
}
