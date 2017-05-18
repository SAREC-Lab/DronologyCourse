
package edu.nd.dronology.ui.cc.main.handler;

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SwitchPerspectiveHandler {

	
	private static final ILogger LOGGER = LoggerProvider.getLogger(SwitchPerspectiveHandler.class);

	
	@Execute
	public void execute(final MWindow window,
	                    final EPartService partService,
	                    final EModelService modelService,
	                    @Named("id")
	                    final String perspectiveId) {
		
		final List<MPerspective> perspectives =
			       modelService.findElements(window, perspectiveId, MPerspective.class, null);
			  if (!(perspectives.isEmpty())) {
			     // Show perspective for looked up id
			     partService.switchPerspective(perspectives.get(0));
			  }
			  else{
				  LOGGER.info("Perspective '"+perspectiveId+"' not found.");
			  }
			}
	

}