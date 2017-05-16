package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.List;

import edu.nd.dronology.core.util.Coordinates;
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
	private static FlightManagerServiceRemoteFacade INSTANCE;

	protected FlightManagerServiceRemoteFacade() throws RemoteException {
		super(FlightManagerService.getInstance());
	}

	public static IFlightManagerRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				INSTANCE = new FlightManagerServiceRemoteFacade();
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public void planFlight(Coordinates coordinates, List<Coordinates> flight) throws RemoteException {
		FlightManagerService.getInstance().planFlight(coordinates, flight);

	}

	@Override
	public FlightInfo getFlightDetails() throws RemoteException {
		return FlightManagerService.getInstance().getFlightDetails();
	}

}