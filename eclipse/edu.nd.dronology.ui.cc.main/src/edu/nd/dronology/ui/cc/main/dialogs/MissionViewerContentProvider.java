package edu.nd.dronology.ui.cc.main.dialogs;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import edu.nd.dronology.core.mission.MissionPlan.RouteSet;

public class MissionViewerContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		return ((RouteSet) inputElement).getUav2routeMappings().toArray();
	}

}
