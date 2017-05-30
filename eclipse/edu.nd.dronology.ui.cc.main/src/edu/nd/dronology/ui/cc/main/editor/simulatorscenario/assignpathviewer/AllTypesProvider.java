package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assignpathviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;

public class AllTypesProvider implements IStructuredContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof ISimulatorScenario)) {
			return new Object[0];
		}
		ISimulatorScenario input = (ISimulatorScenario) inputElement;
		List<String> contained = input.getAssignedFlightPaths();
		IFlightRouteplanningRemoteService service;
		try {
			service = (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> drones = service.getItems();

			List<FlightRouteInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (FlightRouteInfo s : drones) {
				if (contained.contains(s.getName())) {
					toShow.remove(s);
				}
			}

			return toShow.toArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}

	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
