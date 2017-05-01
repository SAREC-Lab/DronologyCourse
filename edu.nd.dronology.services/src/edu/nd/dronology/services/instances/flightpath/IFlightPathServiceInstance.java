package edu.nd.dronology.services.instances.flightpath;

import java.util.Collection;

import edu.nd.dronology.services.core.api.IFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.FlightPathCategoryInfo;
import edu.nd.dronology.services.core.info.FlightPathInfo;

public interface IFlightPathServiceInstance extends IFileTransmitServiceInstance<FlightPathInfo> {

	Collection<FlightPathCategoryInfo> getFlightPathCategories();


}
