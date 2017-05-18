
package edu.nd.dronology.ui.cc.main.parts;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.specification.SpecificationEditor;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class SpecificationPart extends AbstractDronologyControlCenterPart {
	private SpecificationEditor editor;

	@Inject
	public SpecificationPart() {

	}


	private void open(Event event) {
		DroneSpecificationInfo elem = (DroneSpecificationInfo) event.getProperty("org.eclipse.e4.data");
		if (elem == null) {
			return;
		}

		editor.setInput(elem);

	}

	private void refresh() {
		System.out.println("OPEN!");

	}

	@Override
	protected void doCreateContents(Composite parent) {
		ControlUtil.setColor(parent);
		DronologyMainActivator.getDefault().getEventBroker().subscribe(EventConstants.SPECIFICATION_OPEN,
				(org.osgi.service.event.Event event) -> {
					open(event);
				});

		editor = new SpecificationEditor(parent);
		
	}

}