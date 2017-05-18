package edu.nd.dronology.ui.cc.util.managedcontrol;

import org.eclipse.jface.dialogs.IInputValidator;

public interface IManagedTextInputValidator<T> extends IInputValidator {
	
	public T getValue();
}
