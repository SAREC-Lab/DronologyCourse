package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.items.AssignedDrone;
import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;

public class AllTypesProvider implements IStructuredContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof ISimulatorScenario)) {
			return new Object[0];
		}
		ISimulatorScenario input = (ISimulatorScenario) inputElement;
		List<AssignedDrone> contained = input.getAssignedDrones();
		IDroneSpecificationRemoteService service;
		try {
			service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSpecificationRemoteService.class);
			Collection<DroneSpecificationInfo> drones = service.getItems();

			List<DroneSpecificationInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (DroneSpecificationInfo s : drones) {
				if (contained.contains(new AssignedDrone(s.getName()))) {
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
