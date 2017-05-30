package edu.nd.dronology.ui.cc.util.managedcontrol;

public class StringValidator extends AbstractStringValidator {


	public StringValidator(boolean permitEmpty) {
		super(permitEmpty);
	}


	@Override
	protected Result validate(String newText) {
		return new Result(null, newText);
	}

}
