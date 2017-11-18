package edu.nd.dronology.services.dronesetup;

import java.util.Collection;
import java.util.Map;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.core.vehicle.proxy.UAVProxy;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IDroneSetupServiceInstance extends IServiceInstance {


	Map<String, UAVProxy> getDrones();

	void initializeDrones(DroneInitializationInfo[] info)throws DronologyServiceException;

	void addDroneStatusChangeListener(IDroneStatusChangeListener listener);

	void removeDroneStatusChangeListener(IDroneStatusChangeListener listener);

	Collection<IUAVProxy> getActiveUAVs();

 

}
