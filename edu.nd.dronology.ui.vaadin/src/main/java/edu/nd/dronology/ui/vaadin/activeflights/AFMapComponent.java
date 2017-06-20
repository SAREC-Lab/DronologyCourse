package edu.nd.dronology.ui.vaadin.activeflights;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.Configuration;

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
	private IDroneSetupRemoteService service;
  private BaseServiceProvider provider = MyUI.getProvider();
	
  private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
  private FileResource drone_icon = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
  
  
	public AFMapComponent(String tileDataURL, String name) {
		this.setWidth("100%");
		addStyleName("map_component");
		
		leafletMap = new LMap();
		
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
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addDroneMarkers();
		
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

	public void addDroneMarkers(){
		try {
			drones = service.getDrones();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Entry<String, DroneStatus> e:drones.entrySet()){
			LMarker marker = new LMarker(e.getValue().getLatitude()*.000001, e.getValue().getLongitude()*.000001);
			marker.setId(e.getValue().getID());
			marker.setIcon(drone_icon);
			marker.setIconSize(new Point(30, 30));
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
							temp.setLat(e.getValue().getLatitude()*.000001);
							temp.setLon(e.getValue().getLongitude()*.000001);
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
								LMarker newMarker = new LMarker(e1.getValue().getLatitude()*.000001, e1.getValue().getLongitude()*.000001);
								newMarker.setId(e1.getValue().getID());
								newMarker.setIcon(drone_icon);
								newMarker.setIconSize(new Point(30, 30));
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
						LMarker marker = new LMarker(e.getValue().getLatitude()*.000001, e.getValue().getLongitude()*.000001);
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