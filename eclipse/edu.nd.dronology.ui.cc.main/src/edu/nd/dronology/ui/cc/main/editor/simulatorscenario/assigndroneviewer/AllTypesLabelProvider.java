package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class AllTypesLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return ImageProvider.IMG_SPECIFICATION_24;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof DroneSpecificationInfo) {
			return ((DroneSpecificationInfo) element).getName();
		}
		return element.toString();
	}

}
