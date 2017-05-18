package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.services.core.items.AssignedDrone;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class AssignedTypesLabelProvider implements ITableLabelProvider {
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return ImageProvider.IMG_SPECIFICATION_24;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof AssignedDrone) {
				AssignedDrone drone = (AssignedDrone) element;
				return drone.getName() + " | Start-Coordinate: [" + drone.getStartCoordinate().getLatitude() + ","
						+ drone.getStartCoordinate().getLongitude() + "," + drone.getStartCoordinate().getAltitude() + "]";
			}
		}
		return element.toString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}
