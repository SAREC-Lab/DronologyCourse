package edu.nd.dronology.monitoring.safety.internal;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IDroneSafetyServiceInstance extends IServiceInstance {

	void registerDroneSafetyCase(String uavid, String safetycase) throws DronologyServiceException;

}
