package edu.nd.dronology.services.instances.flightmanager;

import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.flightzone.FlightZoneManager2;
import edu.nd.dronology.core.mission.IMissionPlan;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.services.core.util.ServiceIds;

public class FlightManagerServiceInstance extends AbstractServiceInstance implements IFlightManagerServiceInstance {

	private FlightZoneManager2 flightManager;
	private FlightMissionManager missionManager;

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
		missionManager = new FlightMissionManager();
		// flightManager.startThread();

	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public FlightInfo getFlightDetails() {

		// return
		// RemoteInfoFactory.createFlightInfo(flightManager.getFlights());
		return null;
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
		flightManager.pauseFlight(uavid);

	}

	@Override
	public FlightInfo getFlightInfo(String uavId) {
		return FlightInfoCreator.createInfo(uavId);
	}

	@Override
	public Collection<FlightPlanInfo> getCurrentFlights() {
		return FlightInfoCreator.getCurrenctFlights();
	}

	@Override
	public void cancelPendingFlights(String uavid) throws Exception {
		flightManager.cancelPendingFlights(uavid);
	}

	@Override
	public void planMission(IMissionPlan missionPlan) throws Exception {
		missionManager.planMission(missionPlan);

	}

}
