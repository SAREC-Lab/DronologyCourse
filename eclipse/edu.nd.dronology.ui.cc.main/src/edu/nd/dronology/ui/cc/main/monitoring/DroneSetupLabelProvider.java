package edu.nd.dronology.ui.cc.main.monitoring;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class DroneSetupLabelProvider implements ILabelProvider {

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
	public Image getImage(Object element) {
		if (element instanceof DroneStatus) {
			DroneStatus status = (DroneStatus) element;

			String stat = status.getStatus();

			if ("FLYING".equals(stat)) {
				return ImageProvider.IMG_DRONE_FLYING;
			} else if ("ON_GROUND".equals(stat)) {
				return ImageProvider.IMG_DRONE_ONGROUND;

			} else if ("TAKING_OFF".equals(stat)) {
				return ImageProvider.IMG_DRONE_TAKINGOFF;
			}
		}

		return null;

	}

	@Override
	public String getText(Object element) {

		if (element instanceof DroneStatus) {
			DroneStatus status = (DroneStatus) element;
			return status.toString();

		}

		return element.toString();
	}

}
