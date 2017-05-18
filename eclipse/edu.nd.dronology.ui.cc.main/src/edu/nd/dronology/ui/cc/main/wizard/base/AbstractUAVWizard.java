package edu.nd.dronology.ui.cc.main.wizard.base;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public abstract class AbstractUAVWizard extends Wizard {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractUAVWizard.class);

	

	public int open() {
		try {
			return new UAVWizardDialog(this).open();

		} catch (SWTException | IllegalArgumentException ex) {
			// catch swt exception when dirty closing the wizard...
			// LOGGER.warn(ex.getMessage());
			return SWT.ABORT;
		}
	}



	public Point getInitialSize() {
		return new Point(1000,700);
	}
}
