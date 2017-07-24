package edu.nd.dronology.ui.cc.main.simulator;

import java.rmi.RemoteException;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;

public class FlightRouteSelectorContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		try {
			IFlightRouteplanningRemoteService routeService = (IFlightRouteplanningRemoteService) ServiceProvider
					.getBaseServiceProvider().getRemoteManager().getService(IFlightRouteplanningRemoteService.class);

			return routeService.getItems().toArray();

		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return new Object[0];
	}

}
