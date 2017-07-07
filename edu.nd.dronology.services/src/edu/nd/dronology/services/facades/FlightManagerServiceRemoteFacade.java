package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.List;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightManagerServiceRemoteFacade extends AbstractRemoteFacade implements IFlightManagerRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightManagerServiceRemoteFacade.class);
	private static volatile FlightManagerServiceRemoteFacade INSTANCE;

	protected FlightManagerServiceRemoteFacade() throws RemoteException {
		super(FlightManagerService.getInstance());
	}

	public static IFlightManagerRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (FlightManagerServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new FlightManagerServiceRemoteFacade();
					}
				}

			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public FlightInfo getFlightInfo(String uavId) throws RemoteException, Exception {
		return FlightManagerService.getInstance().getFlightInfo(uavId);
	}

	@Override
	public void planFlight(String uavid, String planName, List<Waypoint> waypoints) throws Exception {
		FlightManagerService.getInstance().planFlight(uavid, planName, waypoints);

	}

	@Override
	public void planFlight(String planName, List<Waypoint> waypoints) throws Exception {
		FlightManagerService.getInstance().planFlight(planName, waypoints);

	}

	@Override
	public void returnToHome(String uavid) throws RemoteException, Exception {
		FlightManagerService.getInstance().returnToHome(uavid);

	}

	@Override
	public void pauseFlight(String uavid) throws RemoteException, Exception {
		FlightManagerService.getInstance().pauseFlight(uavid);

	}

}