package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.items.AssignedDrone;
import edu.nd.dronology.services.core.items.ISimulatorScenario;

public class AssignedTypesProvider implements IStructuredContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof ISimulatorScenario)) {
			return new Object[0];
		}

		ISimulatorScenario input = (ISimulatorScenario) inputElement;
		List<AssignedDrone> list = new ArrayList<>(input.getAssignedDrones());

		// Collections.sort(list, new EventTypeNameComparator());

		return list.toArray();

	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
