package edu.nd.dronology.ui.cc.util.managedcontrol;

public class NumberValidator extends AbstractStringValidator {

	public NumberValidator(boolean permitEmpty) {
		super(permitEmpty);
	}

	@Override
	protected Result validate(String newText) {

		String trimmed = newText.trim();
		Integer val;
		try {
			val = Integer.valueOf(newText);
		} catch (Exception ex) {
			val = null;
			return new Result("Only Integer values are allowed!", null);
		}
		return new Result(null, trimmed);

	}

}
