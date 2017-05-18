package edu.nd.dronology.ui.cc.main.sidebar.simulatorscenario;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.remote.IDroneSimulatorRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SimScenarioShelfViewerContentProvider implements ITreeContentProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SimScenarioShelfViewerContentProvider.class);
	private SimulatorScenarioCategoryInfo category;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		this.category = (SimulatorScenarioCategoryInfo) inputElement;
		IDroneSimulatorRemoteService service;
		try {
			service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSimulatorRemoteService.class);
			Collection<SimulatorScenarioInfo> scenarios = service.getItems();

			List<SimulatorScenarioInfo> toShow = new ArrayList<>(scenarios);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (SimulatorScenarioInfo info : scenarios) {
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
