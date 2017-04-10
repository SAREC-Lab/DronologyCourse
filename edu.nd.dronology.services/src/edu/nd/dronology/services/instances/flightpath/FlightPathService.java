package edu.nd.dronology.services.instances.flightpath;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.info.FlightPathInfo;

public class FlightPathService extends AbstractFileTransmitServerService<IFlightPathServiceInstance, FlightPathInfo> {

	private static volatile FlightPathService INSTANCE;

	protected FlightPathService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static FlightPathService getInstance() {
		if (INSTANCE == null) {
			synchronized (FlightPathService.class) {
				INSTANCE = new FlightPathService();
			}
		}
		return INSTANCE;
	}

	@Override
	protected IFlightPathServiceInstance initServiceInstance() {
		return new FlightPathServiceInstance();
	}


}
