package edu.nd.dronology.monitoring.validation;

import edu.nd.dronology.core.util.PreciseTimestamp;
import edu.nd.dronology.monitoring.validation.ValidationResult.Result;

public class ValidationEntry {

	private final String assumptionid;
	private final Result result;
	private PreciseTimestamp timestamp;

	public ValidationEntry(String assumptionid, Result result) {
		this.timestamp = PreciseTimestamp.create();
		this.assumptionid = assumptionid;
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	public boolean checkPassed() {
		return result == Result.MONITORING_PROPERTY_PASSED || result == Result.STATIC_CHECK_PASSED;
	}

	public void setTimestamp(PreciseTimestamp timestamp) {
		this.timestamp = timestamp;

	}

	public String getAssumptionid() {
		return assumptionid;
	}

	public PreciseTimestamp geTimestamp() {
		return timestamp;
	}

}
