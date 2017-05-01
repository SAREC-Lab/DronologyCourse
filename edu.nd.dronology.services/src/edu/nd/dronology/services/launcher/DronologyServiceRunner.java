package edu.nd.dronology.services.launcher;

import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.flightpath.FlightPathService;
import edu.nd.dronology.services.remote.RemoteService;
import edu.nd.dronology.services.supervisor.SupervisorService;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DronologyServiceRunner {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronologyServiceRunner.class);

	public static void main(String[] args) {
		try {
			RemoteService.getInstance().startService();
			SupervisorService.getInstance().startService();
			FlightPathService.getInstance().startService();

			RuntimeDroneTypes runtimeMode = RuntimeDroneTypes.getInstance();

			runtimeMode.setVirtualEnvironment();

			FlightZoneManager flightManager = new FlightZoneManager();
			Flights flights = flightManager.getFlights();
			flightManager.startThread();

		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		} catch (FlightZoneException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}

	}

}
