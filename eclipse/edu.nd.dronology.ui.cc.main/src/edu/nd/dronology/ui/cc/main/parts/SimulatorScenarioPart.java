 
package edu.nd.dronology.ui.cc.main.parts;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.simulatorscenario.SimulatorScenarioEditor;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class SimulatorScenarioPart extends AbstractDronologyControlCenterPart {
	private SimulatorScenarioEditor editor;

	@Inject
	public SimulatorScenarioPart() {
		
	}

	@Override
	protected void doCreateContents(Composite parent) {
		
		ControlUtil.setColor(parent);
		DronologyMainActivator.getDefault().getEventBroker().subscribe(EventConstants.SIMULATORSCENARIO_OPEN,
				(org.osgi.service.event.Event event) -> {
					open(event);
				});

		editor = new SimulatorScenarioEditor(parent);
		
	}


	private void open(Event event) {
		SimulatorScenarioInfo elem = (SimulatorScenarioInfo) event.getProperty("org.eclipse.e4.data");
		if (elem == null) {
			return;
		}

		editor.setInput(elem);

	}


	
	
	
	
	
	
}