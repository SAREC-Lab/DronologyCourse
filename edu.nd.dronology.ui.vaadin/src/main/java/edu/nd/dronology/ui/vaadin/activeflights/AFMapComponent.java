package edu.nd.dronology.ui.vaadin.activeflights;

import java.awt.MouseInfo;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
	private AbsoluteLayout layout = new AbsoluteLayout();

	private MapMarkerUtilities utilities;

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private FileResource droneIcon = new FileResource(new File(basepath + "/VAADIN/img/drone_icon.png"));
	private FileResource droneIconFocused = new FileResource(new File(basepath + "/VAADIN/img/drone_icon_focused.png"));
	private FileResource droneIconSelected = new FileResource(new File(basepath + "/VAADIN/img/drone_icon_selected.png"));
	private FileResource dotIcon = new FileResource(new File(basepath + "/VAADIN/img/dot.png"));

	public AFMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("map_component");

		leafletMap = new LMap();
		utilities = new MapMarkerUtilities(leafletMap);

		VerticalLayout content = new VerticalLayout();

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
					polyLines = utilities.drawLines(wayPoints, true, 2);
				else {
					boolean drawn = false;
					for (String name : checked){
						if (e.getDroneId().equals(name)){
							polyLines = utilities.drawLines(wayPoints, true, 1);
							drawn = true;
						}
					}
					if (!drawn)
						polyLines = utilities.drawLines(wayPoints, true, 0);
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
				if (wayPointMarkers.size() != currentFlights.size()) {
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

	public void followDrones(List<String> names) {
		Configuration configuration = Configuration.getInstance();
		if (names.size() < 1) {
			Point point = new Point(configuration.getMapCenterLat(), configuration.getMapCenterLon());
			double zoom = configuration.getMapDefaultZoom();
			leafletMap.setCenter(point, zoom);
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
			double zoom;
			if (farthestLat == 0 && farthestLon == 0) {
				zoom = 17;
			} else {
				zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
			}
			leafletMap.setCenter(point, zoom);
		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private class DroneMouseListener implements LeafletMouseOverListener {

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {
			// TODO Auto-generated method stub
			LMarker leafletMarker = (LMarker) event.getSource();
			VerticalLayout content = new VerticalLayout();
			PopupView popup = new PopupView(null, content);

			try {
				drones = service.getDrones();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Entry<String, DroneStatus> e : drones.entrySet()) {
				if (e.getValue().getID().equals(leafletMarker.getId())) {
					AFInfoBox box = new AFInfoBox(false, e.getValue().getID(), e.getValue().getStatus(),
							e.getValue().getBatteryLevel(), "green", e.getValue().getLatitude(), e.getValue().getLongitude(),
							e.getValue().getAltitude(), e.getValue().getVelocity(), false);
					box.setBoxVisible(false);
					box.addStyleName("af_info_box");
					box.getRouteButton().addClickListener(click -> {
						popup.setPopupVisible(false);
					});
					box.getHomeButton().addClickListener(click -> {
						popup.setPopupVisible(false);
					});
					content.addComponent(box);
				}
			}

			popup.setPopupVisible(true);
			popup.addStyleName("bring_front");

			layout.addComponent(popup, "top:" + String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getY() - 150)
					+ "px;left:" + String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getX() - 360) + "px");
		}
	}

	private class WaypointMouseListener implements LeafletMouseOverListener {

		@Override
		public void onMouseOver(LeafletMouseOverEvent event) {	
			LMarker leafletMarker = (LMarker)event.getSource();

			VerticalLayout content = new VerticalLayout();
			PopupView popup = new PopupView(null, content);
			
			try {
				currentFlights = flightRouteService.getCurrentFlights();
				for (FlightPlanInfo e : currentFlights) {
					List<Waypoint> coordinates = e.getWaypoints();
					for (Waypoint coord : coordinates) {
						if (coord.getCoordinate().getLatitude() == leafletMarker.getPoint().getLat() && coord.getCoordinate().getLongitude() == leafletMarker.getPoint().getLon()) {
							content.addComponent(new Label("Latitude: " + coord.getCoordinate().getLatitude()));
							content.addComponent(new Label("Longitude: " + coord.getCoordinate().getLongitude()));
							content.addComponent(new Label("Altitude: "  + coord.getCoordinate().getAltitude()));
							content.addComponent(new Label("Transit Speed: " + coord.getApproachingspeed()));
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
	
			popup.setPopupVisible(true);
			popup.addStyleName("bring_front");
			
			layout.addComponent(popup, "top:" + String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getY() - 150)
			+ "px;left:" + String.valueOf((int) MouseInfo.getPointerInfo().getLocation().getX() - 360) + "px");
		}		
	}
}