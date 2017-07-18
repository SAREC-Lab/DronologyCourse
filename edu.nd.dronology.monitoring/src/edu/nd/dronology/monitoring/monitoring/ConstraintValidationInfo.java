package edu.nd.dronology.monitoring.monitoring;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import edu.nd.dronology.monitoring.validation.ValidationEntry;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ConstraintValidationInfo implements Serializable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ConstraintValidationInfo.class);
	private static final long serialVersionUID = 4240806480087389298L;
	private final String assumptionid;
	private final AtomicInteger countTotal = new AtomicInteger(0);
	private final AtomicInteger countPassed = new AtomicInteger(0);
	private final AtomicInteger countFailed = new AtomicInteger(0);
	private final AtomicInteger countError = new AtomicInteger(0);

	public ConstraintValidationInfo(String assumptionid) {
		this.assumptionid = assumptionid;
	}

	public String getAssumptionid() {
		return assumptionid;
	}

	public int getTotalEvaluations() {
		return countTotal.intValue();
	}

	public int getPassEvaluations() {
		return countPassed.intValue();
	}

	public int getFailedEvaluations() {
		return countFailed.intValue();
	}

	public int getErrors() {
		return countError.intValue();
	}

	public void addResult(ValidationEntry validationResult) {
		countTotal.incrementAndGet();

		switch (validationResult.getResult()) {
		case MONITORING_CHECK_PASSED: {
			countPassed.incrementAndGet();
			break;
		}
		case MONITORING_CHECK_FAILED: {
			countFailed.incrementAndGet();
			break;
		}
		case MONITORING_CHECK_ERROR: {
			countError.incrementAndGet();
			break;
		}

		default:
			LOGGER.error("Invalid validation result '" + validationResult.getResult() + "'");
			break;
		}

	}

}
