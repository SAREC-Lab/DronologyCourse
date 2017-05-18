
package edu.nd.dronology.ui.cc.main.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

import edu.nd.dronology.ui.cc.main.wizard.settings.ConfigWizard;

public class ShowSettingsHandler {

	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	@Inject
	private IEclipseContext context;

	@Execute
	public void execute(IEclipseContext context, IWorkbench workbench) {
		
		ConfigWizard wizard = new ConfigWizard(commandService,handlerService);
		wizard.open();
		if(wizard.needsRestart()){
			workbench.restart();
		}
		
	}

}