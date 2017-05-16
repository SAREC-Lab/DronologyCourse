package edu.nd.dronology.services.launcher;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.services.equipment.DroneEquipmentService;
import edu.nd.dronology.services.instances.dronesimulator.DroneSimulatorService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.instances.flightroute.FlightRouteplanningService;
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
			FlightRouteplanningService.getInstance().startService();
			FlightManagerService.getInstance().startService();
			DroneSetupService.getInstance().startService();
			DroneEquipmentService.getInstance().startService();
			DroneSimulatorService.getInstance().startService();
			RuntimeDroneTypes runtimeMode = RuntimeDroneTypes.getInstance();

			runtimeMode.setVirtualEnvironment();

		
		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		} catch (FlightZoneException e) {
			LOGGER.error(e);
		}

	}

}
