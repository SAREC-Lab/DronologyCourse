package edu.nd.dronology.ui.cc.main.runnable;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.swt.widgets.Display;



public class ConnectionErrorDialog {

	public static void showError(final Throwable e) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					
					DialogUtil.openErrorDialog(Display.getDefault().getActiveShell(),"Connection Problem","Error when connecting to remote server: \n" + e.getMessage()
									,ExceptionUtils.getStackTrace(e));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

	}

}
