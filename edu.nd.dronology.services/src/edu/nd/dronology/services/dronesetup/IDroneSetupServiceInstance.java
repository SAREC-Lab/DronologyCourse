package edu.nd.dronology.services.dronesetup;

import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IDroneSetupServiceInstance extends IServiceInstance {

	void initializeDrones(List<String[]> newDrones, boolean b) throws DronologyServiceException;

	Map<String, DroneStatus> getDrones();

	void initializeDrones(DroneInitializationInfo[] info)throws DronologyServiceException;

	void addDroneStatusChangeListener(IDroneStatusChangeListener listener);

	void removeDroneStatusChangeListener(IDroneStatusChangeListener listener);

 

}
