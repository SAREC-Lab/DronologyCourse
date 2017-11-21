package edu.nd.dronology.services.specification;

import java.util.Collection;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;

public class DroneSpecificationService
		extends AbstractFileTransmitServerService<IDroneSpecificationServiceInstance, DroneSpecificationInfo> {

	private static volatile DroneSpecificationService INSTANCE;

	protected DroneSpecificationService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneSpecificationService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneSpecificationService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneSpecificationService();
				}
			}
		}
		return INSTANCE;

	}

	@Override
	protected IDroneSpecificationServiceInstance initServiceInstance() {
		return new DronSpecificationServiceInstance();
	}

	public Collection<TypeSpecificationInfo> getTypeSpecifications() {
		return serviceInstance.getTypeSpecifications();
	}

}
