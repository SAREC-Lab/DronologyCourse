package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
 * This is the map component for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	private LMap leafletMap;

	private MapMarkerUtilities route;
	private FRTableDisplay tableDisplay = new FRTableDisplay();
	private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout mapAndPopup = new AbsoluteLayout();
	private FRMetaInfo bar = new FRMetaInfo();
	private FReditBar editBar = new FReditBar(this);
	private AbsoluteLayout layout = new AbsoluteLayout();
	private FRMetaInfo selectedBar;
	private List<WayPoint> storedPoints = new ArrayList<>();
	private FRDeleteRoute delete = new FRDeleteRoute(this);
	private ArrayList<String> names = new ArrayList<>();
	private PopupView popup;
	private FlightRouteInfo selectedRoute;
	private FRMainLayout mainLayout;
	private boolean toDo = true;

	public FRMapComponent(String tileDataURL, String name, String satelliteTileDataURL, String satelliteLayerName, FRMainLayout layout, boolean toDo) {
		this.setWidth("100%");
		addStyleName("map_component");
		addStyleName("fr_map_component");

		leafletMap = new LMap();
		leafletMap.addStyleName("fr_leaflet_map");
	
		Window window = createWayPointWindow();
		PopupView popup = createWayPointPopupView();
		mapAndPopup.addComponent(popup);
		route = new MapMarkerUtilities(mapAndPopup, leafletMap, tableDisplay, window, popup, this);

		mapAndPopup.addStyleName("fr_mapabsolute_layout");
		mapAndPopup.addComponent(leafletMap);
		tableDisplay.setRoute(route);

		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);

		LTileLayer satelliteTiles = new LTileLayer();
		satelliteTiles.setUrl(satelliteTileDataURL);

		leafletMap.addBaseLayer(tiles, name);
		leafletMap.addOverlay(satelliteTiles, satelliteLayerName);
		content.addComponent(mapAndPopup);

		route.disableRouteEditing();

		setCompositionRoot(content);
		content.addComponents(tableDisplay.getGrid());
		mainLayout = layout;
		
		this.toDo = toDo;
		
		this.setRouteCenter(toDo);
	}

	private Window createWayPointWindow() {
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);

		VerticalLayout windowContent = new VerticalLayout();
		TextField altitudeField = new TextField("Altitude: ");
		TextField transitSpeedField = new TextField("Transit Speed: ");

		windowContent.addComponent(altitudeField);
		windowContent.addComponent(transitSpeedField);
		windowContent.addComponent(buttons);

		Window window;
		window = new Window(null, windowContent);

		window.setModal(true);
		window.setClosable(false);
		window.setResizable(false);

		return window;
	}

	public PopupView createWayPointPopupView() {
		//popup that displays on pin mouse-over. Contains button allowing for the waypoint to be deleted
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
		
		popupContent.addComponent(latitudeLabel);
		popupContent.addComponent(longitudeLabel);
		popupContent.addComponent(altitudeLabel);
		popupContent.addComponent(transitSpeedLabel);
		
		popup = new PopupView(null, popupContent);
		
		Button toDelete = new Button("Remove Waypoint");
		toDelete.addClickListener(event -> {
			popup.setPopupVisible(false);
			
			for (int i = 0; i < route.getMapPoints().size(); i++) {
				if (route.getMapPoints().get(i).getId().equals(route.getSelectedWayPointId())) {
					route.getMapPoints().remove(route.getMapPoints().get(i));
				}
			}
			
			route.getMap().removeComponent(route.getLeafletMarker());
			route.removeAllLines(route.getPolylines());
			route.drawLines(route.getMapPoints(), true, 1, false);
			route.getGrid().setItems(route.getMapPoints());
			onMapEdited(storedPoints);
			
			for (int i = 0; i < route.getMapPoints().size(); i++) {
				route.getMapPoints().get(i).setOrder(i+1);
			}
		});
		
		toDelete.setId("toDelete");
		popupContent.addComponent(toDelete);
		
		popup.addStyleName("bring_front");
		popup.setVisible(false);
		popup.setPopupVisible(false);
		
		return popup;
	}
	
	public void display() {
		//displays when no route is selected
		content.addComponent(bar);
		content.addComponents(mapAndPopup, tableDisplay.getGrid());
	}
	public void displayNoRoute(){
		//displays with the map and table empty. Called when a route is deleted so that its waypoints are no longer displayed
		content.removeAllComponents();
		content.addComponent(bar);
		content.addComponents(mapAndPopup, tableDisplay.getGrid());
			
		route.getMapPoints().clear();
		route.getGrid().setItems(route.getMapPoints());
		
		route.removeAllMarkers(route.getPins());
		route.removeAllLines(route.getPolylines());
	}
	
	@WaypointReplace
	public void displayByName(FlightRouteInfo info, String routeName, int numCoords, boolean whichName, boolean toDo) {
		//displays with selected route info based on either the FlightRouteInfo object or the name and coordinate number passed in
		selectedRoute = info;
		route.disableRouteEditing();

		layout = new AbsoluteLayout();
		layout.addStyleName("fr_mapabsolute_layout");
		
		if (whichName) {
			selectedBar = new FRMetaInfo(routeName, numCoords, this, toDo);
		} else {
			selectedBar = new FRMetaInfo(info, this, toDo);
		}
		
		getMetaInfo().getAutoZooming().addValueChangeListener(event -> {
			this.toDo = getMetaInfo().getAutoZooming().getValue();
		});
		
		editBar = new FReditBar(this);
		CheckBox tableBox = selectedBar.getCheckBox();
		
		//to hide the table 
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
		
		tableDisplay.setGrid(route.getMapPoints());	
	}
	
	public void displayStillEdit(FlightRouteInfo info, String routeName, int numCoords, boolean whichName, boolean toDo){
		//Displays the route determined by the FlightRouteInfo object and then re-enables edit mode
		displayByName(info, routeName, numCoords, whichName, toDo);
		
		route.enableRouteEditing();
		leafletMap.setEnabled(true);
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
	}
	
	public void displayNoTable() {
		//removes the grid and changes the style of the map accordingly
		content.removeComponent(tableDisplay.getGrid());
		this.addStyleName("fr_map_component_no_table");
	}

	public void displayTable() {
		//adds the grid
		content.addComponent(tableDisplay.getGrid());
		this.removeStyleName("fr_map_component_no_table");
	}
	
	public void setCenter(double centerLat, double centerLon) {
		leafletMap.setCenter(centerLat, centerLon);
	}
	
	public void setZoomLevel(double zoomLevel) {
		leafletMap.setZoomLevel(zoomLevel);
	}

	public double getCenterLat() {
		return leafletMap.getCenter().getLat();
	}

	public double getCenterLon() {
		return leafletMap.getCenter().getLon();
	}

	public double getZoomLevel() {
		return leafletMap.getZoomLevel();
	}

	public LMap getMapInstance() {
		return leafletMap;
	}

	public MapMarkerUtilities getUtils() {
		return route;
	}

	public FRTableDisplay getTableDisplay() {
		return tableDisplay;
	}

	public void enableEdit() {
		//enables editing, adds the edit bar,  and calls the enableRouteEditing function from the MapMarkerUtilities class
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");

		route.enableRouteEditing();
	}
	public void exitEditMode() {
		//disables editing, removes the edit bar, and changes the component styles accordingly
		route.disableRouteEditing();

		layout.removeComponent(editBar);
		leafletMap.setStyleName("fr_leaflet_map");
		leafletMap.addStyleName("bring_back");
		tableDisplay.getGrid().setStyleName("fr_table_component");
	}
	public void setRouteCenter(boolean toDo){
		if (toDo) {
			//calculates the mean point and sets the route
			double meanLat = 0;
			double meanLon = 0;
			int numberPoints;
			double farthestLat = 0;
			double farthestLon = 0;
			double zoom;
			
			List<WayPoint> currentWayPoints = route.getMapPoints();
			numberPoints = route.getMapPoints().size();
			
			for(WayPoint p: currentWayPoints){
					meanLat += Double.valueOf(p.getLatitude());
					meanLon += Double.valueOf(p.getLongitude());
			}
			
			meanLat /= (numberPoints * 1.0);
			meanLon /= (numberPoints * 1.0);
			
			//finds farthest latitude and longitude from mean
			for(WayPoint p: currentWayPoints){
				if((Math.abs(Double.valueOf(p.getLatitude()) - meanLat) > farthestLat)){
					farthestLat = (Math.abs((Double.valueOf(p.getLatitude())) - meanLat));
				}
				if((Math.abs(Double.valueOf(p.getLongitude()) - meanLon) > farthestLon)){
					farthestLon = (Math.abs((Double.valueOf(p.getLongitude()) - meanLon)));
				}
			}  
			
			Point centerPoint = new Point(meanLat, meanLon);
			if(farthestLat == 0 && farthestLon == 0){
				zoom = 17;
			}else{
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
				
			leafletMap.setCenter(centerPoint, zoom+1);
		}
		if (selectedBar != null)
			selectedBar.getAutoZooming().setValue(toDo);
	}
	public FRMetaInfo getMetaBar(){
		return selectedBar;
	}
	public Button getEditButton(){
		return selectedBar.getEditButton();
	}
	public FRDeleteRoute getDeleteRouteWindow(){
		return delete;
	}
	public void editButton(){
		//called when the edit button is clicked. Stores the current contents of route.getMapPoints() in case the changes are reverted
		storedPoints.clear();
		for (int i = 0; i < route.getMapPoints().size(); i++) {
			storedPoints.add(route.getMapPoints().get(i));
		}
		
		route.enableRouteEditing();
		leafletMap.setEnabled(true);
		editBar.addStyleName("bring_front");
		layout.addComponent(editBar);

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
	}
	public void cancelClick(){
		//called when the cancel button is clicked. Disables editing and reverts changes back to the contents of storedPoints
		route.disableRouteEditing();
		
		for (int i = 0; i < route.getMapPoints().size(); i++) {
			route.getMapPoints().remove(i);
		}
		
		route.getMapPoints().clear();
		
		for (int i = 0; i < storedPoints.size(); i++) {
			route.getMapPoints().add(storedPoints.get(i));
		}
		
		route.getGrid().setItems(route.getMapPoints());
		
		route.removeAllMarkers(route.getPins());
		route.removeAllLines(route.getPolylines());
		
		for (int i = 0; i < storedPoints.size(); i++) {
			WayPoint point = storedPoints.get(i);
			route.addPinForWayPoint(point);
		}
		
		route.drawLines(storedPoints, true, 0, false);
		
		route.disableRouteEditing();
		
		layout.removeComponent(editBar);
		leafletMap.addStyleName("bring_back");
		leafletMap.removeStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().removeStyleName("fr_table_component_edit_mode");
	}
	public void deleteClick(){
		//called when the delete button is clicked. It passes the correct FlightRouteInfo object to the FRDeleteRoute class
		delete.setRouteInfoTobeDeleted(selectedRoute);
		UI.getCurrent().addWindow(delete.getWindow());
	}
	public void saveClick(){
		//called when the save button on the edit bar is clicked. It exits edit mode, sends the points to dronology, and uses stored points to display the correct waypoints on the map
		
		exitEditMode();
		
		List<WayPoint> newWaypoints = route.getMapPoints();
		
		FlightRoutePersistenceProvider routePersistor = FlightRoutePersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IFlightRoute froute;

		IFlightRouteplanningRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		ArrayList routeList;

		//sends the information to dronology to be saved
		try {
			service = (IFlightRouteplanningRemoteService) provider.getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);

			String id;
			String name;

			// gets routes from dronology and requests their name/id
			id = selectedRoute.getId();
			name = selectedRoute.getName();

			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			froute = routePersistor.loadItem(inStream);

			ArrayList<Waypoint> oldCoords = new ArrayList<>(froute.getWaypoints());
			for (Waypoint cord : oldCoords) {
				froute.removeWaypoint(cord);
			}
			
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
		
		//tests if points were added or deleted. If added, an identical ArrayList of waypoints is created (this is a workaround to remove the click-listeners)
		if(storedPoints.size() < route.getMapPoints().size()){
			storedPoints.clear();
			for(int i = 0; i < route.getMapPoints().size(); i++){
				String alt = route.getMapPoints().get(i).getAltitude();
				String lon = route.getMapPoints().get(i).getLongitude();
				String lat = route.getMapPoints().get(i).getLatitude();
				String trans = route.getMapPoints().get(i).getTransitSpeed();
				
				Point pt = new Point();
				
				pt.setLat(Double.valueOf(lat));
				pt.setLon(Double.valueOf(lon));
				
				WayPoint way = new WayPoint(pt, false);
				way.setAltitude(alt);
				way.setTransitSpeed(trans);
				
				storedPoints.add(way);
			}
			
			for (int i = 0; i < route.getMapPoints().size(); i++) {
				route.getMapPoints().remove(i);
			}
			route.getMapPoints().clear();
			//waypoints are re-loaded into mapPoints, but without click listeners
			for (int i = 0; i < storedPoints.size(); i++) {
				route.getMapPoints().add(storedPoints.get(i));
			}
			//then, the map has the points and lines redrawn
			route.getGrid().setItems(route.getMapPoints());
			
			route.removeAllMarkers(route.getPins());
			route.removeAllLines(route.getPolylines());
			
			for (int i = 0; i < storedPoints.size(); i++) {
				WayPoint point = storedPoints.get(i);
				route.addPinForWayPoint(point);
			}
			
			route.drawLines(storedPoints, true, 0, false);
		}
		route.disableRouteEditing();
	}
	public FRMainLayout getMainLayout(){
		return mainLayout;
	}
	public void onMapEdited(List<WayPoint> waypoints) {
		bar.setNumWaypoints(waypoints.size());
		if (mainLayout.isNew()) {
			displayStillEdit(mainLayout.getDrone(), mainLayout.getDroneName(), route.getMapPoints().size(), true, toDo);
		}
		else {
			displayStillEdit(mainLayout.getFlightInfo(), mainLayout.getFlightInfo().getName(), route.getMapPoints().size(), true, toDo);
		}
	}
	public FRMetaInfo getMetaInfo() {
		return selectedBar;
	}
	public boolean getToDo() {
		return toDo;
	}
}
