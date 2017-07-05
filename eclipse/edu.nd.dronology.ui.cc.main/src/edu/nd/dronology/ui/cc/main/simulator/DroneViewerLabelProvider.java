package edu.nd.dronology.ui.cc.main.simulator;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.main.simulator.DroneViewerContentProvider.FlightListWrapper;

public class DroneViewerLabelProvider extends CellLabelProvider implements IStyledLabelProvider {

	DecimalFormat df = new DecimalFormat("#.######");

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);

		return new StyledString(text);
	}

	private String getText(Object element) {
		if (element instanceof DroneStatus) {
			DroneStatus info = (DroneStatus) element;
			return info.getID() + " - " + info.getStatus();
		}

		if (element instanceof LlaCoordinate) {
			LlaCoordinate info = (LlaCoordinate) element;
			return df.format(info.getLatitude()) + "," + df.format(info.getLongitude()) + ","
					+ df.format(info.getAltitude());
		}
		if (element instanceof FlightInfo) {
			return "Flight-Plans";
		}
		if (element instanceof FlightListWrapper) {
			FlightListWrapper info = (FlightListWrapper) element;
			return info.getType();

		}
		if (element instanceof FlightPlanInfo) {

			FlightPlanInfo info = (FlightPlanInfo) element;
			return info.getName();
		}

		return element.toString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof FlightRouteInfo) {
			return ImageProvider.IMG_FLIGHTROUTE_24;
		}

		return null;
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof FlightRouteInfo) {
			return "FlightRoute";
		}
		return super.getToolTipText(element);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
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
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ViewerCell cell) {
		cell.setText(getText(cell.getElement()));
		cell.setImage(getImage(cell.getElement()));

	}
}
