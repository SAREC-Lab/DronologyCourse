
package edu.nd.dronology.ui.cc.main.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.sidebar.specification.SpecificationShelfViewer;

public class SpecificationSidePart {
	private SpecificationShelfViewer viewer;

	@Inject
	public SpecificationSidePart() {

	}

	@PostConstruct
	protected void postConstruct(Composite parent) {
		viewer = new SpecificationShelfViewer(parent);

		DronologyMainActivator.getDefault().getEventBroker().subscribe(EventConstants.REFRESH_SIDEBAR,
				(org.osgi.service.event.Event event) -> {
					viewer.refresh();
				});
		viewer.	refreshFully(false);
	}

}