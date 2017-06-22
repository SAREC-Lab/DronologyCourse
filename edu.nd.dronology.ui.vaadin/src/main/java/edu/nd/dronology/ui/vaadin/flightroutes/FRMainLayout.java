package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;

import org.vaadin.addon.leaflet.shared.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main layout for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMainLayout extends CustomComponent {
	private static final long serialVersionUID = 1L;
	int index;
	
	WayPoint way;
	ArrayList<WayPoint> waypoints;
	
	public FRMainLayout() {
						
		addStyleName("main_layout");
		
		CssLayout content = new CssLayout();
		content.setSizeFull();
		
		FRControlsComponent controls = new FRControlsComponent();
		
		//FRMetaInfo bar = new FRMetaInfo(flightInfo);
		
		FRMapComponent map = new FRMapComponent(
  		"VAADIN/sbtiles/{z}/{x}/{y}.png",
  		"South Bend");
		map.setCenter(41.68, -86.25);
		map.setZoomLevel(13);
    
		VerticalLayout routes;
    
		routes = controls.getInfoPanel().getRoutes();

		
		//adds click listener to route list
		routes.addLayoutClickListener(e->{
			Component child = e.getChildComponent();
			child.setStyleName("info_box_colored");
			child.addStyleName("fr_info_box");
			index = routes.getComponentIndex(child);
			
			//gets FRInfoPanel component through FRControlsComponent, and flight info from accessor in FRInfoPanel
			FlightRouteInfo flightInfo = controls.getInfoPanel().getFlight(index);
			List<Coordinate> coords = flightInfo.getCoordinates();
			
			long tempLong;
			long tempLat;
			int numComponents;
			boolean first = true;
		
			ArrayList<WayPoint> waypoints = new ArrayList<WayPoint>() ;
			Point pt = new Point();
		
			//trying to convert to waypoint
			for(Coordinate coor: coords){
				
				//Convert to point
				tempLong = coor.getLongitude();
				tempLat = coor.getLatitude();
			
				double doubleLong = tempLong * .000001;
				double doubleLat = tempLat * .000001;
				
				pt.setLat(doubleLat);
				pt.setLon(doubleLong);
			
				WayPoint way = new WayPoint(pt);
			
				map.getUtils().addNewPinRemoveOld(pt, first);
			
				waypoints.add(way);
				first = false;
			}
			
			map.getUtils().drawLines(waypoints);
    
			numComponents = routes.getComponentCount();
			//when one route is clicked, the others go back to default background color
			for(int i = 0; i < numComponents; i++){
				if(i != index){
					routes.getComponent(i).setStyleName("info_box");
					routes.addStyleName("fr_info_box");
					
				}
			}
		});
		
    	content.addComponents(controls, map);
    	setCompositionRoot(content);
	}
}
