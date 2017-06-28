package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.flightzone.FlightZoneManager;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.info.RemoteInfoFactory;

public class FlightManagerServiceInstance extends AbstractServiceInstance implements IFlightManagerServiceInstance {

	private FlightZoneManager flightManager;

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
		flightManager = new FlightZoneManager();
		flightManager.startThread();

	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void planFlight(String planName, LlaCoordinate coordinates, List<LlaCoordinate> flight) {
		flightManager.planFlight(planName, coordinates, flight);

	}

	@Override
	public FlightInfo getFlightDetails() {

		return RemoteInfoFactory.createFlightInfo(flightManager.getFlights());

	}

	@Override
	public void planFlight(String uavid, String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
		flightManager.planFlight(uavid, planName, start, wayPoints);

	}

}
