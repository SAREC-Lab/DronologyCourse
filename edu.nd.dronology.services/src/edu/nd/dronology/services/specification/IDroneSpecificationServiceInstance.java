package edu.nd.dronology.services.specification;

import java.util.Collection;

import edu.nd.dronology.services.core.api.IFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;

public interface IDroneSpecificationServiceInstance extends IFileTransmitServiceInstance<DroneSpecificationInfo> {

	Collection<TypeSpecificationInfo> getTypeSpecifications();

}
