package edu.nd.dronology.ui.vaadin.flightroutes;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.core.util.LlaCoordinate;
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

	public FRMainLayout() {

		addStyleName("main_layout");

		CssLayout content = new CssLayout();
		content.setSizeFull();

		FRMapComponent map = new FRMapComponent("VAADIN/sbtiles/{z}/{x}/{y}.png", "South Bend");
		map.setCenter(41.68, -86.25);
		map.setZoomLevel(13);

		VerticalLayout routes;

		routes = controls.getInfoPanel().getRoutes();

		map.display();
		// adds click listener to route list
		routes.addLayoutClickListener(e -> {
			Component child = e.getChildComponent();
			child.addStyleName("info_box_focus");
			index = routes.getComponentIndex(child);

			// gets FRInfoPanel component through FRControlsComponent, and flight info from accessor in FRInfoPanel
			FlightRouteInfo flightInfo = controls.getInfoPanel().getFlight(index);
			List<LlaCoordinate> coords = flightInfo.getCoordinates();

			map.display(flightInfo);

			long tempLong;
			long tempLat;
			int numComponents;
			boolean first = true;

			ArrayList<WayPoint> waypoints = new ArrayList<>();
			Point pt = new Point();

			// trying to convert to waypoint
			for (LlaCoordinate coor : coords) {

				// // Convert to point
				// tempLong = coor.getLongitude();
				// tempLat = coor.getLatitude();
				//
				// double doubleLong = tempLong * .000001;
				// double doubleLat = tempLat * .000001;

				pt.setLat(coor.getLatitude());
				pt.setLon(coor.getLongitude());

				WayPoint way = new WayPoint(pt, false);

				map.getUtils().addNewPinRemoveOld(pt, first);

				waypoints.add(way);
				first = false;
			}

			map.getUtils().drawLines(waypoints);

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
}
