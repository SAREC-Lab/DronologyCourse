package edu.nd.dronology.services.instances.flightmanager;

import java.util.List;

import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.util.FileManager;
import edu.nd.dronology.services.info.RemoteInfoFactory;

public class FlightManagerServiceInstance extends AbstractServiceInstance implements IFlightManagerServiceInstance {

	private FlightZoneManager flightManager;

	public FlightManagerServiceInstance() {
		super("FLIGHTMANAGER");
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
	public void planFlight(Coordinates coordinates, List<Coordinates> flight) {
		flightManager.planFlight(coordinates, flight);

	}

	@Override
	public FlightInfo getFlightDetails() {

		return RemoteInfoFactory.createFlightInfo(flightManager.getFlights());

	}

}
