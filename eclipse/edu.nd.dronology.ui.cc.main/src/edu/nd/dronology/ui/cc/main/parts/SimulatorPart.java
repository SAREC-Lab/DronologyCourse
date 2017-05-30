
package edu.nd.dronology.ui.cc.main.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.ui.cc.main.launcher.LauncherControl;
import edu.nd.dronology.ui.cc.main.simulator.SimulatorControl2;

public class SimulatorPart extends AbstractDronologyControlCenterPart {

	@Inject
	private EPartService partService;
	@Inject
	EModelService modelService;
	@Inject
	MApplication app;
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	@Inject
	IEclipseContext context;
	@Inject
	IWorkbench workbench;
	@Inject
	EContextService contextService;
	private LauncherControl control;
	@Inject
	private EventBroker eventBroker;

	@Inject
	public SimulatorPart() {

	}

	@Override
	protected void doCreateContents(Composite parent) {

		new SimulatorControl2(parent, partService, modelService, commandService, handlerService, app);
	}

}