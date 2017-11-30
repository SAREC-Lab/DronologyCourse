package edu.nd.dronology.ui.vaadin.activeflights;

import java.awt.MouseInfo;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletMouseOverEvent;
import org.vaadin.addon.leaflet.LeafletMouseOverListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.Configuration;
import edu.nd.dronology.ui.vaadin.utils.MapMarkerUtilities;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the map component for the Active Flights UI
 * 
 * @author Jinghui Cheng
 * 
 */
public class AFMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	private LMap leafletMap;
	private ArrayList<LMarker> markers = new ArrayList<>();
	private Collection<IUAVProxy> drones;
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

	/**
	 * This function gets the flight routes from dronology core and draws them on the map.
	 * 
	 * @param focused
	 *          this is the drone that is focused in the AFInfoPanel. It's flight route will be orange
	 * @param checked
	 *          this is a list of drones that have their checkbox checked in the AFInfoPanel. Their routes will be black.
	 */
	@WaypointReplace
	public void addActiveFlightRoutes(String focused, List<String> checked) {
		try {
			currentFlights = flightRouteService.getCurrentFlights();
			for (FlightPlanInfo e : currentFlights) { // goes through each route
				List<Waypoint> coordinates = e.getWaypoints();
				List<UIWayPoint> wayPoints = new ArrayList<>();
				List<LMarker> wayPointMarker = new ArrayList<>();
				int i = 0;
				for (Waypoint coord : coordinates) { // goes through all the coordinates in each route
					Point point = new Point(coord.getCoordinate().getLatitude(), coord.getCoordinate().getLongitude());
					UIWayPoint wayPoint = new UIWayPoint(point, nextReached(coordinates, i + 1));
					wayPoints.add(wayPoint);
					if (wayPointMarkers.size() != currentFlights.size()) { // adds the waypoints to the map first
						LMarker marker = new LMarker(point);
						marker.setIcon(dotIcon);
						marker.setIconSize(new Point(15, 15));
						marker.addMouseOverListener(new WaypointMouseListener());
						wayPointMarker.add(marker);
						leafletMap.addComponent(marker);
					}
					i++;
				}
				List<LPolyline> polyLines = new ArrayList<>(); // draws the lines and loads them into a list
				if (e.getDroneId().equals(focused)) {
					utilities.removeAllLines();
					polyLines = utilities.drawLinesForWayPoints(wayPoints, 2, true);
				} else {
					boolean drawn = false;
					for (String name : checked) {
						if (e.getDroneId().equals(name)) {
							utilities.removeAllLines();
							polyLines = utilities.drawLinesForWayPoints(wayPoints, 1, true);
							drawn = true;
						}
					}
					if (!drawn) {
						utilities.removeAllLines();
						polyLines = utilities.drawLinesForWayPoints(wayPoints, 0, true);
					}
				}
				flightRoutes.add(polyLines); // keep a list of all lines and markers
				if (wayPointMarkers.size() != currentFlights.size())
					wayPointMarkers.add(wayPointMarker);
			}
		} catch (RemoteException e) { // reconnect to dronology if connection is lost
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

	/**
	 * assists in the logic of updating flight routes
	 * 
	 * @param coordinates
	 * @param i
	 * @return
	 */
	@SuppressWarnings("static-method")
	private boolean nextReached(List<Waypoint> coordinates, int i) {
		if (coordinates.size() <= i) {
			return false;
		}
		Waypoint next = coordinates.get(i);
		return next.isReached();
	}

	/**
	 * updates the flight routes. Deletes old ones, adds new ones, and redraws the lines to different colors as each waypoint is reached
	 * 
	 * @param focused
	 *          this is the drone that is focused in the AFInfoPanel. It's flight route will be orange
	 * @param checked
	 *          this is a list of drones that have their checkbox checked in the AFInfoPanel. Their routes will be black.
	 */
	public void updateActiveFlightRoutes(String focused, List<String> checked) {
		try {
			currentFlights = flightRouteService.getCurrentFlights();
			if (currentFlights.size() != flightRoutes.size() || true) {
				utilities.removeAllLines();
				boolean exists = true; // determines if flight route is still active
				for (List<LMarker> e : wayPointMarkers) {
					boolean individualExist = false; // helper variable to determine if each flight route is still active
					for (FlightPlanInfo q : currentFlights) {
						if (e.get(0).getPoint().getLat() == q.getWaypoints().get(0).getCoordinate().getLatitude()
								&& e.get(0).getPoint().getLon() == q.getWaypoints().get(0).getCoordinate().getLongitude()) {
							individualExist = true;
						}
					}
					if (individualExist == false)
						exists = false;
				}
				if (!exists || wayPointMarkers.size() != currentFlights.size()) { // if flight doesn't exist, remove it's waypoint markers
					for (List<LMarker> lmarkers : wayPointMarkers) {
						for (LMarker m : lmarkers) {
							utilities.getMap().removeComponent(m);
						}
					}
					wayPointMarkers.clear();
					if (!follow && flightRoutes.size() < currentFlights.size()) // only reset the center when a flight route is added
						this.setAverageCenter();
				}
			}
			flightRoutes.clear();
			/*
			 * if (wayPointMarkers.size() != flightRoutes.size()){ for (ArrayList<LMarker> e:wayPointMarkers){ utilities.removeAllMarkers(e); } wayPointMarkers.clear(); } flightRoutes.clear();
			 */
			this.addActiveFlightRoutes(focused, checked); // redraw the flight routes
			// }

		} catch (RemoteException e) { // reconnect to dronology
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

	/**
	 * This function adds icons on the map that represent each drone's position.
	 * 
	 * @param focused
	 *          this is the drone that is focused in the AFInfoPanel. It's flight route will be orange
	 * @param checked
	 *          this is a list of drones that have their checkbox checked in the AFInfoPanel. Their routes will be black.
	 */
	@SuppressWarnings("deprecation")
	public void addDroneMarkers(String focused, List<String> checked) {
		try {
			drones = service.getActiveUAVs();
		} catch (RemoteException e) {
			try {
				service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
				flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);
			} catch (RemoteException | DronologyServiceException e1) {
				Notification.show("Reconnecting...");
			}
			Notification.show("Reconnecting...");
		}
		for (IUAVProxy e : drones) {
			LMarker marker = new LMarker(e.getLatitude(), e.getLongitude());
			marker.setId(e.getID());
			if (marker.getId().equals(focused))
				marker.setIcon(droneIconFocused);
			else {
				boolean chosen = false;
				for (String name : checked) {
					if (marker.getId().equals(name)) {
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

	/**
	 * This function updates the position of the drone icons on the map
	 * 
	 * @param focused
	 *          this is the drone that is focused in the AFInfoPanel. It's flight route will be orange
	 * @param checked
	 *          this is a list of drones that have their checkbox checked in the AFInfoPanel. Their routes will be black.
	 */
	@SuppressWarnings("deprecation")
	public void updateDroneMarkers(String focused, List<String> checked) {
		try {
			drones = service.getActiveUAVs();
			ArrayList<LMarker> remove = new ArrayList<>();
			if (markers.size() == drones.size()) {
				for (LMarker marker : markers) {
					boolean exists = false;
					for (IUAVProxy e : drones) {
						if (marker.getId().equals(e.getID())) { // if the marker correlates to the drone
							Point temp = new Point();
							temp.setLat(e.getLatitude()); // update location
							temp.setLon(e.getLongitude());
							marker.setPoint(temp);
							if (marker.getId().equals(focused))
								marker.setIcon(droneIconFocused);
							else {
								boolean chosen = false;
								for (String name : checked) {
									if (marker.getId().equals(name)) {
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
					if (!exists) { // if the drone that is represented by the marker is no longer active or if the drone is new
						remove.add(marker);
						for (IUAVProxy e1 : drones) {
							boolean old = false;
							for (LMarker marker1 : markers) {
								if (e1.getID().equals(marker1.getId()))
									old = true;
							}
							if (!old) { // the drone does not have a marker represented by it
								LMarker newMarker = new LMarker(e1.getLatitude(), e1.getLongitude());
								newMarker.setId(e1.getID());
								if (marker.getId().equals(focused))
									marker.setIcon(droneIconFocused);
								else {
									boolean chosen = false;
									for (String name : checked) {
										if (marker.getId().equals(name)) {
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
				for (IUAVProxy e: drones) {
					boolean exists = false;
					for (LMarker marker : markers) {
						if (e.getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists) {
						LMarker marker = new LMarker(e.getLatitude(), e.getLongitude()); // create new marker for the drone
						marker.setId(e.getID());
						if (marker.getId().equals(focused))
							marker.setIcon(droneIconFocused);
						else {
							boolean chosen = false;
							for (String name : checked) {
								if (marker.getId().equals(name)) {
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
					for (IUAVProxy e : drones) {
						if (e.getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists) // remove marker that represents a deactivated drone
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
		} catch (RemoteException e) { // reconnect to dronology
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

	/**
	 * This function sets the center and zoom of the map to include all drones and their flight routes. It finds the average latitude and longitude first. It then finds the point farthest from the
	 * center and bases the zoom level off of that point.
	 */
	@SuppressWarnings("deprecation")
	public void setAverageCenter() {
		if (content.getComponentIndex(layout) == -1) { // if coming out of follow mode
			content.removeAllComponents();
			leafletMap.removeStyleName("af_leaflet_map_edit_mode");
			content.addComponent(layout);
		}
		Configuration configuration = Configuration.getInstance();
		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			drones = service.getActiveUAVs();
			double avgLat = 0;
			double avgLon = 0;
			int numPoints = 0;
			for ( IUAVProxy e : drones) { // finding the average latitude and longitude of the drones and flight routes
				avgLat += e.getLatitude();
				avgLon += e.getLongitude();
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
			double farthestLon = 0; // finding the farthest point from the center
			for (IUAVProxy e : drones) {
				if (Math.abs(e.getLatitude() - avgLat) > farthestLat) {
					farthestLat = Math.abs(e.getLatitude() - avgLat);
				}
				if (Math.abs(e.getLongitude() - avgLon) > farthestLon) {
					farthestLon = Math.abs(e.getLongitude() - avgLon);
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
			} else { // sets the zoom based on the calculation of degrees on the map per zoom level
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
			leafletMap.setCenter(point, zoom);
		} catch (RemoteException | DronologyServiceException e1) {
			e1.printStackTrace();
		}
		if (drones.size() < 1) {
			Point point = new Point(configuration.getMapCenterLat(), configuration.getMapCenterLon());
			double zoom = configuration.getMapDefaultZoom();
			leafletMap.setCenter(point, zoom);
		}

	}

	/**
	 * @return follow is a boolean variable that is true when the map is following drones
	 */
	public boolean getFollow() {
		return this.follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	/**
	 * @return followZoom determines whether the map should zoom in on the drones in follow mode. Only happens once initially when the user clicks the button to follow the drones on the map.
	 */
	public boolean getFollowZoom() {
		return this.followZoom;
	}

	public void setFollowZoom(boolean followZoom) {
		this.followZoom = followZoom;
	}

	/**
	 * This function sets the center of the map as an average of the drones that it is following. This will constantly change as each drone flies.
	 * 
	 * @param names
	 *          The list of drone names that the map should be following
	 */
	@SuppressWarnings("deprecation")
	public void followDrones(List<String> names) {
		if (names.size() < 1) {
			this.follow = false;
			if (content.getComponentIndex(layout) == -1) { // if not in follow mode
				content.removeAllComponents();
				leafletMap.removeStyleName("af_leaflet_map_edit_mode");
				content.addComponent(layout);
			}
			return;
		}
		if (this.follow == false) {
			if (content.getComponentIndex(layout) == -1) { // if not in follow mode
				content.removeAllComponents();
				leafletMap.removeStyleName("af_leaflet_map_edit_mode");
				content.addComponent(layout);
			}
			return;
		}
		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			drones = service.getActiveUAVs();
			double avgLat = 0; // finds the average latitude and longitude
			double avgLon = 0;
			int numPoints = 0;
			for (IUAVProxy e : drones) {
				for (String name : names) {
					if (e.getID().equals(name)) {
						avgLat += e.getLatitude();
						avgLon += e.getLongitude();
						numPoints++;
					}
				}
			}
			avgLat /= (numPoints * 1.0);
			avgLon /= (numPoints * 1.0);
			double farthestLat = 0; // finds the farthest point from the center
			double farthestLon = 0;
			for (IUAVProxy e : drones) {
				for (String name : names) {
					if (e.getID().equals(name)) {
						if (Math.abs(e.getLatitude() - avgLat) > farthestLat) {
							farthestLat = Math.abs(e.getLatitude() - avgLat);
						}
						if (Math.abs(e.getLongitude() - avgLon) > farthestLon) {
							farthestLon = Math.abs(e.getLongitude() - avgLon);
						}
					}
				}
			}
			Point point = new Point(avgLat, avgLon);
			if (this.followZoom) { // if the first time after the button click, set the zoom level to fit all drones
				double zoom;
				if (farthestLat == 0 && farthestLon == 0) {
					zoom = 17;
				} else {
					zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
				}
				leafletMap.setCenter(point, zoom);
				this.followZoom = false;
			} else {
				leafletMap.setCenter(point);
			}
			if (content.getComponentIndex(layout) != -1) { // change the map layout to display the follow bar.
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
			} else {
				followBar.updateUAVList(names);
			}
		} catch (RemoteException | DronologyServiceException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * this is a listener that displays an AFInfoBox in a popup over the drone that was just hovered over
	 * 
	 * @author Patrick Falvey
	 *
	 */
	private class DroneMouseListener implements LeafletMouseOverListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {
			try {
				drones = service.getActiveUAVs();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			dronePopup.setVisible(false);
			dronePopup.setPopupVisible(false);
			LMarker leafletMarker = (LMarker) event.getSource();

			VerticalLayout popupContent = (VerticalLayout) dronePopup.getContent().getPopupComponent();
			popupContent.removeAllComponents();
			for (IUAVProxy e : drones) { // change the popup content to display the right AFInfoBox
				if (e.getID().equals(leafletMarker.getId())) {
					AFInfoBox box = new AFInfoBox(false, e.getID(), e.getStatus(),
							e.getBatteryLevel(), "green", e.getLatitude(), e.getLongitude(),
							e.getAltitude(), e.getVelocity(), false);
					box.setBoxVisible(false);
					VerticalLayout boxes = panel.getBoxes();
					int numUAVs = panel.getNumUAVS();
					for (int i = 1; i < numUAVs + 1; i++) {
						AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
						if (panelBox.getName().equals(box.getName())) { // get the updated information from the AFInfoPanel
							box.setIsChecked(panelBox.getIsChecked());
							box.setHealthColor(panelBox.getHealthColor());
							box.setHoverInPlace(panelBox.getHoverInPlace());
							box.setStatus(e.getStatus());
						}
					}
					box.getRouteButton().addClickListener(click -> {
						dronePopup.setPopupVisible(false);
					});
					box.getHomeButton().addClickListener(click -> {
						dronePopup.setPopupVisible(false);
					});
					box.getHoverSwitch().addValueChangeListener(click -> {
						for (int i = 1; i < numUAVs + 1; i++) {
							AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
							if (panelBox.getName().equals(box.getName())) {
								panelBox.setHoverInPlace(box.getHoverInPlace());
							}
						}
					});
					box.getCheckBox().addValueChangeListener(click -> { // if checkbox clicked in popup, it will change in AFInfoPanel
						if (box.getCheckBox().getValue()) {
							for (int i = 1; i < numUAVs + 1; i++) {
								AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
								if (panelBox.getName().equals(box.getName())) {
									panelBox.setIsChecked(true);
								}
							}
						} else {
							for (int i = 1; i < numUAVs + 1; i++) {
								AFInfoBox panelBox = (AFInfoBox) boxes.getComponent(i);
								if (panelBox.getName().equals(box.getName())) {
									panelBox.setIsChecked(false);
								}
							}
						}
					});
					popupContent.addComponent(box);
				}
			}
			/*
			 * find the location on the screen to display the popup. Takes the absolute position of the mouse and converts that to the relative position of the mouse on the map. Uses the map dimensions and
			 * the map position within the layout
			 */
			double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
			double mapHeight = UI.getCurrent().getPage().getBrowserWindowHeight() * 0.9;

			double xDegreeDifference = -(leafletMap.getCenter().getLon() - leafletMarker.getPoint().getLon());
			double yDegreeDifference = leafletMap.getCenter().getLat() - leafletMarker.getPoint().getLat();
			double degreePerZoom = (360.0 / (Math.pow(2, leafletMap.getZoomLevel())));
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

			layout.addComponent(dronePopup, "top:" + String.valueOf((int) adjustedYLocation) + "px;left:"
					+ String.valueOf((int) adjustedXLocation) + "px");

			dronePopup.setVisible(true);
			dronePopup.setPopupVisible(true);

		}
	}

	/**
	 * This listener displays a popup of information about a certain waypoint. Virtually the same listener used in the flight routes UI.
	 * 
	 * @author Patrick Falvey
	 *
	 */
	private class WaypointMouseListener implements LeafletMouseOverListener {

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {

			popup.setVisible(false);
			popup.setPopupVisible(false);
			LMarker leafletMarker = (LMarker) event.getSource();

			VerticalLayout popupContent = (VerticalLayout) popup.getContent().getPopupComponent();
			Iterator<Component> it = popupContent.iterator(); // iterates through the popup content and updates the waypoint information
			while (it.hasNext()) {
				Component c = it.next();
				try {
					currentFlights = flightRouteService.getCurrentFlights();
					for (FlightPlanInfo e : currentFlights) {
						List<Waypoint> coordinates = e.getWaypoints();
						for (Waypoint coord : coordinates) {
							if (coord.getCoordinate().getLatitude() == leafletMarker.getPoint().getLat()
									&& coord.getCoordinate().getLongitude() == leafletMarker.getPoint().getLon()) {
								if (c.getId() != null && c.getId().equals("latitude")) {
									Label l = (Label) c;
									l.setValue("Latitude: " + coord.getCoordinate().getLatitude());
								}
								if (c.getId() != null && c.getId().equals("longitude")) {
									Label l = (Label) c;
									l.setValue("Longitude: " + coord.getCoordinate().getLongitude());
								}
								if (c.getId() != null && c.getId().equals("altitude")) {
									Label l = (Label) c;
									l.setValue("Altitude: " + coord.getCoordinate().getAltitude());
								}
								if (c.getId() != null && c.getId().equals("transitSpeed")) {
									Label l = (Label) c;
									l.setValue("Transit Speed: " + coord.getApproachingspeed());
								}
							}
						}
					}
				} catch (RemoteException e) {
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
			/*
			 * find the location on the screen to display the popup. Takes the absolute position of the mouse and converts that to the relative position of the mouse on the map. Uses the map dimensions and
			 * the map position within the layout
			 */
			double mapWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 366.0;
			double mapHeight = UI.getCurrent().getPage().getBrowserWindowHeight() * 0.9;

			double xDegreeDifference = -(leafletMap.getCenter().getLon() - leafletMarker.getPoint().getLon());
			double yDegreeDifference = leafletMap.getCenter().getLat() - leafletMarker.getPoint().getLat();
			double degreePerZoom = (360.0 / (Math.pow(2, leafletMap.getZoomLevel())));
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

			layout.addComponent(popup, "top:" + String.valueOf((int) adjustedYLocation) + "px;left:"
					+ String.valueOf((int) adjustedXLocation) + "px");

			popup.setVisible(true);
			popup.setPopupVisible(true);
		}
	}

	/**
	 * 
	 * @return returns the waypoint popup
	 */
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

	/**
	 * 
	 * @return returns the drone popup
	 */
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