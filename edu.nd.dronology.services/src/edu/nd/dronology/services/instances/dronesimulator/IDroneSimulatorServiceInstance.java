package edu.nd.dronology.services.instances.dronesimulator;

import java.util.Collection;
import java.util.List;

import edu.nd.dronology.services.core.api.IFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IDroneSimulatorServiceInstance extends IFileTransmitServiceInstance<SimulatorScenarioInfo> {

	void activateScenario(SimulatorScenarioInfo scenario) throws DronologyServiceException;

	Collection<SimulatorScenarioCategoryInfo> getCategories();


}
