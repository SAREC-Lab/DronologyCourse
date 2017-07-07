package edu.nd.dronology.ui.cc.main.simulator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class FlightRouteSelectorLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return ImageProvider.IMG_FLIGHTROUTE_24;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		FlightRouteInfo info = (FlightRouteInfo) element;
		return info.getName() + " -- Waypoints:" + info.getWaypoints().size();
	}

}
