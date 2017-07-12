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
import edu.nd.dronology.ui.vaadin.utils.Configuration;
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

	MapMarkerUtilities route;
	FRTableDisplay tableDisplay = new FRTableDisplay();
	VerticalLayout content = new VerticalLayout();
	AbsoluteLayout mapAndPopup = new AbsoluteLayout();
	FRMetaInfo bar = new FRMetaInfo();
	FReditBar editBar = new FReditBar();
	AbsoluteLayout layout = new AbsoluteLayout();
	FRMetaInfo selectedBar;
	private List<WayPoint> storedPoints = new ArrayList<>();

	public FRMapComponent(String tileDataURL, String name, String satelliteTileDataURL, String satelliteLayerName) {
		this.setWidth("100%");
		addStyleName("map_component");

		leafletMap = new LMap();
		leafletMap.addStyleName("fr_leaflet_map");

		Configuration configuration = Configuration.getInstance();
	
		Window popup = createWayPointWindow();
		route = new MapMarkerUtilities(mapAndPopup, leafletMap, tableDisplay, popup);

		mapAndPopup.addStyleName("fr_mapandpopup_layout");
		mapAndPopup.addComponent(leafletMap);
		tableDisplay.setRoute(route);
		tableDisplay.getGrid().addStyleName("fr_table_component");

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
		
		this.setRouteCenter();
		
	}

	private Window createWayPointWindow() {
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);

		VerticalLayout popupContent = new VerticalLayout();
		TextField altitudeField = new TextField("Altitude: ");
		TextField transitSpeedField = new TextField("Transit Speed: ");

		popupContent.addComponent(altitudeField);
		popupContent.addComponent(transitSpeedField);
		popupContent.addComponent(buttons);

		Window popup;
		popup = new Window(null, popupContent);

		popup.setModal(true);
		popup.setClosable(false);
		popup.setResizable(false);

		return popup;
	}

	public void display() {
		//displays when no route is selected
		mapAndPopup.setHeight("510px");
		mapAndPopup.setWidth("1075px");
		content.addComponent(bar);
		content.addComponents(mapAndPopup, tableDisplay.getGrid());
	}

	@WaypointReplace
	public void displayByName(FlightRouteInfo info, String routeName, int numCoords, boolean whichName) {
		//displays with selected route info
		
		route.disableRouteEditing();

		layout = new AbsoluteLayout();
		layout.setHeight("447px");
		layout.setWidth("1075px");

		
		if (whichName) {
			int numWaypoints = route.getMapPoints().size();
			selectedBar = new FRMetaInfo(routeName, numCoords);
		} else {
			selectedBar = new FRMetaInfo(info);
		}
		 

		editBar = new FReditBar();
		editBar.setStyleName("edit_bar");
		CheckBox tableBox = selectedBar.getCheckBox();
		Button edit = selectedBar.getEditButton();
		
		//to hide the table 
		tableBox.addValueChangeListener(event -> {

			if (tableBox.getValue()) {
				displayTable();
			} else {
				displayNoTable();
			}
		});

		leafletMap.setStyleName("bring_back");

		// enable editing
		edit.addClickListener(event -> {
			for (int i = 0; i < route.getMapPoints().size(); i++) {
				storedPoints.add(route.getMapPoints().get(i));
			}
			
			route.enableRouteEditing();
			leafletMap.setEnabled(true);
			editBar.addStyleName("bring_front");
			editBar.setWidth("880px");
			layout.addComponent(editBar, "top: 5px; left:95px");

			leafletMap.addStyleName("fr_leaflet_map_edit_mode");
			tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
			
		});

		Button cancel = editBar.getCancelButton();
		cancel.addClickListener(event -> {
			route.disableRouteEditing();
			
			for (int i = 0; i < route.getMapPoints().size(); i++) {
				route.getMapPoints().remove(i);
			}
			
			route.getMapPoints().clear();
			
			for (int i = 0; i < storedPoints.size(); i++) {
				route.getMapPoints().add(storedPoints.get(i));
			}
			
			route.getGrid().setItems(route.getMapPoints());

			layout.removeComponent(editBar);
			leafletMap.setStyleName("fr_leaflet_map");
			leafletMap.addStyleName("bring_back");
			tableDisplay.getGrid().setStyleName("fr_table_component");
			leafletMap.setEnabled(false);
		});

		Button save = editBar.getSaveButton();
		save.addClickListener(event -> {

			route.disableRouteEditing();

			layout.removeComponent(editBar);
			leafletMap.setStyleName("fr_leaflet_map");
			leafletMap.addStyleName("bring_back");
			tableDisplay.getGrid().setStyleName("fr_table_component");
			leafletMap.setEnabled(false);

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
				id = info.getId();
				name = info.getName();

				byte[] information = service.requestFromServer(id);
				inStream = new ByteArrayInputStream(information);
				froute = routePersistor.loadItem(inStream);

				ArrayList<Waypoint> oldCoords = new ArrayList(froute.getWaypoints());
				for (Waypoint cord : oldCoords) {
					froute.removeWaypoint(cord);
				}
				
				for (WayPoint way : newWaypoints) {
					double alt = 0;
					double lon = 0;
					double lat = 0;
				
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
					
					froute.addWaypoint(new Waypoint(new LlaCoordinate(lat, lon, alt)));
					
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

		});
		
		layout.addComponent(mapAndPopup, "top:5px; left:5px");

		content.removeAllComponents();
		content.addComponent(selectedBar);
		content.addComponents(layout, tableDisplay.getGrid());
		
		tableDisplay.setGrid(route.getMapPoints());
		
	}
	
	public void displayStillEdit(FlightRouteInfo info, String routeName, int numCoords, boolean whichName){
		
		displayByName(info, routeName, numCoords, whichName);
		
		route.enableRouteEditing();
		leafletMap.setEnabled(true);
		editBar.addStyleName("bring_front");
		editBar.setWidth("880px");
		layout.addComponent(editBar, "top: 5px; left:95px");

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");
		
	}
	
	public void displayNoTable() {
		content.removeComponent(tableDisplay.getGrid());
	}

	public void displayTable() {
		content.addComponent(tableDisplay.getGrid());
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

//		route.enableRouteEditing();
//		leafletMap.setEnabled(true);
		editBar.addStyleName("bring_front");
		editBar.setWidth("880px");
		layout.addComponent(editBar, "top: 5px; left:95px");

		leafletMap.addStyleName("fr_leaflet_map_edit_mode");
		tableDisplay.getGrid().addStyleName("fr_table_component_edit_mode");

		route.enableRouteEditing();
	}
	public void setRouteCenter(){
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
	public FRMetaInfo getMetaBar(){
		return selectedBar;
	}
	public Button getEditButton(){
		return selectedBar.getEditButton();
	}
}
