package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assignpathviewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class AllTypesLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return ImageProvider.IMG_FLIGHTROUTE_24;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof FlightRouteInfo) {
			return ((FlightRouteInfo) element).getName();
		}
		return element.toString();
	}

}
