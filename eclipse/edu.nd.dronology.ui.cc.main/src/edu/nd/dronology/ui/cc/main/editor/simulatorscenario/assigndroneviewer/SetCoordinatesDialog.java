package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class SetCoordinatesDialog extends Dialog {
	private Text txtLat;
	private Text txtLong;
	private Text txtAlt;

	private String latitude = "0";

	private String longitude = "0";
	private String altitude = "0";

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SetCoordinatesDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		Label lblLat = new Label(container, SWT.NONE);
		lblLat.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLat.setText("Latitude:");

		txtLat = new Text(container, SWT.BORDER);
		txtLat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLat.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				latitude = txtLat.getText();
			}
		});

		Label lblLong = new Label(container, SWT.NONE);
		lblLong.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLong.setText("Longitude:");

		txtLong = new Text(container, SWT.BORDER);
		txtLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLong.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				longitude = txtLong.getText();
			}
		});
		Label lblAlt = new Label(container, SWT.NONE);
		lblAlt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlt.setText("Altitude:");

		txtAlt = new Text(container, SWT.BORDER);
		txtAlt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtAlt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				altitude = txtAlt.getText();
			}
		});
		txtLat.setText("0");
		txtLong.setText("0");
		txtAlt.setText("0");

		return container;
	}

	public String getAltitude() {
		return altitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
