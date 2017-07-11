package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the main layout for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */

public class FRMainLayout extends CustomComponent {
	private static final long serialVersionUID = 1L;
	private int index;
	private FRControlsComponent controls = new FRControlsComponent();
	private WayPoint way;
	private FRMapComponent map;
	private VerticalLayout routeLayout;

	@WaypointReplace
	public FRMainLayout() {

		addStyleName("main_layout");
		CssLayout content = new CssLayout();
		content.setSizeFull();

		FRMapComponent map = new FRMapComponent("VAADIN/sbtiles/{z}/{x}/{y}.png", "South Bend",
				"VAADIN/sateltiles/{z}/{x}/{y}.png", "Satellite");
		
		map.setCenter(41.68, -86.25);
		map.setZoomLevel(13);

		routeLayout = controls.getInfoPanel().getRoutes();

		//this defines the add new route button and adds a click listener
		Button drawRoute = controls.getInfoPanel().getDrawButton();
		drawRoute.addClickListener(e -> {
			map.enableEdit();
			//to get rid of points and lines from previous routes
			map.getUtils().removeAllMarkers(map.getUtils().getPins());
			map.getUtils().removeAllLines(map.getUtils().getPolylines());
			map.getUtils().getMapPoints().clear();
			
			//displays the drone information in the info bar
			FlightRouteInfo drone = controls.getInfoPanel().getRoute();
			int numCoords = drone.getWaypoints().size();
			String droneName = controls.getInfoPanel().getName();
			map.displayByName(drone, droneName, numCoords, true);
			
			Point pt = new Point(0, 0);
			way = new WayPoint(pt, true);
			
			map.getTableDisplay().getGrid().setItems();
			map.enableEdit();

		});

		map.display();
		
		// adds click listener to route list
		routeLayout.addLayoutClickListener(e -> {
			
			//gets box of route info and changes its style to show that it is selected
			Component child = e.getChildComponent();
			
			if(routeLayout.getComponentIndex(child) != -1){
				child.addStyleName("info_box_focus");
			}
			
			//child.addStyleName("info_box_focus");
			index = routeLayout.getComponentIndex(child);

			//gets the flight info for that route
			FlightRouteInfo flightInfo = controls.getInfoPanel().getFlight(index);
			List<Waypoint> flightWaypoints = flightInfo.getWaypoints();
			
			// removes old pins, polylines, and style when switching routes
			map.getUtils().removeAllMarkers(map.getUtils().getPins());
			map.getTableDisplay().getGrid().setStyleName("fr_table_component");

			int numComponents;
			boolean first = true;

			List<WayPoint> waypoints = new ArrayList<>();
			Point pt = new Point();
			
			//iterates through the flight info and adds to internal waypoints list
			for (Waypoint coor : flightWaypoints) {

				pt.setLat(coor.getCoordinate().getLatitude());
				pt.setLon(coor.getCoordinate().getLongitude());
				
				WayPoint way = new WayPoint(pt, false);
				way.setAltitude("wow");
				map.getUtils().addNewPinRemoveOld(pt, first);

				waypoints.add(way);
				first = false;
			}

			//adds the lines to the map
			List<LPolyline> mapLines = map.getUtils().drawLines(waypoints, false, 1);
			map.getUtils().setPolylines(mapLines);
			for (int i = 0; i < mapLines.size(); i++) {
				map.getUtils().getMap().addComponent(mapLines.get(i));
			}

			numComponents = routeLayout.getComponentCount();
			
			// when one route is clicked, the others go back to default background color
			for (int i = 0; i < numComponents; i++) {
				if (i != index) {
					routeLayout.getComponent(i).removeStyleName("info_box_focus");
				}
			}	
			map.setRouteCenter();
			map.displayByName(flightInfo, null, 0, false);
		});
		
		content.addComponents(controls, map);
		setCompositionRoot(content);
	}

	public FRControlsComponent getControls() {
		return controls;
	}

	public int getIndex() {
		return index;
	}

	public FRMapComponent getMap() {
		return map;
	}
}
