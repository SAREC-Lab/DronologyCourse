package edu.nd.dronology.services.dronesetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.fleet.AbstractDroneFleetFactory;
import edu.nd.dronology.core.fleet.PhysicalDroneFleetFactory;
import edu.nd.dronology.core.fleet.VirtualDroneFleetFactory;
import edu.nd.dronology.core.flightzone.FlightZoneManager;
import edu.nd.dronology.core.status.DroneCollectionStatus;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSetupServiceInstance extends AbstractServiceInstance implements IDroneSetupServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSetupServiceInstance.class);

	private FlightZoneManager flightManager;
	private AbstractDroneFleetFactory droneFleetFactory;
	private List<IDroneStatusChangeListener> listenerList = new ArrayList<>();
	private static final boolean IS_PYHSICAL = false; 

	public DroneSetupServiceInstance() {
		super("DRONESETUP");
	}

	@Override
	protected Class<?> getServiceClass() {
		return DroneSetupService.class;
	}

	@Override
	protected int getOrder() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	protected String getPropertyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		if (IS_PYHSICAL) {
			droneFleetFactory = PhysicalDroneFleetFactory.getInstance();
		} else {
			droneFleetFactory = VirtualDroneFleetFactory.getInstance();
		}
	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeDrones(List<String[]> newDrones, boolean physical) throws DronologyServiceException {
		for (String[] newDrone : newDrones) {
			String droneID = newDrone[0];
			String droneType = newDrone[1];
			long latitude = Long.parseLong(newDrone[2]);
			long longitude = Long.parseLong(newDrone[3]);
			int altitude = Integer.parseInt(newDrone[4]);
			try {
				droneFleetFactory.initializeDrone(droneID, droneType, latitude, longitude, altitude);
			} catch (DroneException e) {
				throw new DronologyServiceException(e.getMessage());
			}
		}

	}

	@Override
	public Map<String, DroneStatus> getDrones() {
		return DroneCollectionStatus.getInstance().getDrones();

	}

	@Override
	public void initializeDrones(DroneInitializationInfo[] info) throws DronologyServiceException {
		NullUtil.checkArrayNull(info);
		for (DroneInitializationInfo di : info) {
			try {
				doInitDrone(di);
			} catch (DroneException e) {
				throw new DronologyServiceException(e.getMessage());
			}
		}
	}

	private void doInitDrone(DroneInitializationInfo di) throws DroneException {
		droneFleetFactory.initializeDrone(di.getId(), di.getType(), di.getInitialLocation());
		DroneStatus drStat = DroneCollectionStatus.getInstance().getDrone(di.getId());
		notifyDroneStatusChange(drStat);
	}

	@Override
	public void addDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		synchronized (listenerList) {
			boolean success = listenerList.add(listener);
			if (!success) {
				// throw exception
			}
		}

	}

	@Override
	public void removeDroneStatusChangeListener(IDroneStatusChangeListener listener) {
		synchronized (listenerList) {
			boolean success = listenerList.remove(listener);
			if (!success) {
				// throw exception
			}
		}

	}

	private void notifyDroneStatusChange(DroneStatus status) {
		List<IDroneStatusChangeListener> notifyList;
		synchronized (listenerList) {
			notifyList = new ArrayList<>(listenerList);
		}
		for (IDroneStatusChangeListener listener : notifyList) {
			try {
				listener.droneStatusChanged(status);
			} catch (Exception e) {
				LOGGER.error(e);
				listenerList.remove(listener);
			}
		}
	}

}
