package edu.nd.dronology.ui.cc.main.runnable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class DialogUtil {
	
	public static void openErrorDialog(Shell parentShell, String title, String message, String error ) {
		new ExtendedErrorDialog(parentShell, title, message, error).open();
	}
	public static void openErrorDialog(Shell parentShell, String title, String message) {
		MessageDialog.openError(parentShell, title,message);
	}

}
