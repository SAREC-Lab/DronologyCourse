package edu.nd.dronology.services.equipment;

import java.util.Collection;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.info.EquipmentTypeInfo;

public class DroneEquipmentService extends AbstractFileTransmitServerService<IDroneEquipmentServiceInstance, DroneEquipmentInfo> {

	private static volatile DroneEquipmentService INSTANCE;

	protected DroneEquipmentService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneEquipmentService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneEquipmentService.class) {
				INSTANCE = new DroneEquipmentService();
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneEquipmentServiceInstance initServiceInstance() {
		return new DronEquipmentServiceInstance();
	}

	public Collection<EquipmentTypeInfo> getEquipmentTypes() {
		return serviceInstance.getEquipmentTypes();
	}



}
