package edu.nd.dronology.ui.cc.util.managedcontrol;

public class YearValidator extends AbstractStringValidator {

	public YearValidator(boolean permitEmpty) {
		super(permitEmpty);
	}

	@Override
	protected Result validate(String newText) {

		String trimmed = newText.trim();

		try {
			int val = Integer.parseInt(trimmed);
			if (val < 1900 || val > 2100) {
				return new Result("Value must a year between 1900 and 2100", null);
			}
		} catch (NumberFormatException e) {
			return new Result("Value must a year between 1900 and 2100", null);
		}

		return new Result(null, trimmed);
	}

}
