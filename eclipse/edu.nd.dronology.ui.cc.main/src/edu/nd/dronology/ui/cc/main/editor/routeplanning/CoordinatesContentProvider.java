package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import edu.nd.dronology.services.core.items.IFlightRoute;

public class CoordinatesContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		IFlightRoute info = (IFlightRoute) inputElement;
		return info.getWaypoints().toArray();
	}

}
