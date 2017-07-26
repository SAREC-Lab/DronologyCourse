package edu.nd.dronology.ui.vaadin.activeflights;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.LeafletMouseOverEvent;
import org.vaadin.addon.leaflet.LeafletMouseOverListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.Configuration;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the map component for the Active Flights UI
 * 
 * @author Jinghui Cheng
 */
public class AFMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	private LMap leafletMap;
	private ArrayList<LMarker> markers = new ArrayList<>();
	private Map<String, DroneStatus> drones;
	private Collection<FlightPlanInfo> currentFlights;
	private IDroneSetupRemoteService service;
	private IFlightManagerRemoteService flightRouteService;
	private BaseServiceProvider provider = MyUI.getProvider();
	private List<List<LPolyline>> flightRoutes = new ArrayList<>();
	private List<List<LMarker>> wayPointMarkers = new ArrayList<>();
	private boolean follow = false;
	private boolean followZoom = false;
  private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout followLayout = new AbsoluteLayout();
	private AFFollowBar followBar;
	private AbsoluteLayout layout = new AbsoluteLayout();
	private PopupView popup;
	private PopupView dronePopup;

	private MapMarkerUtilities utilities;

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private FileResource droneIcon = new FileResource(new File(basepath + "/VAADIN/img/drone_icon.png"));
	private FileResource droneIconFocused = new FileResource(new File(basepath + "/VAADIN/img/drone_icon_focused.png"));
	private FileResource droneIconSelected = new FileResource(new File(basepath + "/VAADIN/img/drone_icon_selected.png"));
	private FileResource dotIcon = new FileResource(new File(basepath + "/VAADIN/img/dot.png"));
	
	private AFInfoPanel panel;

	public AFMapComponent(String tileDataURL, String name, AFInfoPanel panel) {
		this.panel = panel;
		
		this.setWidth("100%");
		addStyleName("map_component");
		addStyleName("af_map_component");

		leafletMap = new LMap();
		utilities = new MapMarkerUtilities(leafletMap);

		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);

		leafletMap.addBaseLayer(tiles, name);
		leafletMap.zoomToContent();
		leafletMap.addStyleName("bring_back");

		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
					.getService(IFlightManagerRemoteService.class);
			// currentFlights = flightRouteService.getFlightDetails().getCurrentFlights();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> temp = new ArrayList<>();
		addDroneMarkers("", temp);
		addActiveFlightRoutes("", temp);
		this.setAverageCenter();
		double screenHeight = UI.getCurrent().getPage().getBrowserWindowHeight();
		int layoutHeight = (int) Math.rint(screenHeight * 0.9);
		layout.setHeight(Integer.toString(layoutHeight) + "px");
		layout.addComponent(leafletMap);
		popup = createWayPointPopupView();
		dronePopup = createDronePopupView();
		layout.addComponents(popup, dronePopup);
		content.addComponent(layout);
		setCompositionRoot(content);
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

	@WaypointReplace
	public void addActiveFlightRoutes(String focused, List<String> checked) {
		try {
			currentFlights = flightRouteService.getCurrentFlights();
			for (FlightPlanInfo e : currentFlights) {
				List<Waypoint> coordinates = e.getWaypoints();
				List<WayPoint> wayPoints = new ArrayList<>();
				List<LMarker> wayPointMarker = new ArrayList<>();
				int i = 0;
				for (Waypoint coord : coordinates) {
					Point point = new Point(coord.getCoordinate().getLatitude(), coord.getCoordinate().getLongitude());
					WayPoint wayPoint = new WayPoint(point, nextReached(coordinates, i + 1));
					wayPoints.add(wayPoint);
					if (wayPointMarkers.size() != currentFlights.size()) {
						LMarker marker = new LMarker(point);
						marker.setIcon(dotIcon);
						marker.setIconSize(new Point(15, 15));
						marker.addMouseOverListener( new WaypointMouseListener());
						wayPointMarker.add(marker);
						leafletMap.addComponent(marker);
						if (!follow)
							this.setAverageCenter();
					}
					i++;
				}
				List<LPolyline> polyLines = new ArrayList<>();
				if (e.getDroneId().equals(focused))
					polyLines = utilities.drawLines(wayPoints, true, 2, true);
				else {
					boolean drawn = false;
					for (String name : checked){
						if (e.getDroneId().equals(name)){
							polyLines = utilities.drawLines(wayPoints, true, 1, true);
							drawn = true;
						}
					}
					if (!drawn)
						polyLines = utilities.drawLines(wayPoints, true, 0, true);
				}
				flightRoutes.add(polyLines);
				if (wayPointMarkers.size() != currentFlights.size())
					wayPointMarkers.add(wayPointMarker);
			}
		} catch (RemoteException e) {
			try {
				Notification.show("Reconnecting...");
				service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
				flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);
			} catch (RemoteException | DronologyServiceException e1) {
				// TODO Auto-generated catch block
				Notification.show("Reconnecting...");
			}
			Notification.show("Reconnecting...");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private boolean nextReached(List<Waypoint> coordinates, int i) {
		if (coordinates.size() <= i) {
			return false;
		}
		Waypoint next = coordinates.get(i);
		return next.isReached();
	}

	public void updateActiveFlightRoutes(String focused, List<String> checked) {
		try {
			currentFlights = flightRouteService.getCurrentFlights();
			if (currentFlights.size() != flightRoutes.size() || true) {
				for (List<LPolyline> e : flightRoutes) {
					utilities.removeAllLines(e);
				}
				boolean exists = true;
				for (List<LMarker> e : wayPointMarkers){
					boolean individualExist = false;
					for (FlightPlanInfo q : currentFlights){
						if (e.get(0).getPoint().getLat() == q.getWaypoints().get(0).getCoordinate().getLatitude() && 
								e.get(0).getPoint().getLon() == q.getWaypoints().get(0).getCoordinate().getLongitude()){
							individualExist = true;
						}
					}
					if (individualExist == false)
						exists = false;
				}
				if (!exists || wayPointMarkers.size() != currentFlights.size()) {
					for (List<LMarker> e : wayPointMarkers) {
						utilities.removeAllMarkers(e);
					}
					wayPointMarkers.clear();
					if (!follow)
						this.setAverageCenter();
				}
			}
			flightRoutes.clear();
			/*
			 * if (wayPointMarkers.size() != flightRoutes.size()){ for (ArrayList<LMarker> e:wayPointMarkers){ utilities.removeAllMarkers(e); } wayPointMarkers.clear(); } flightRoutes.clear();
			 */
			this.addActiveFlightRoutes(focused, checked);
			// }

		} catch (RemoteException e) {
			try {
				Notification.show("Reconnecting...");
				service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
				flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);
			} catch (RemoteException | DronologyServiceException e1) {
				// TODO Auto-generated catch block
				Notification.show("Reconnecting...");
			}
			Notification.show("Reconnecting...");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void addDroneMarkers(String focused, List<String> checked) {
		try {
			drones = service.getDrones();
		} catch (RemoteException e) {
			try {
				service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
				flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);
			} catch (RemoteException | DronologyServiceException e1) {
				// TODO Auto-generated catch block
				Notification.show("Reconnecting...");
			}
			Notification.show("Reconnecting...");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		for (Entry<String, DroneStatus> e : drones.entrySet()) {
			LMarker marker = new LMarker(e.getValue().getLatitude(), e.getValue().getLongitude());
			marker.setId(e.getValue().getID());
			if (marker.getId().equals(focused))
				marker.setIcon(droneIconFocused);
			else {
				boolean chosen = false;
				for (String name : checked){
					if (marker.getId().equals(name)){
						marker.setIcon(droneIconSelected);
						chosen = true;
					}
				}
				if (!chosen)
					marker.setIcon(droneIcon);
			}
			marker.setIconSize(new Point(77, 33));
			marker.addMouseOverListener(new DroneMouseListener());
			markers.add(marker);
			leafletMap.addComponent(marker);
			if (!follow)
				this.setAverageCenter();
		}
	}

	public void updateDroneMarkers(String focused, List<String> checked) {
		try {
			drones = service.getDrones();
			ArrayList<LMarker> remove = new ArrayList<>();
			if (markers.size() == drones.size()) {
				for (LMarker marker : markers) {
					boolean exists = false;
					for (Entry<String, DroneStatus> e : drones.entrySet()) {
						if (marker.getId().equals(e.getValue().getID())) {
							Point temp = new Point();
							temp.setLat(e.getValue().getLatitude());
							temp.setLon(e.getValue().getLongitude());
							marker.setPoint(temp);
							if (marker.getId().equals(focused))
								marker.setIcon(droneIconFocused);
							else {
								boolean chosen = false;
								for (String name : checked){
									if (marker.getId().equals(name)){
										marker.setIcon(droneIconSelected);
										chosen = true;
									}
								}
								if (!chosen)
									marker.setIcon(droneIcon);
							}
							exists = true;
						}
					}
					if (!exists) {
						remove.add(marker);
						for (Entry<String, DroneStatus> e1 : drones.entrySet()) {
							boolean old = false;
							for (LMarker marker1 : markers) {
								if (e1.getValue().getID().equals(marker1.getId()))
									old = true;
							}
							if (!old) {
								LMarker newMarker = new LMarker(e1.getValue().getLatitude(), e1.getValue().getLongitude());
								newMarker.setId(e1.getValue().getID());
								if (marker.getId().equals(focused))
									marker.setIcon(droneIconFocused);
								else {
									boolean chosen = false;
									for (String name : checked){
										if (marker.getId().equals(name)){
											marker.setIcon(droneIconSelected);
											chosen = true;
										}
									}
									if (!chosen)
										marker.setIcon(droneIcon);
								}
								newMarker.setIconSize(new Point(77, 33));
								newMarker.addMouseOverListener(new DroneMouseListener());
								markers.add(newMarker);
								leafletMap.addComponent(newMarker);
								if (!follow)
									this.setAverageCenter();
							}
						}
					}
				}
			} else if (markers.size() < drones.size()) {
				for (Entry<String, DroneStatus> e : drones.entrySet()) {
					boolean exists = false;
					for (LMarker marker : markers) {
						if (e.getValue().getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists) {
						LMarker marker = new LMarker(e.getValue().getLatitude(), e.getValue().getLongitude());
						marker.setId(e.getValue().getID());
						if (marker.getId().equals(focused))
							marker.setIcon(droneIconFocused);
						else {
							boolean chosen = false;
							for (String name : checked){
								if (marker.getId().equals(name)){
									marker.setIcon(droneIconSelected);
									chosen = true;
								}
							}
							if (!chosen)
								marker.setIcon(droneIcon);
						}
						marker.setIconSize(new Point(77, 33));
						marker.addMouseOverListener(new DroneMouseListener());
						markers.add(marker);
						leafletMap.addComponent(marker);
						if (!follow)
							this.setAverageCenter();

					}
				}
			} else if (markers.size() > drones.size()) {
				for (LMarker marker : markers) {
					boolean exists = false;
					for (Entry<String, DroneStatus> e : drones.entrySet()) {
						if (e.getValue().getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists)
						remove.add(marker);
				}
			}
			if (remove.size() > 0) {
				for (LMarker e : remove) {
					markers.remove(e);
					leafletMap.removeComponent(e);
					if (!follow)
						this.setAverageCenter();
				}
				remove.clear();
			}
		} catch (RemoteException e) {
			try {
				Notification.show("Reconnecting...");
				service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
				flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);
			} catch (RemoteException | DronologyServiceException e1) {
				// TODO Auto-generated catch block
				Notification.show("Reconnecting...");
			}
			Notification.show("Reconnecting...");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void setAverageCenter() {
	   if (content.getComponentIndex(layout) == -1){
			content.removeAllComponents();
			leafletMap.removeStyleName("af_leaflet_map_edit_mode");
			content.addComponent(layout);
		}
		Configuration configuration = Configuration.getInstance();
		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			drones = service.getDrones();
			double avgLat = 0;
			double avgLon = 0;
			int numPoints = 0;
			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				avgLat += e.getValue().getLatitude();
				avgLon += e.getValue().getLongitude();
				numPoints++;
			}
			currentFlights = flightRouteService.getCurrentFlights();
			for (FlightPlanInfo e : currentFlights) {
				List<Waypoint> coordinates = e.getWaypoints();
				for (Waypoint coord : coordinates) {
					avgLat += coord.getCoordinate().getLatitude();
					avgLon += coord.getCoordinate().getLongitude();
					numPoints++;
				}
			}
			avgLat /= (numPoints * 1.0);
			avgLon /= (numPoints * 1.0);
			double farthestLat = 0;
			double farthestLon = 0;
			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				if (Math.abs(e.getValue().getLatitude() - avgLat) > farthestLat) {
					farthestLat = Math.abs(e.getValue().getLatitude() - avgLat);
				}
				if (Math.abs(e.getValue().getLongitude() - avgLon) > farthestLon) {
					farthestLon = Math.abs(e.getValue().getLongitude() - avgLon);
				}
			}
			for (FlightPlanInfo e : currentFlights) {
				List<Waypoint> coordinates = e.getWaypoints();
				for (Waypoint coord : coordinates) {
					if (Math.abs(coord.getCoordinate().getLatitude() - avgLat) > farthestLat) {
						farthestLat = Math.abs(coord.getCoordinate().getLatitude() - avgLat);
					}
					if (Math.abs(coord.getCoordinate().getLongitude() - avgLon) > farthestLon) {
						farthestLon = Math.abs(coord.getCoordinate().getLongitude() - avgLon);
					}
				}
			}
			Point point = new Point(avgLat, avgLon);
			double zoom;
			if (farthestLat == 0 && farthestLon == 0) {
				zoom = 14;
			} else {
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
			leafletMap.setCenter(point, zoom);
		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (drones.size() < 1) {
			Point point = new Point(configuration.getMapCenterLat(), configuration.getMapCenterLon());
			double zoom = configuration.getMapDefaultZoom();
			leafletMap.setCenter(point, zoom);
		}

	}

	public boolean getFollow() {
		return this.follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}
	
	public boolean getFollowZoom(){
		return this.followZoom;
	}
	
	public void setFollowZoom(boolean followZoom){
		this.followZoom = followZoom;
	}

	public void followDrones(List<String> names) {
		if (names.size() < 1) {
			this.follow = false;
			if (content.getComponentIndex(layout) == -1){
				content.removeAllComponents();
				leafletMap.removeStyleName("af_leaflet_map_edit_mode");
				content.addComponent(layout);
			}
			return;
		}
		if (this.follow == false){
			if (content.getComponentIndex(layout) == -1){
				content.removeAllComponents();
				leafletMap.removeStyleName("af_leaflet_map_edit_mode");
				content.addComponent(layout);
			}
			return;
		}
		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			drones = service.getDrones();
			double avgLat = 0;
			double avgLon = 0;
			int numPoints = 0;
			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				for (String name : names) {
					if (e.getValue().getID().equals(name)) {
						avgLat += e.getValue().getLatitude();
						avgLon += e.getValue().getLongitude();
						numPoints++;
					}
				}
			}
			avgLat /= (numPoints * 1.0);
			avgLon /= (numPoints * 1.0);
			double farthestLat = 0;
			double farthestLon = 0;
			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				for (String name : names) {
					if (e.getValue().getID().equals(name)) {
						if (Math.abs(e.getValue().getLatitude() - avgLat) > farthestLat) {
							farthestLat = Math.abs(e.getValue().getLatitude() - avgLat);
						}
						if (Math.abs(e.getValue().getLongitude() - avgLon) > farthestLon) {
							farthestLon = Math.abs(e.getValue().getLongitude() - avgLon);
						}
					}
				}
			}
			Point point = new Point(avgLat, avgLon);
			if (this.followZoom){
				double zoom;
				if (farthestLat == 0 && farthestLon == 0) { 
					zoom = 17;
				} else {
					zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
				}
			  leafletMap.setCenter(point, zoom);
			  this.followZoom = false;
			}
			else {
				leafletMap.setCenter(point);
			}
			if(content.getComponentIndex(layout) != -1){
				leafletMap.addStyleName("af_leaflet_map_edit_mode");
				followBar = new AFFollowBar(this, names);
				followLayout.addStyleName("af_mapabsolute_layout");
				followBar.addStyleName("bring_front");
				double screenHeight = UI.getCurrent().getPage().getBrowserWindowHeight();
				int layoutHeight = (int) Math.rint(screenHeight * 0.9);
				followLayout.setHeight(Integer.toString(layoutHeight) + "px");
				followLayout.addComponent(layout);
				followLayout.addComponent(followBar);
				content.removeAllComponents();
				content.addComponent(followLayout);
			}
			else{
				followBar.updateUAVList(names);
			}
		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private class DroneMouseListener implements LeafletMouseOverListener {

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {
			try {
				drones = service.getDrones();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			dronePopup.setVisible(false);
			dronePopup.setPopupVisible(false);
			LMarker leafletMarker = (LMarker)event.getSource();

			VerticalLayout popupContent = (VerticalLayout)dronePopup.getContent().getPopupComponent();
			popupContent.removeAllComponents();
			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				if (e.getValue().getID().equals(leafletMarker.getId())) {
					AFInfoBox box = new AFInfoBox(false, e.getValue().getID(), e.getValue().getStatus(),
							e.getValue().getBatteryLevel(), "green", e.getValue().getLatitude(), e.getValue().getLongitude(),
							e.getValue().getAltitude(), e.getValue().getVelocity(), false);
					box.setBoxVisible(false);
					VerticalLayout boxes = panel.getBoxes();
					int numUAVs = panel.getNumUAVS();
					for(int i = 1; i < numUAVs + 1; i++){
						AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
						if (panelBox.getName().equals(box.getName())){
							box.setIsChecked(panelBox.getIsChecked());
							box.setHealthColor(panelBox.getHealthColor());
							box.setHoverInPlace(panelBox.getHoverInPlace());
							box.setStatus(e.getValue().getStatus());
						}
					}
					box.getRouteButton().addClickListener(click -> {
						dronePopup.setPopupVisible(false);
					});
					box.getHomeButton().addClickListener(click -> {
						dronePopup.setPopupVisible(false);
					});
					box.getHoverSwitch().addValueChangeListener(click ->{
						dronePopup.setPopupVisible(false);
					});
					box.getCheckBox().addValueChangeListener(click -> {
						if (box.getCheckBox().getValue()){
							for(int i = 1; i < numUAVs + 1; i++){
								AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
								if (panelBox.getName().equals(box.getName())){
									panelBox.setIsChecked(true);
								}
							}
						}
						else {
							for(int i = 1; i < numUAVs + 1; i++){
								AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
								if (panelBox.getName().equals(box.getName())){
									panelBox.setIsChecked(false);
								}
							}
						}
					});
					popupContent.addComponent(box);
				}
			}
			
			double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
			double mapHeight = UI.getCurrent().getPage().getBrowserWindowHeight() * 0.9;
			
			double xDegreeDifference = -(leafletMap.getCenter().getLon() - leafletMarker.getPoint().getLon());
			double yDegreeDifference = leafletMap.getCenter().getLat() - leafletMarker.getPoint().getLat();
			double degreePerZoom = (360.0/(Math.pow(2, leafletMap.getZoomLevel())));
			double degreePerPixel = degreePerZoom / mapWidth;
			double xPixelDifference = (xDegreeDifference / degreePerPixel) / 3.0;
			double yPixelDifference = (yDegreeDifference / degreePerPixel) / 3.0;

			xPixelDifference = xPixelDifference * 0.55;
			
			double pixelsToLeftBorder = (mapWidth / 2.0) + xPixelDifference;
			double pixelsToTopBorder = (mapHeight / 2.0) + yPixelDifference;
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			double mapTopLeftX = mouseX - pixelsToLeftBorder;
			double mapTopLeftY = mouseY - pixelsToTopBorder;
			
			double adjustedXLocation = mouseX - mapTopLeftX;
			double adjustedYLocation = mouseY - mapTopLeftY;
			
			layout.addComponent(dronePopup, "top:" + String.valueOf((int) adjustedYLocation)
			+ "px;left:" + String.valueOf((int) adjustedXLocation) + "px");
			
			dronePopup.setVisible(true);
			dronePopup.setPopupVisible(true);
			
		}
	}

	private class WaypointMouseListener implements LeafletMouseOverListener {

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {	
			
			popup.setVisible(false);
			popup.setPopupVisible(false);
			LMarker leafletMarker = (LMarker)event.getSource();

			VerticalLayout popupContent = (VerticalLayout)popup.getContent().getPopupComponent();
			Iterator<Component> it = popupContent.iterator();
			while(it.hasNext()) {
				Component c = it.next();
				try {
					currentFlights = flightRouteService.getCurrentFlights();
					for (FlightPlanInfo e : currentFlights) {
						List<Waypoint> coordinates = e.getWaypoints();
						for (Waypoint coord : coordinates) {
							if (coord.getCoordinate().getLatitude() == leafletMarker.getPoint().getLat() && coord.getCoordinate().getLongitude() == leafletMarker.getPoint().getLon()) {
								if (c.getId()!=null && c.getId().equals("latitude")) {
									Label l = (Label)c;
									l.setValue("Latitude: " + coord.getCoordinate().getLatitude());
								}
								if (c.getId()!=null && c.getId().equals("longitude")) {
									Label l = (Label)c;
									l.setValue("Longitude: " + coord.getCoordinate().getLongitude());
								}
								if (c.getId()!=null && c.getId().equals("altitude")) {
									Label l = (Label)c;
									l.setValue("Altitude: "  + coord.getCoordinate().getAltitude());
								}
								if (c.getId()!=null && c.getId().equals("transitSpeed")) {
									Label l = (Label)c;
									l.setValue("Transit Speed: " + coord.getApproachingspeed());
								}
							}
						}
					}
				}catch (RemoteException e) {
					try {
						Notification.show("Reconnecting...");
						service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
						flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
							.getService(IFlightManagerRemoteService.class);
					} catch (RemoteException | DronologyServiceException e1) {
						Notification.show("Reconnecting...");
					}
					Notification.show("Reconnecting...");
				}
			}

			double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
			double mapHeight = UI.getCurrent().getPage().getBrowserWindowHeight() * 0.9;
			
			double xDegreeDifference = -(leafletMap.getCenter().getLon() - leafletMarker.getPoint().getLon());
			double yDegreeDifference = leafletMap.getCenter().getLat() - leafletMarker.getPoint().getLat();
			double degreePerZoom = (360.0/(Math.pow(2, leafletMap.getZoomLevel())));
			double degreePerPixel = degreePerZoom / mapWidth;
			double xPixelDifference = (xDegreeDifference / degreePerPixel) / 3.0;
			double yPixelDifference = (yDegreeDifference / degreePerPixel) / 3.0;

			xPixelDifference = xPixelDifference * 0.55;
			yPixelDifference = yPixelDifference * 0.6;
			
			double pixelsToLeftBorder = (mapWidth / 2.0) + xPixelDifference;
			double pixelsToTopBorder = (mapHeight / 2.0) + yPixelDifference;
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			double mapTopLeftX = mouseX - pixelsToLeftBorder;
			double mapTopLeftY = mouseY - pixelsToTopBorder;
			
			double adjustedXLocation = mouseX - mapTopLeftX;
			double adjustedYLocation = mouseY - mapTopLeftY;
			
			layout.addComponent(popup, "top:" + String.valueOf((int) adjustedYLocation)
			+ "px;left:" + String.valueOf((int) adjustedXLocation) + "px");

			popup.setVisible(true);
			popup.setPopupVisible(true);
		}		
	}
	
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
		
		popupContent.addComponent(latitudeLabel);
		popupContent.addComponent(longitudeLabel);
		popupContent.addComponent(altitudeLabel);
		popupContent.addComponent(transitSpeedLabel);
		
		PopupView popup = new PopupView(null, popupContent);
		
		popup.addStyleName("bring_front");
		popup.setVisible(false);
		popup.setPopupVisible(false);
		
		return popup;
	}
	
	public PopupView createDronePopupView() {
		VerticalLayout popupContent = new VerticalLayout();
		popupContent.removeAllComponents();
		
		popupContent.addComponent(new Label("Drone Information"));
		PopupView popup = new PopupView(null, popupContent);
		
		popup.addStyleName("bring_front");
		popup.setVisible(false);
		popup.setPopupVisible(false);
		return popup;
	}
}