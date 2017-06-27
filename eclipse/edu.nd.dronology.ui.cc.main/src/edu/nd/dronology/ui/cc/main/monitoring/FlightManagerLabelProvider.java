package edu.nd.dronology.ui.cc.main.monitoring;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.core.flight.IFlightPlan;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class FlightManagerLabelProvider implements ILabelProvider {

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
		if (element instanceof IFlightPlan) {
			return ImageProvider.IMG_DRONE_FLIGHTPLAN;

		}
		if (element instanceof Waypoint) {
			Waypoint wp = (Waypoint) element;
			System.out.println(wp.isReached());
			return ImageProvider.IMG_DRONE_WAYPOINT;
		}
		if (element instanceof WrappedCoordinate) {
			return ImageProvider.IMG_DRONE_WAYPOINT_START;
		}
		if (element instanceof String) {
			return ImageProvider.IMG_ARROW_RIGHT_24;

		}
		return null;

	}

	@Override
	public String getText(Object element) {

		if (element instanceof DroneStatus) {
			DroneStatus status = (DroneStatus) element;
			return status.toString();

		}

		if (element instanceof IFlightPlan) {
			return ((IFlightPlan) element).getFlightID();

		}
		
		if (element instanceof FlightPlanInfo) {
			return ((FlightPlanInfo) element).getName();

		}

		return element.toString();
	}

}
