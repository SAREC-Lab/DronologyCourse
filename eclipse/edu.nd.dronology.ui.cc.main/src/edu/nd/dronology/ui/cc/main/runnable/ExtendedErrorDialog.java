package edu.nd.dronology.ui.cc.main.runnable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExtendedErrorDialog extends Dialog {
	private Text txtError;
	private String message;
	private String error;
	private String title;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ExtendedErrorDialog(Shell parentShell, String title, String message, String error) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.error = error;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblMessage = new Label(container, SWT.NONE);
		lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		txtError = new Text(container, SWT.BORDER | SWT.WRAP);
		txtError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtError.setEditable(false);

		txtError.setText(error!=null?error:"no error");
		lblMessage.setText(message);
		
		getShell().setText(title);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
