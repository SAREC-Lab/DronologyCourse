package edu.nd.dronology.ui.cc.main.wizard.base;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class UAVWizardDialog extends WizardDialog {

	private AbstractUAVWizard wizard;

	public UAVWizardDialog(AbstractUAVWizard newWizard) {
		super(Display.getDefault().getActiveShell(), newWizard);
		this.wizard = newWizard;
	}

	/**
	 * dirty.... :)
	 */
	@Deprecated
	public void hardClose() {
		super.getShell().close();
	}

	@Override
	public boolean close() {

		return super.close();
	}

	@Override
	public void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle);
	}

	@Override
	protected void nextPressed() {
		IWizardPage currentActivePage = getCurrentPage();

		if (!((AbstractUAVWizardPage) currentActivePage).nextPressed()) {
			return;
		}
		super.nextPressed();
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setData(StyleProvider.CSS_TAG, "Wizard");

		Control control = super.createContents(parent);
		return control;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		//set margin to zero to ensure full skinning..
		Control control = super.createDialogArea(parent);
		((Composite) ((Composite) control).getChildren()[1]).setLayout(new PageContainerFillLayout(0, 0, 300, 300));
		return control;
	}
	
	
	@Override
	protected Point getInitialSize() {
		return wizard.getInitialSize();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		ControlUtil.setColor(parent, parent.getParent());

		Button finish = getButton(IDialogConstants.FINISH_ID);
		Button cancel = getButton(IDialogConstants.CANCEL_ID);
		Button back = getButton(IDialogConstants.BACK_ID);
		Button next = getButton(IDialogConstants.NEXT_ID);

		finish.setFont(StyleProvider.getSelectedFont());
		cancel.setFont(StyleProvider.getSelectedFont());
		if (back != null) {
			back.setFont(StyleProvider.getSelectedFont());
		}
		if (next != null) {
			next.setFont(StyleProvider.getSelectedFont());
		}
	}

}
