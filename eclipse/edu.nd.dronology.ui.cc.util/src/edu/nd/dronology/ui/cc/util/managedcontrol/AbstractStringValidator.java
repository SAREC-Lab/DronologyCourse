package edu.nd.dronology.ui.cc.util.managedcontrol;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.osgi.util.NLS;

public abstract class AbstractStringValidator implements IManagedTextInputValidator<String> {

	

	private final AtomicReference<String> value = new AtomicReference<String>();
	private final boolean permitEmpty;
	private final String valueId;

	protected AbstractStringValidator() {
		this(true);
	}

	protected AbstractStringValidator(String valueId) {
		this(false, valueId);
	}

	protected AbstractStringValidator(boolean permitEmpty) {
		this(permitEmpty, "Value");
	}

	protected AbstractStringValidator(boolean permitEmpty, String valueId) {
		this.valueId = (valueId != null ? valueId : "Value");
		this.permitEmpty = permitEmpty;
	}


	@Override
	public final String getValue() {
		return value.get();
	}

	@Override
	public final String isValid(String newText) {
		if (newText == null || newText.isEmpty()) {
			if (!permitEmpty) {
				return handleResult(new Result(NLS.bind("{0} must not be empty", valueId), null));
			}
			return handleResult(new Result(null, "")); //$NON-NLS-1$
		}
		Result res = validate(newText);
		if (res == null) {
			// in case the subclass behaves bad we simply accept the value
			value.set(newText);
			return null;
		}
		return handleResult(res);
	}

	protected final String getValueId() {
		return this.valueId;
	}

	private String handleResult(Result res) {
		value.set(res.getValue());
		return res.getError();
	}

	protected abstract Result validate(String newText);

	protected static final class Result {

		private final String errMsg;
		private final String newValue;

		public Result(String errMsg, String newValue) {
			this.errMsg = errMsg;
			this.newValue = newValue;
		}

		String getError() {
			return errMsg;
		}

		String getValue() {
			return newValue;
		}

	}

}
