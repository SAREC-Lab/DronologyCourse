package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.info.FlightInfo;

public class FlightManagerService extends AbstractServerService<IFlightManagerServiceInstance> {

	private static volatile FlightManagerService INSTANCE;

	protected FlightManagerService() {
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static FlightManagerService getInstance() {
		if (INSTANCE == null) {
			synchronized (FlightManagerService.class) {
				INSTANCE = new FlightManagerService();
			}
		}
		return INSTANCE;
	}

	@Override
	protected IFlightManagerServiceInstance initServiceInstance() {
		return new FlightManagerServiceInstance();
	}

	public void planFlight(Coordinates coordinates, List<Coordinates> flight) {
		serviceInstance.planFlight(coordinates, flight);

	}

	public FlightInfo getFlightDetails() {
		return serviceInstance.getFlightDetails();

	}

}
