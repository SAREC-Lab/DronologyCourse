 
package edu.nd.dronology.ui.cc.main.parts;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.routeplanning.FlightRouteEditor;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class FlightRoutePlanningPart extends AbstractDronologyControlCenterPart {
	private FlightRouteEditor editor;

	@Inject
	public FlightRoutePlanningPart() {
		
	}

	@Override
	protected void doCreateContents(Composite parent) {
		ControlUtil.setColor(parent);
		DronologyMainActivator.getDefault().getEventBroker().subscribe(EventConstants.FLIGHTROUTE_OPEN,
				(org.osgi.service.event.Event event) -> {
					open(event);
				});

		editor = new FlightRouteEditor(parent);
		
	}


	private void open(Event event) {
		FlightRouteInfo elem = (FlightRouteInfo) event.getProperty("org.eclipse.e4.data");
		if (elem == null) {
			return;
		}

		editor.setInput(elem);

	}


	
	
	
	
	
	
}