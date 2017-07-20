package edu.nd.dronology.monitoring.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult implements Serializable {

	public enum Result {
		ERROR, STATIC_CHECK_PASSED, STATIC_CHECK_FAILED, MONITORING_PROPERTY_PASSED, MONITORING_PROPERTY_FAILED, MONITORING_CHECK_ERROR, MONITORING_CHECK_FAILED, MONITORING_CHECK_PASSED

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7557783530993071227L;
	private List<ValidationEntry> validationEntries = new ArrayList<>();

	public void addValidationEntry(ValidationEntry entry) {
		validationEntries.add(entry);

	}

	public boolean validationPassed() {
		for (ValidationEntry entry : validationEntries) {
			if (!entry.checkPassed()) {
				return false;
			}
		}
		return true;
	}

}
