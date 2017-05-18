
package edu.nd.dronology.ui.cc.main.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.launcher.LauncherControl;

public class LauncherPart {
	@Inject
	public LauncherPart() {

	}

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

	@PostConstruct
	public void postConstruct(IEclipseContext context, Composite parent) {

		control = new LauncherControl(parent, partService, modelService, commandService, handlerService, app);
		DronologyMainActivator.getDefault().setEventBroker(eventBroker);
		DronologyMainActivator.getDefault().setCommandService(commandService);
		DronologyMainActivator.getDefault().setHandlerService(handlerService);
		// setTheme();
		// setPerspective();

	}

	private void setPerspective() {
		// IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ConfigPreferenceConstants.PREFERENCE_NODE);
		// String perspectiveId = preferences.get(ConfigPreferenceConstants.PREFERENCE_PERSPECTIVE,
		// PerspectiveConstants.LAUNCHER_PERSPECTIVE);
		// if (PerspectiveConstants.LAUNCHER_PERSPECTIVE.equals(perspectiveId)) {
		// return;
		// }
		//
		// Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("id", perspectiveId);
		// ParameterizedCommand cmd2 = commandService.createCommand(CommandConstants.SWTICH_PERSPECTIVE_COMMAND,
		// parameters);
		// handlerService.executeHandler(cmd2);

	}

	private void setTheme() {

		// IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ConfigPreferenceConstants.PREFERENCE_NODE);
		// String themeId = preferences.get(ConfigPreferenceConstants.PREFERENCE_THEME, ThemeSwitchHandler.THEME_DEFAULT);
		//
		// Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("id", themeId);
		// ParameterizedCommand cmd = commandService.createCommand(CommandConstants.SWTICH_THEME_COMMAND, parameters);
		// handlerService.executeHandler(cmd);

	}

	@PreDestroy
	public void preDestroy() {
	}

	@Inject
	@Optional
	private void subscribeTopicTodoUpdated(@UIEventTopic(EventConstants.THEME_CHANGE) boolean value) {
		// control.refresh();
	}

}