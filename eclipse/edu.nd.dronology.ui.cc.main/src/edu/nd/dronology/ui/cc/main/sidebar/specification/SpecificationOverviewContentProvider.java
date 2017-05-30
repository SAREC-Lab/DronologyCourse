package edu.nd.dronology.ui.cc.main.sidebar.specification;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;
import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SpecificationOverviewContentProvider implements ITreeContentProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SpecificationOverviewContentProvider.class);
	private TypeSpecificationInfo type;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {

		this.type = (TypeSpecificationInfo) inputElement;

		IDroneSpecificationRemoteService service;
		try {
			service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSpecificationRemoteService.class);
			Collection<DroneSpecificationInfo> drones = service.getItems();

			List<DroneSpecificationInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (DroneSpecificationInfo info : drones) {
				if (!info.getType().equals(type.getName())) {
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
