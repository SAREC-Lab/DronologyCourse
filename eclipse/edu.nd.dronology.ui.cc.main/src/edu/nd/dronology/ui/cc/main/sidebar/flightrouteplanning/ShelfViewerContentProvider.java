package edu.nd.dronology.ui.cc.main.sidebar.flightrouteplanning;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ShelfViewerContentProvider implements ITreeContentProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ShelfViewerContentProvider.class);
	private FlightRouteCategoryInfo category;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		this.category = (FlightRouteCategoryInfo) inputElement;
		IFlightRouteplanningRemoteService service;
		try {
			service = (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> scenarios = service.getItems();

			List<FlightRouteInfo> toShow = new ArrayList<>(scenarios);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (FlightRouteInfo info : scenarios) {
				if (!info.getCategory().equals(category.getName())) {
					toShow.remove(info);
				}
			}

			return toShow.toArray();

		} catch (DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// IFlightPathRemoteService service;
		// try {
		// service = (IFlightPathRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
		// .getService(IFlightPathRemoteService.class);
		// Collection<FlightPathInfo> scenarios = service.getItems();
		// return scenarios.toArray();
		// } catch (DronologyServiceException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
