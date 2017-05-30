
package edu.nd.dronology.ui.cc.main.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.sidebar.flightrouteplanning.FlightRoutePlanningnShelfViewer;

public class FlightRoutePlanningSidePart {
	private FlightRoutePlanningnShelfViewer viewer;

	@Inject
	public FlightRoutePlanningSidePart() {

	}

	@PostConstruct
	protected void postConstruct(Composite parent) {
		viewer = new FlightRoutePlanningnShelfViewer(parent);

		DronologyMainActivator.getDefault().getEventBroker().subscribe(EventConstants.REFRESH_SIDEBAR,
				(org.osgi.service.event.Event event) -> {
					viewer.refresh();
				});
		viewer.refreshFully(false);

	}

}