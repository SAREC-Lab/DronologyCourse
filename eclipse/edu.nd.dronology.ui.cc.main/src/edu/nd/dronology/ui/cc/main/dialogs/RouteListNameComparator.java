package edu.nd.dronology.ui.cc.main.dialogs;

import java.util.Comparator;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

public class RouteListNameComparator implements Comparator<FlightRouteInfo> {

	@Override
	public int compare(FlightRouteInfo thisRoute, FlightRouteInfo thatRoute) {
		return thisRoute.getName().compareTo(thatRoute.getName());
	}

}
