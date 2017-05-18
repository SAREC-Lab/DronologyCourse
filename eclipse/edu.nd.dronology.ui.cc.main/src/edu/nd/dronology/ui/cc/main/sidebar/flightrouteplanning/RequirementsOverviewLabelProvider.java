package edu.nd.dronology.ui.cc.main.sidebar.flightrouteplanning;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class RequirementsOverviewLabelProvider extends CellLabelProvider implements IStyledLabelProvider {

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);

		return new StyledString(text);
	}

	private String getText(Object element) {
		if (element instanceof FlightRouteInfo) {
			FlightRouteInfo info = (FlightRouteInfo) element;
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
