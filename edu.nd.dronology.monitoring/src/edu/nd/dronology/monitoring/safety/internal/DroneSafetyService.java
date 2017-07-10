package edu.nd.dronology.monitoring.safety.internal;

import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class DroneSafetyService extends AbstractServerService<IDroneSafetyServiceInstance> {

	private static volatile DroneSafetyService INSTANCE;

	protected DroneSafetyService() {
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneSafetyService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneSafetyService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneSafetyService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneSafetyServiceInstance initServiceInstance() {
		return new DroneSafetyServiceInstance();
	}

	public void registerDroneSafetyCase(String uavid, String safetycase) throws DronologyServiceException {
		serviceInstance.registerDroneSafetyCase(uavid, safetycase);
		
	}

}
