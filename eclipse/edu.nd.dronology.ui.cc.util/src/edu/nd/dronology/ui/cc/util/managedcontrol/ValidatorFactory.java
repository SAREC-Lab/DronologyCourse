package edu.nd.dronology.ui.cc.util.managedcontrol;

public class ValidatorFactory {

	/**
	 * Creates a new Validator that accepts all input Strings.<br/>
	 * Useful in case a Validator has to be set, but no restrictions should be applied.
	 * 
	 * @param permitEmpty
	 *          indicates if empty values are permitted
	 * @return a validator that accepts all input Strings
	 */
	public static IManagedTextInputValidator<String> createStringValidator(boolean permitEmpty) {
		return new StringValidator(permitEmpty);
	}

	
	
	public static IManagedTextInputValidator<String> createYearValidator(boolean permitEmpty) {
		return new YearValidator(permitEmpty);
	}
	
	
	public static IManagedTextInputValidator<String> createNumberValidator(boolean permitEmpty) {
		return new NumberValidator(permitEmpty);
	}

}
