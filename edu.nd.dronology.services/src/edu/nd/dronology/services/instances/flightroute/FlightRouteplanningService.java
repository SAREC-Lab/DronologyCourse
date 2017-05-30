package edu.nd.dronology.services.instances.flightroute;

import java.util.Collection;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class FlightRouteplanningService extends AbstractFileTransmitServerService<IFlightRouteplanningServiceInstance, FlightRouteInfo> {

	private static volatile FlightRouteplanningService INSTANCE;

	protected FlightRouteplanningService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static FlightRouteplanningService getInstance() {
		if (INSTANCE == null) {
			synchronized (FlightRouteplanningService.class) {
				INSTANCE = new FlightRouteplanningService();
			}
		}
		return INSTANCE;
	}

	@Override
	protected IFlightRouteplanningServiceInstance initServiceInstance() {
		return new FlightRouteplanningServiceInstance();
	}

	public Collection<FlightRouteCategoryInfo> getFlightPathCategories() {
		return serviceInstance.getFlightPathCategories();
	}

	public FlightRouteInfo getItem(String name) throws DronologyServiceException {
		return serviceInstance.getItem(name);
	}

}
