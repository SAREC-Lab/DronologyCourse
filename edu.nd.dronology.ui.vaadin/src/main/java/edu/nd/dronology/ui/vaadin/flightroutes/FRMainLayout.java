package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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
	private boolean isFirst = true;
	private int componentCount;
	private String name = "";
	
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
		componentCount = controls.getInfoPanel().getRouteList().size();
		
		//this defines the add new route button and adds a click listener
		Button drawRoute = controls.getInfoPanel().getDrawButton();
		drawRoute.addClickListener(e -> {
			
			//tests whether a route was added or not
			if(!(componentCount == controls.getInfoPanel().getRouteList().size())){
			
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
				
				map.getMapInstance().addClickListener(eve->{
					
					//the size of mapPoints is received as 1 for the first two waypoints added
					if(map.getUtils().getMapPoints().size() == 1 && isFirst){
						map.displayStillEdit(drone, droneName, map.getUtils().getMapPoints().size(), true);
						isFirst = false;
					}
					else{
						map.displayStillEdit(drone, droneName, map.getUtils().getMapPoints().size()+1, true);
					}
				});
			}
		});

		map.display();
		name = controls.getInfoPanel().getName();
		
		// adds click listener to route list
		routeLayout.addLayoutClickListener(e -> {
			if (map.getUtils().isEditable()) {
				HorizontalLayout buttons = new HorizontalLayout();
				Button yes = new Button("Yes");
				Button no = new Button("No");
				buttons.addComponents(yes, no);
				
				VerticalLayout windowContent = new VerticalLayout();
				Label statement = new Label("You have unsaved changes on " + name + ".");
				Label question = new Label ("Are you sure you want to discard all unsaved changes?");
				
				windowContent.addComponents(statement, question, buttons);
				
				Window warning;
				warning = new Window(null, windowContent);
				
				warning.setModal(true);
				warning.setClosable(false);
				warning.setResizable(false);
				
				UI.getCurrent().addWindow(warning);
				
				yes.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
					switchWindows(e, map);
				});
				
				no.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
				});
			}
			else {
				switchWindows(e, map);
			}
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
	
	private void switchWindows(LayoutClickEvent e, FRMapComponent map) {
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
			
			String altitude = String.valueOf(coor.getCoordinate().getAltitude());
			String approachingSpeed = String.valueOf(coor.getApproachingspeed());
			
			pt.setLat(coor.getCoordinate().getLatitude());
			pt.setLon(coor.getCoordinate().getLongitude());
			
			WayPoint way = new WayPoint(pt, false);
			way.setAltitude(altitude);
			way.setTransitSpeed(approachingSpeed);
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
		
		//click listener to update waypoint number 
		map.getMapInstance().addClickListener(eve->{
			
			map.displayStillEdit(flightInfo, flightInfo.getName(), map.getUtils().getMapPoints().size()+1, true);
					
		});
		
		
		//map.getUtils().setMapPoints(waypoints);
		//map.getTableDisplay().setGrid(map.getUtils().getMapPoints());
		
		//List<WayPoint> local = map.getUtils().getMapPoints();
		//map.getUtils().setSetPoints(waypoints);
		map.getTableDisplay().setGrid(waypoints);
	}
}
