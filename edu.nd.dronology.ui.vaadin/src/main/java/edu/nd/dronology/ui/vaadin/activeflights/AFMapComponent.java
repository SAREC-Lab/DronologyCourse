package edu.nd.dronology.ui.vaadin.activeflights;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vividsolutions.jts.geom.Coordinate;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;
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
	private List<FlightPlanInfo> currentFlights;
	private IDroneSetupRemoteService service;
	private IFlightManagerRemoteService flightRouteService;
  private BaseServiceProvider provider = MyUI.getProvider();
  private ArrayList<ArrayList<LPolyline>> flightRoutes = new ArrayList<>();
  //private ArrayList<ArrayList<WayPoint>> wayPointLists = new ArrayList<>();
	
  private MapMarkerUtilities utilities;
  
  private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
  private FileResource drone_icon = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
  
	public AFMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("map_component");
		
		leafletMap = new LMap();
		utilities = new MapMarkerUtilities(leafletMap);
		Configuration configuration = Configuration.getInstance();
		leafletMap.setCenter(configuration.getMapCenterLat(), configuration.getMapCenterLon());
		leafletMap.setZoomLevel(configuration.getMapDefaultZoom());
		
		VerticalLayout content = new VerticalLayout();
		
		LTileLayer tiles = new LTileLayer();
		tiles.setUrl(tileDataURL);

		leafletMap.addBaseLayer(tiles, name);
		leafletMap.zoomToContent();

		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			flightRouteService = (IFlightManagerRemoteService) provider.getRemoteManager().getService(IFlightManagerRemoteService.class);
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addDroneMarkers();
		addActiveFlightRoutes();
		
		setCompositionRoot(content);  
		content.addComponents(leafletMap);
	}
	
	public void setCenter(double centerLat, double centerLon) {
		leafletMap.setCenter(41.68, -86.25);
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
	
	public void addActiveFlightRoutes(){
		try {
			currentFlights = flightRouteService.getFlightDetails().getCurrentFlights();
			for (FlightPlanInfo e:currentFlights){
				List<Waypoint> coordinates = e.getWaypoints();
				ArrayList<WayPoint> wayPoints = new ArrayList<>();
				//LlaCoordinate tempCoord = e.getStartLocation();
				//Point tempPoint = new Point(tempCoord.getLatitude(), tempCoord.getLongitude());
				//WayPoint tempWayPoint = new WayPoint(tempPoint,false);
				//wayPoints.add(tempWayPoint);
				int i=0;
				for (Waypoint coord:coordinates){
					
					Point point = new Point(coord.getCoordinate().getLatitude(), coord.getCoordinate().getLongitude());
					WayPoint wayPoint = new WayPoint(point, nextReached(coordinates,i+1));
					wayPoints.add(wayPoint);
					i++;
				}
				ArrayList<LPolyline> polyLines = utilities.drawLines(wayPoints);
				flightRoutes.add(polyLines);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean nextReached(List<Waypoint> coordinates, int i) {
		if (coordinates.size() <= i) {
			return false;
		}
		Waypoint next = coordinates.get(i);
		return next.isReached();
	}
	
	public void updateActiveFlightRoutes() {
		try {
			currentFlights = flightRouteService.getFlightDetails().getCurrentFlights();
			if (currentFlights.size() != flightRoutes.size() || true) {
				for (ArrayList<LPolyline> e : flightRoutes) {
					utilities.removeAllLines(e);
				}
				flightRoutes.clear();
				this.addActiveFlightRoutes();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void updateActiveFlightRoutes(){
		try {
			currentFlights = flightRouteService.getFlightDetails().getCurrentFlights();
			if (currentFlights.size() != flightRoutes.size()){
				for (ArrayList<LPolyline> e:flightRoutes){
					utilities.removeAllLines(e);
				}
				flightRoutes.clear();
				this.addActiveFlightRoutes();	
			}
			//update dashed lines
			drones = service.getDrones();
			int index = 0;
			for ( Entry<String, DroneStatus> e:drones.entrySet()){
				if (e.getValue().getStatus().equals("FLYING")){
					//insert drone into waypoint list after most visited waypoint list. While waypoint = visited {} waypointlist.add(drone)
				}
				
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void addDroneMarkers(){
		try {
			drones = service.getDrones();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Entry<String, DroneStatus> e:drones.entrySet()){
			LMarker marker = new LMarker(e.getValue().getLatitude(), e.getValue().getLongitude());
			marker.setId(e.getValue().getID());
			marker.setIcon(drone_icon);
			marker.setIconSize(new Point(77, 33));
			markers.add(marker);
			leafletMap.addComponent(marker);
		}
	}
	
	public void updateDroneMarkers(){
	  try {
			drones = service.getDrones();
			ArrayList<LMarker> remove = new ArrayList<>();
			if (markers.size() == drones.size()){
				for (LMarker marker:markers){
					boolean exists = false;
					for (Entry<String, DroneStatus> e:drones.entrySet()){
						if (marker.getId().equals(e.getValue().getID())){
							Point temp = new Point();
							temp.setLat(e.getValue().getLatitude());
							temp.setLon(e.getValue().getLongitude());
							marker.setPoint(temp);
							exists = true;
						}
					}
					if (!exists){
						remove.add(marker);
						for (Entry<String, DroneStatus> e1:drones.entrySet()){
							boolean old = false;
							for (LMarker marker1:markers){
								if (e1.getValue().getID().equals(marker1.getId()))
									old = true;
							}
							if (!old){
								LMarker newMarker = new LMarker(e1.getValue().getLatitude(), e1.getValue().getLongitude());
								newMarker.setId(e1.getValue().getID());
								newMarker.setIcon(drone_icon);
								newMarker.setIconSize(new Point(77, 33));
								markers.add(newMarker);
								leafletMap.addComponent(newMarker);
							}
						}
					}
				}
			}
			else if (markers.size() < drones.size()){
				for (Entry<String, DroneStatus> e:drones.entrySet()){
					boolean exists = false;
					for (LMarker marker:markers){
						if (e.getValue().getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists){
						LMarker marker = new LMarker(e.getValue().getLatitude(), e.getValue().getLongitude());
						marker.setId(e.getValue().getID());
						marker.setIcon(drone_icon);
						marker.setIconSize(new Point(77, 33));
						markers.add(marker);
						leafletMap.addComponent(marker);
					}
				}
			}
			else if (markers.size() > drones.size()){
				for (LMarker marker:markers){
					boolean exists = false;
					for (Entry<String, DroneStatus> e:drones.entrySet()){
						if (e.getValue().getID().equals(marker.getId()))
							exists = true;
					}
					if (!exists)
						remove.add(marker);
				}
			}
			if (remove.size() > 0){
				for (LMarker e:remove){
					markers.remove(e);
					leafletMap.removeComponent(e);
				}
				remove.clear();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
	}
}