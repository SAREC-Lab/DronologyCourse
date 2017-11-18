package edu.nd.dronology.services.dronesetup;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.core.vehicle.proxy.UAVProxy;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class DroneSetupService extends AbstractServerService<IDroneSetupServiceInstance> {

	private static volatile DroneSetupService INSTANCE;

	protected DroneSetupService() {
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static DroneSetupService getInstance() {
		if (INSTANCE == null) {
			synchronized (DroneSetupService.class) {
				if (INSTANCE == null) {
					INSTANCE = new DroneSetupService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IDroneSetupServiceInstance initServiceInstance() {
		return new DroneSetupServiceInstance();
	}

	public Map<String, UAVProxy> getDrones() {
		return serviceInstance.getDrones();

	}

	public void initializeDrones(DroneInitializationInfo... info) throws DronologyServiceException {
		serviceInstance.initializeDrones(info);
	}

	public void addDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		serviceInstance.addDroneStatusChangeListener(listener);

	}

	public void removeDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		serviceInstance.removeDroneStatusChangeListener(listener);

	}

	public Collection<IUAVProxy> getActiveUAVs() {
		return serviceInstance.getActiveUAVs();
	}

}
