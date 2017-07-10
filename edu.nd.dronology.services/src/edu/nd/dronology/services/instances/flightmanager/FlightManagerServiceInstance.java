package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.flight.PlanPoolManager;
import edu.nd.dronology.core.flightzone.FlightZoneManager2;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.util.ServiceIds;

public class FlightManagerServiceInstance extends AbstractServiceInstance implements IFlightManagerServiceInstance {

	private FlightZoneManager2 flightManager;

	public FlightManagerServiceInstance() {
		super(ServiceIds.SERVICE_FLIGHTMANAGER, "Flight Management");
	}

	@Override
	protected Class<?> getServiceClass() {
		return FlightManagerService.class;
	}

	@Override
	protected int getOrder() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	protected String getPropertyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		flightManager = new FlightZoneManager2();
		// flightManager.startThread();

	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void planFlight(String uavid, String planName, List<Waypoint> waypoints) throws Exception {
		flightManager.planFlight(uavid, planName, waypoints);

	}

	@Override
	public void planFlight(String planName, List<Waypoint> waypoints) throws Exception {
		flightManager.planFlight(planName, waypoints);

	}

	@Override
	public void returnToHome(String uavid) throws Exception {
		flightManager.returnToHome(uavid);

	}

	@Override
	public void pauseFlight(String uavid) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public FlightInfo getFlightInfo(String uavId) {
		return FlightInfoCreator.createInfo(uavId);
	}

	@Override
	public void planFlight(String uavid, String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
		flightManager.planFlight(uavid, planName, start, wayPoints);

	}

}
