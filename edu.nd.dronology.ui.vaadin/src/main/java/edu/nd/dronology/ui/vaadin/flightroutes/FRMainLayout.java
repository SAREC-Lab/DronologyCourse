package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.utils.WayPoint;

/**
 * This is the main layout for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMainLayout extends CustomComponent {
	private static final long serialVersionUID = 1L;
	private int index;
	private FRControlsComponent controls = new FRControlsComponent();
	WayPoint way;
	ArrayList<WayPoint> waypoints;
	ArrayList routeList;
	FRMapComponent map;
	
	public FRMainLayout() {

		addStyleName("main_layout");

		CssLayout content = new CssLayout();
		content.setSizeFull();

		FRMapComponent map = new FRMapComponent(
				"VAADIN/sbtiles/{z}/{x}/{y}.png",
				"South Bend",
				"VAADIN/sateltiles/{z}/{x}/{y}.png",
				"Satellite");
		map.setCenter(41.68, -86.25);
		map.setZoomLevel(13);

		VerticalLayout routes;

		routes = controls.getInfoPanel().getRoutes();
		
		Button tempDraw = controls.getInfoPanel().getDrawButton();
		tempDraw.addClickListener(e -> {
			map.enableEdit();
			//map.display();
			map.getUtils().removeAllMarkers(map.getUtils().getPins());
			map.getUtils().removeAllLines(map.getUtils().getPolylines());
			map.getUtils().getMapPoints().clear();
			FlightRouteInfo drone = controls.getInfoPanel().getRoute();
			
			int numCoords = drone.getCoordinates().size();
			String droneName = controls.getInfoPanel().getName();
			map.displayByName(drone, droneName, numCoords);
			
			Point pt = new Point(0,0);
			WayPoint way = new WayPoint(pt, true);
			map.getTableDisplay().getGrid().setItems();
			map.enableEdit();
			
		});

		map.display();
		// adds click listener to route list
		routes.addLayoutClickListener(e -> {
			Component child = e.getChildComponent();
			child.addStyleName("info_box_focus");
			index = routes.getComponentIndex(child);
			
			FlightRouteInfo flightInfo = controls.getInfoPanel().getFlight(index);
			List<LlaCoordinate> coords = flightInfo.getCoordinates();
			
			map.display(flightInfo);

			
			//removes old pins and polylines when switching routes
			map.getUtils().removeAllMarkers(map.getUtils().getPins());
			
			//to make sure the table is no longer bolded when switching routes without saving
			map.getTableDisplay().getGrid().setStyleName("fr_table_component");
			
			long tempLong;
			long tempLat;
			int numComponents;
			boolean first = true;

			ArrayList<WayPoint> waypoints = new ArrayList<>();
			Point pt = new Point();

			// trying to convert to waypoint
			for (LlaCoordinate coor : coords) {


				pt.setLat(coor.getLatitude());
				pt.setLon(coor.getLongitude());

				WayPoint way = new WayPoint(pt, false);

				map.getUtils().addNewPinRemoveOld(pt, first);

				waypoints.add(way);
				first = false;
			}

			ArrayList<LPolyline> mapLines = map.getUtils().drawLines(waypoints, false);
			
			map.getUtils().setPolylines(mapLines);
			for(int i = 0; i < mapLines.size(); i++){
				map.getUtils().getMap().addComponent(mapLines.get(i));
			}

			numComponents = routes.getComponentCount();
			// when one route is clicked, the others go back to default background color
			for (int i = 0; i < numComponents; i++) {
				if (i != index) {
					routes.getComponent(i).removeStyleName("info_box_focus");
				}
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
	public FRMapComponent getMap(){
		return map;
	}
}
