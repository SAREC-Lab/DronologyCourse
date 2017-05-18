package edu.nd.dronology.ui.cc.util.managedcontrol;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Control;

public class ManagedTextVerifier {

	private IManagedTextInputValidator<?> validator;

	private ControlDecoration deco;
	private String lastMsg = "";

	public ManagedTextVerifier(Control control, IManagedTextInputValidator<?> validator) {
		if (control == null || validator == null) {
			throw new IllegalArgumentException("control and validator must not be null"); //$NON-NLS-1$
		}

		this.validator = validator;
		deco = VerificationUtil.createDecoration(control);
		deco.setShowOnlyOnFocus(false);

	}

	public IManagedTextInputValidator<?> getValidator() {
		return validator;
	}

	public String validate(String text) {
		String msg = validator.isValid(text);
		if (lastMsg != null && !lastMsg.equals(msg)) {
			updateMessages(msg);
		}
		return msg;
	}

	public void clearMessages() {
		lastMsg = ""; //$NON-NLS-1$
		if (deco != null) {
			deco.setDescriptionText(null);
			deco.hide();
		}
	}

	private void updateMessages(final String msg) {
		clearMessages();
		if (deco != null && msg != null) {
			deco.setDescriptionText(msg);
			deco.show();
		}
		lastMsg = msg;

	}

	/**
	 * @return true if there are no error messages
	 */
	public boolean isOk() {
		return lastMsg != null && lastMsg.isEmpty();
	}
}
