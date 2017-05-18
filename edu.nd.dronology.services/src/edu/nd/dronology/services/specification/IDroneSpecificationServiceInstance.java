package edu.nd.dronology.services.equipment;

import java.util.Collection;

import edu.nd.dronology.services.core.api.IFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.info.EquipmentTypeInfo;

public interface IDroneEquipmentServiceInstance extends IFileTransmitServiceInstance<DroneEquipmentInfo> {

	Collection<EquipmentTypeInfo> getEquipmentTypes();

}
