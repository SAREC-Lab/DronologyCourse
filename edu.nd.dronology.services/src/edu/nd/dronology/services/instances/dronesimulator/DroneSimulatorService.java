package edu.nd.dronology.services.instances.dronesimulator;

import java.util.Collection;
import java.util.List;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class DroneSimulatorService
		extends AbstractFileTransmitServerService<IDroneSimulatorServiceInstance, SimulatorScenarioInfo> {

	private static volatile DroneSimulatorService INSTANCE;

	protected DroneSimulatorService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneSimulatorService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneSimulatorService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneSimulatorService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneSimulatorServiceInstance initServiceInstance() {
		return new DroneSimulatorServiceInstance();
	}

	public void activateScenario(SimulatorScenarioInfo scenario) throws DronologyServiceException {
		serviceInstance.activateScenario(scenario);

	}

	public Collection<SimulatorScenarioCategoryInfo> getCategories() {
		return serviceInstance.getCategories();
	}

}
