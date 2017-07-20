package edu.nd.dronology.ui.cc.main.dialogs;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.core.mission.MissionPlan.UavRoutePair;
import edu.nd.dronology.services.core.info.FlightRouteInfo;

public class MissionViewerLabelProvider implements ITableLabelProvider {

	private List<FlightRouteInfo> routeInfo;

	public MissionViewerLabelProvider(List<FlightRouteInfo> routeInfo) {
		this.routeInfo = routeInfo;
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		UavRoutePair pair = (UavRoutePair) element;
		if (columnIndex == 0) {
			return pair.getUavid();
		}
		return getRouteName(pair.getRouteid());
	}

	private String getRouteName(String routeid) {
		for (FlightRouteInfo info : routeInfo) {
			if (info.getId().equals(routeid)) {
				return info.getName();
			}
		}
		return routeid;
	}

}
