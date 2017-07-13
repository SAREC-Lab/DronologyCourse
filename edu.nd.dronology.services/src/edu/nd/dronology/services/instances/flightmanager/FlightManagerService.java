package edu.nd.dronology.services.instances.flightmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;

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
				if (INSTANCE == null) {
					INSTANCE = new FlightManagerService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IFlightManagerServiceInstance initServiceInstance() {
		return new FlightManagerServiceInstance();
	}

	public FlightInfo getFlightInfo(String uavId) {
		return serviceInstance.getFlightInfo(uavId);

	}

	public void planFlight(String uavid, String planName, List<Waypoint> waypoints) throws Exception {
		serviceInstance.planFlight(uavid, planName, waypoints);

	}

	public void planFlight(String planName, List<Waypoint> waypoints) throws Exception {
		serviceInstance.planFlight(planName, waypoints);

	}

	public void returnToHome(String uavid) throws Exception {
		serviceInstance.returnToHome(uavid);

	}

	public void pauseFlight(String uavid) throws Exception {
		serviceInstance.pauseFlight(uavid);

	}

	public Collection<FlightPlanInfo> getCurrentFlights() {
		return serviceInstance.getCurrentFlights();
	}

}
