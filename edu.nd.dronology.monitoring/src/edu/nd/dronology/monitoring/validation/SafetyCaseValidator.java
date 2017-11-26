package edu.nd.dronology.monitoring.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.nd.dronology.gstation.python.connector.IUAVSafetyValidator;
import edu.nd.dronology.monitoring.monitoring.UAVMonitoringManager;
import edu.nd.dronology.monitoring.safety.IExternalSafetyCase;
import edu.nd.dronology.monitoring.safety.ISACAssumption;
import edu.nd.dronology.monitoring.safety.internal.InfrastructureSafetyCase;
import edu.nd.dronology.monitoring.safety.misc.SafetyCaseGeneration;
import edu.nd.dronology.monitoring.util.BenchmarkLogger;
import edu.nd.dronology.monitoring.validation.ValidationResult.Result;
import edu.nd.dronology.monitoring.validation.engine.EngineFactory;
import edu.nd.dronology.monitoring.validation.engine.EvaluationEngineException;
import edu.nd.dronology.monitoring.validation.engine.IEvaluationEngine;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SafetyCaseValidator {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SafetyCaseValidator.class);

	private static final InfrastructureSafetyCase INSTANCE_SAFETY_CASE = SafetyCaseGeneration.getSafetyCase();

	IEvaluationEngine engine = EngineFactory.getEngine();

	private final IExternalSafetyCase safetyCase;

	public SafetyCaseValidator(IExternalSafetyCase safetyCase) {
		this.safetyCase = safetyCase;
	}

	public ValidationResult validate() {
		ValidationResult validationResult = new ValidationResult();
		validateStaticEntries(validationResult);
		validateMonitoringEntries(validationResult);
		validateEvidence(validationResult);

		if (validationResult.validationPassed()) {
			LOGGER.info("Safety case for '" + safetyCase.getUAVId() + "' sucessfully validated!");
		} else {
			LOGGER.warn("Safety case for '" + safetyCase.getUAVId() + "' did not pass validation!");
		}

		return validationResult;

	}

	private void validateEvidence(ValidationResult validationResult) {
		// TODO Auto-generated method stub

	}

	private void validateMonitoringEntries(ValidationResult validationResult) {
		List<ISACAssumption> monitorableAssumptions = INSTANCE_SAFETY_CASE.getMonitoralbeAssumptions();
		MonitoringValidator monitoringValidator = new MonitoringValidator(safetyCase.getUAVId());
		boolean valid = true;
		for (ISACAssumption ass : monitorableAssumptions) {
			ValidationEntry entry;

			try {
				evaluateMonitorable(ass, monitoringValidator);
				entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.MONITORING_PROPERTY_PASSED);
				validationResult.addValidationEntry(entry);

			} catch (EvaluationException e) {
				LOGGER.error("Error when evaluating safety case " + e.getMessage());
				entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.MONITORING_PROPERTY_FAILED);
				validationResult.addValidationEntry(entry);
				valid = false;
			}

		}
		if (valid) {
			LOGGER.info("Validation of monitorable parameters passed!");
		}
		if (validationResult.validationPassed()) {
			UAVMonitoringManager.getInstance().registerValidator(monitoringValidator);
		}
	}

	private Map<String, String> evaluateMonitorable(ISACAssumption ass, MonitoringValidator monitoringValidator)
			throws EvaluationException {
		EvalFunction function = ass.getFunction();
		try {
			monitoringValidator.addFunction(function);
		} catch (EvaluationEngineException e) {
			throw new EvaluationException("Error when creating function " + e.getMessage());
		}
		Map<String, String> paramMap = new HashMap<>();
		ISACAssumption extAssumption = safetyCase.getAssumption(ass.getId());
		if (extAssumption == null) {
			throw new EvaluationException("Assumption '" + ass.getId() + "' not found");
		}

		for (String param : function.getParameters()) {
			if (!isISACParam(param)) {
				String mappedParam = extAssumption.getParameterMapping(param);
				if (mappedParam == null) {
					String value = extAssumption.getParameterValue(param);
					if (value == null) {
						throw new EvaluationException("ParameterMapping for param '" + param + "' not found");
					}
					monitoringValidator.addValue(param, value);
				} else {
					monitoringValidator.addMapping(param, mappedParam);
				}
			}
		}
		return paramMap;

	}

	private void validateStaticEntries(ValidationResult validationResult) {
		List<ISACAssumption> staticassumptions = INSTANCE_SAFETY_CASE.getStaticEvaluateableAssumptions();
		boolean passed = true;
		long startTimestamp = System.nanoTime();
		for (ISACAssumption ass : staticassumptions) {
			Boolean result;
			ValidationEntry entry;
			try {
				result = evaluate(ass);

				if (result == null) {
					entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.ERROR);
					LOGGER.error("Error when evaluating assumption '" + ass.getId() + "' " + ass.getExpression());
					passed = false;
				} else if (result.booleanValue()) {
					entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.STATIC_CHECK_PASSED);
				} else {
					entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.STATIC_CHECK_FAILED);
					LOGGER.warn("Assumption '" + ass.getId() + "' " + ass.getExpression() + " evaluated to false");
					passed = false;
				}
			} catch (EvaluationException e) {
				LOGGER.error("Error when evaluating safety case " + e.getMessage());
				entry = new ValidationEntry(ass.getId(), ass.getWeight(), Result.ERROR);
				passed = false;
			}
			validationResult.addValidationEntry(entry);

		}
		long endTimestamp = System.nanoTime();
		BenchmarkLogger.reportStatic(safetyCase.getUAVId(), (endTimestamp - startTimestamp), Boolean.toString(passed));
		if (passed) {
			LOGGER.info("Validation of static constraints passed!");
		} else {
			LOGGER.warn("Validation of static constraints failed!");
		}

	}

	private Boolean evaluate(ISACAssumption ass) throws EvaluationException {
		// String expression = ass.getExpression();
		EvalFunction function = ass.getFunction();

		try {
			Object createFunction = engine.createFunction(function.getFunctionString());
			String callString = createCallString(function, ass);
			Boolean result = (Boolean) engine.evaluateFunction(callString);
			return result;

		} catch (EvaluationEngineException e) {
			LOGGER.error(e);
		}
		return null;

	}

	private String createCallString(EvalFunction function, ISACAssumption ass) throws EvaluationException {
		StringBuilder params = new StringBuilder();
		for (String param : function.getParameters()) {
			if (isISACParam(param)) {
				params.append(ass.getParameterValue(param));
			} else {
				ISACAssumption extAssumption = safetyCase.getAssumption(ass.getId());
				if (extAssumption == null) {
					throw new EvaluationException("Assumption '" + ass.getId() + "' not found");
				}
				String paramValue = extAssumption.getParameterValue(param);
				if (paramValue == null) {
					throw new EvaluationException("Param '" + param + "' not found");
				}
				params.append(paramValue);

			}
			params.append(",");
		}
		params.append("null");
		return ass.getId() + "(" + params.substring(0, params.length()) + ")";
	}

	public static boolean isISACParam(String param) {
		return param.startsWith(ISACAssumption.ISAC_PREFIX);
	}

}
