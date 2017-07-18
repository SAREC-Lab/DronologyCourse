package edu.nd.dronology.services.launch;

import java.rmi.RemoteException;

import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.fleet.RuntimeDroneTypes;
import edu.nd.dronology.gstation.python.connector.MAVLinkUAVConnector;
import edu.nd.dronology.monitoring.monitoring.UAVMonitoringManager;
import edu.nd.dronology.monitoring.safety.misc.SafetyCaseGeneration;
import edu.nd.dronology.monitoring.service.DroneSafetyService;
import edu.nd.dronology.monitoring.service.DroneSafetyServiceRemoteFacade;
import edu.nd.dronology.monitoring.service.IDroneSafetyRemoteService;
import edu.nd.dronology.monitoring.validation.SafetyCaseValidationManager;
import edu.nd.dronology.monitoring.validation.SafetyCaseValidator;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.services.instances.dronesimulator.DroneSimulatorService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.instances.flightroute.FlightRouteplanningService;
import edu.nd.dronology.services.remote.RemoteManager;
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
			DroneSafetyService.getInstance().startService();
			RuntimeDroneTypes runtimeMode = RuntimeDroneTypes.getInstance();

			runtimeMode.setPhysicalEnvironment();
		//	new SafetyCaseValidator(SafetyCaseGeneration.getUAVSafetyCase()).validate();
			MAVLinkUAVConnector groundStation = new MAVLinkUAVConnector("H001", "localhost", 1234);
			
			RemoteManager.getInstance().contributeService(IDroneSafetyRemoteService.class, DroneSafetyServiceRemoteFacade.getInstance());
			
			runtimeMode.registerCommandHandler(groundStation);

			groundStation.registerMonitoringMessageHandler(UAVMonitoringManager.getInstance());
			groundStation.registerSafetyValidator(SafetyCaseValidationManager.getInstance());
			
			

		} catch (DronologyServiceException | DroneException | FlightZoneException | RemoteException e) {
			LOGGER.error(e);
		}

	}

}
