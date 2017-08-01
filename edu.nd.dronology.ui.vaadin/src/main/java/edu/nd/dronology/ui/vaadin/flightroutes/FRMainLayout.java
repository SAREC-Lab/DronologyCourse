package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.VaadinSession;
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
	
	private FRControlsComponent controls = new FRControlsComponent(this);
	private FRMapComponent map;
	private VerticalLayout routeLayout;
	private boolean isNew = false;
	private boolean toDo = true;
	private String name = "";
	private String newRouteName;
	private FlightRouteInfo flightInfo;
	private FlightRouteInfo newRoute;
	private int index = -1;

	@WaypointReplace
	public FRMainLayout() {
		addStyleName("main_layout");
		CssLayout content = new CssLayout();
		content.setSizeFull();

		FRMapComponent map = new FRMapComponent("VAADIN/sbtiles/{z}/{x}/{y}.png", "South Bend",
				"VAADIN/sateltiles/{z}/{x}/{y}.png", "Satellite", this, toDo);
		
		this.map = map;
		
		map.setCenter(41.68, -86.25);
		map.setZoomLevel(13);

		routeLayout = controls.getInfoPanel().getRoutes();	
		
		map.display();
		name = controls.getInfoPanel().getName();
		
		// adds click listener to route list
		routeLayout.addLayoutClickListener(e -> {
			if (map.getUtils().isEditable()) {
				HorizontalLayout buttons = new HorizontalLayout();
				Button yes = new Button("Yes");
				Button no = new Button("No");
				buttons.addComponents(yes, no);
				
				//creates a window to warn the user about discarding unsaved changes
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
				
				//click listeners for buttons on window
				yes.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
					switchWindows(e, map, null);
					map.exitEditMode();
				});
				no.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
				});
			}
			else {
				//if map is not in edit mode, then just switch to the other route
				switchWindows(e, map, null);
			}		
		});
		//adds click listener to new route button on info panel
		controls.getInfoPanel().getNewRouteButton().addClickListener(e->{
			//only prompts user if map is in edit mode
			if (map.getUtils().isEditable()) {
				HorizontalLayout buttons = new HorizontalLayout();
				Button yes = new Button("Yes");
				Button no = new Button("No");
				buttons.addComponents(yes, no);
				
				//creates a window to warn the user about discarding unsaved changes
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
				
				//click listeners for buttons on window
				yes.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
					map.displayNoRoute();
					map.exitEditMode();
				});
				no.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
					controls.getInfoPanel().removeWindow();
				});
			}
		});
		
		content.addComponents(controls, map);
		setCompositionRoot(content);
	}
	//displays the route that is clicked. Passes in the click event, map, and infobox that was clicked
	public void switchWindows(LayoutClickEvent e, FRMapComponent map, FRInfoBox component) {
		isNew = false;
		//gets box of route info and changes its style to show that it is selected
		Component child = component; //to initialize the variable
		if (e != null){
			child = e.getChildComponent();
		}
		else {
			//checks the id's of the infoboxes to get the correct child if it was null
			for (int i = 0; i < routeLayout.getComponentCount(); i++){
				if (component.getid().equals(((FRInfoBox) routeLayout.getComponent(i)).getid())){
					child = routeLayout.getComponent(i);
				}
			}
		}
		//-1 represent the area inbetween the infoboxes, or if the index was not set
		if(routeLayout.getComponentIndex(child) != -1){
			child.addStyleName("info_box_focus");
		}

		index = routeLayout.getComponentIndex(child);

		//gets the flight info for that route
		flightInfo = controls.getInfoPanel().getFlight(index);
		
		//creates an arraylist of infoboxes and adds click listeners to the 'yes' buttons on their respective delete bars
		ArrayList<FRInfoBox> list = controls.getInfoPanel().getBoxList();
		for(FRInfoBox box: list){
			box.getDeleteBar().getYesButton().addClickListener(even->{
				//if the 'yes' button is clicked, no route is displayed and the info panel is refreshed (route is deleted in FRDeleteRoute)
				map.displayNoRoute();
				map.exitEditMode();
				controls.getInfoPanel().refreshRoutes();
			});
		}
		
		//stores list of waypoints of relevant flight
		List<Waypoint> flightWaypoints = new ArrayList<>();
		if(routeLayout.getComponentIndex(child) != -1){
			flightWaypoints = flightInfo.getWaypoints();
			name = flightInfo.getName();
		}else{
			flightWaypoints = new ArrayList<>();
		}
		
		// removes old pins, polylines, and style when switching routes
		if(routeLayout.getComponentIndex(child) != -1){
			map.getUtils().removeAllMarkers(map.getUtils().getPins());
			map.getUtils().removeAllLines(map.getUtils().getPolylines());
		}

		int numComponents;
		boolean first = true;

		List<WayPoint> wayPoints = new ArrayList<>();
		Point pt = new Point();
		
		//iterates through the flight info and adds to internal waypoints list
		for (Waypoint coor : flightWaypoints) {
			//NOTE: Waypoint and WayPoint are two different objects. Here I convert from one to the other, as WayPoint is what we need later
			String altitude = String.valueOf(coor.getCoordinate().getAltitude());
			String approachingSpeed = String.valueOf(coor.getApproachingspeed());
			
			pt.setLat(coor.getCoordinate().getLatitude());
			pt.setLon(coor.getCoordinate().getLongitude());
			
			WayPoint way = new WayPoint(pt, false);
			way.setAltitude(altitude);
			way.setTransitSpeed(approachingSpeed);
			map.getUtils().addNewPinRemoveOld(pt, first);
			
			wayPoints.add(way);
			first = false;
		}

		//adds the lines to the map
		map.getUtils().drawLines(wayPoints, true, 1, false);

		numComponents = routeLayout.getComponentCount();
		
		// when one route is clicked, the others go back to default background color
		for (int i = 0; i < numComponents; i++) {
			if (i != index) {
				routeLayout.getComponent(i).removeStyleName("info_box_focus");
			}
		}
		//toDo sets whether or not to center and zoom levels should be reset when changing flight routes
		map.setRouteCenter(map.getToDo());
		
		//displays map
		if(routeLayout.getComponentIndex(child) != -1){
			map.displayByName(flightInfo, null, 0, false, map.getToDo());
		}
		//sets grid
		if(routeLayout.getComponentIndex(child) != -1){
			map.getTableDisplay().setGrid(wayPoints);
			map.getUtils().setMapPointsAltitude(wayPoints);
			map.getUtils().setMapPointsTransit(wayPoints);
		}
	}
	//handles what should happen when the user clicks on one of the delete buttons while still in edit mode
	public void deleteInEdit(){
			HorizontalLayout buttons = new HorizontalLayout();
			Button yes = new Button("Yes");
			Button no = new Button("No");
			buttons.addComponents(yes, no);
			
			//creates a window to warn the user
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
			
			//click listeners for yes and no button on window
			yes.addClickListener(event -> {
				map.getDeleteBar().deleteRoute(map.getSelectedRoute());
				UI.getCurrent().removeWindow(warning);
				map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
				map.displayNoRoute();
				map.exitEditMode();
			});
			no.addClickListener(event -> {
				UI.getCurrent().removeWindow(warning);
				controls.getInfoPanel().removeWindow();
			});
	}
	//called if a new route is made, and displays the route on the map and table while enabling edit mode
	public void drawRoute(){
		isNew = true;
		map.enableEdit();
		map.getUtils().enableRouteEditing();
	
		//to get rid of points and lines from previous routes
		map.getUtils().removeAllMarkers(map.getUtils().getPins());
		map.getUtils().removeAllLines(map.getUtils().getPolylines());
		map.getUtils().getMapPoints().clear();
		
		//displays the drone information in the info bar
		newRoute = controls.getInfoPanel().getRoute();
		int numCoords = newRoute.getWaypoints().size();
		newRouteName = controls.getInfoPanel().getName();
		
		map.displayByName(newRoute, newRouteName, numCoords, true, map.getToDo());
		map.getTableDisplay().getGrid().setItems();
		map.enableEdit();
		
		flightInfo = controls.getInfoPanel().getFlight(index);
	}
	//when a route is deleted, this refreshes the routes in the info panel to reflect the data stored in Dronology 
	public void deleteRouteUpdate(){
		VaadinSession session = getSession();
		if(session != null){
			UI.getCurrent().access(() -> {	
				controls.getInfoPanel().refreshRoutes();
			});
		}
	}
	//gets the controls component that holds the infoPanel and mainLayout
	public FRControlsComponent getControls() {
		return controls;
	}
	//gets the index of the selected infobox (or -1 for when no box is selected)
	public int getIndex() {
		return index;
	}
	//gets the currently displayed map
	public FRMapComponent getMap() {
		return map;
	}
	//describes what should happen when the user clicks on the edit button of a specific box (basically switches to that window and enables editing)
	public void editClick(FRInfoBox infoBox){
		switchWindows(null, map, infoBox);
		map.enableEdit();
	}
	//enables map editing by calling function from MapComponent
	public void enableMapEdit(){
		map.enableEdit();
	}
	//gets the flight info of the selected route
	public FlightRouteInfo getFlightInfo() {
		return flightInfo;
	}
	//gets the flight info of a new route
	public FlightRouteInfo getNewRoute() {
		return newRoute;
	}
	//gets the name of the new route
	public String getNewRouteName() {
		return newRouteName;
	}
	//signals if the selected infobox is representing a new route (false if not)
	public boolean isNew() {
		return isNew;
	}
}