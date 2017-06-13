package edu.nd.dronology.services.launch;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.fleet.RuntimeDroneTypes;
import edu.nd.dronology.gstation.python.connector.PythonBase;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.services.instances.dronesimulator.DroneSimulatorService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.instances.flightroute.FlightRouteplanningService;
import edu.nd.dronology.services.remote.RemoteService;
import edu.nd.dronology.services.specification.DroneSpecificationService;
import edu.nd.dronology.services.supervisor.SupervisorService;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DronologyServiceRunner {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronologyServiceRunner.class);

	public static void main(String[] args) {
		try {
			RemoteService.getInstance().startService();
			SupervisorService.getInstance().startService();
			FlightRouteplanningService.getInstance().startService();
			FlightManagerService.getInstance().startService();
			DroneSetupService.getInstance().startService();
			DroneSpecificationService.getInstance().startService();
			DroneSimulatorService.getInstance().startService();
			RuntimeDroneTypes runtimeMode = RuntimeDroneTypes.getInstance();

			runtimeMode.setVirtualEnvironment();
			runtimeMode.setCommandHandler(new PythonBase());

		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		} catch (FlightZoneException e) {
			LOGGER.error(e);
		}

	}

}
