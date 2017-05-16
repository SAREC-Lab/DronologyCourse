package edu.nd.dronology.services.core.persistence;

import com.thoughtworks.xstream.XStream;

import edu.nd.dronology.services.core.items.IFlightRoute;

public class DronologyPersistenceUtil {

	private static final String ROUTE_ALIAS = "FlightRoute";


	public static void preprocessStream(XStream xstream) {

		xstream.alias(ROUTE_ALIAS, IFlightRoute.class);

	}

}
