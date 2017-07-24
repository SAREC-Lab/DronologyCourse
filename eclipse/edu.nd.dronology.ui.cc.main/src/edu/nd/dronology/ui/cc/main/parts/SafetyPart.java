
package edu.nd.dronology.ui.cc.main.parts;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.simulatorscenario.SimulatorScenarioEditor;
import edu.nd.dronology.ui.cc.main.safety.SafetyViewer;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class SafetyPart extends AbstractDronologyControlCenterPart {
	private SimulatorScenarioEditor editor;

	@Inject
	public SafetyPart() {

	}

	@Override
	protected void doCreateContents(Composite parent) {

		ControlUtil.setColor(parent);

		new SafetyViewer(parent);

	}

}