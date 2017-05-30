package edu.nd.dronology.ui.cc.main.wizard.base;

import org.eclipse.jface.wizard.WizardPage;

public abstract class AbstractUAVWizardPage extends WizardPage {

	protected AbstractUAVWizardPage(String pageName) {
		super(pageName);
	}

	public boolean nextPressed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public AbstractUAVWizard getWizard() {
		return (AbstractUAVWizard) super.getWizard();
	}

	protected void updateButtons() {
		getWizard().getContainer().updateButtons();
	}

}
