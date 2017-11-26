package edu.nd.dronology.monitoring.validation;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.fleet.DroneFleetManager;
import edu.nd.dronology.core.util.PreciseTimestamp;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;
import edu.nd.dronology.monitoring.monitoring.UAVMonitoringManager;
import edu.nd.dronology.monitoring.monitoring.ValidationResultManager;
import edu.nd.dronology.monitoring.safety.ISACAssumption;
import edu.nd.dronology.monitoring.safety.misc.SafetyCaseGeneration;
import edu.nd.dronology.monitoring.util.BenchmarkLogger;
import edu.nd.dronology.monitoring.validation.ValidationResult.Result;
import edu.nd.dronology.monitoring.validation.engine.EngineFactory;
import edu.nd.dronology.monitoring.validation.engine.EvaluationEngineException;
import edu.nd.dronology.monitoring.validation.engine.IEvaluationEngine;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringValidator {

	static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().create();

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringValidator.class);

	private static final int MAX_STORE = 50;

	private final String uavid;
	Map<String, String> mapping = new HashMap<>();
	private List<EvalFunction> functions = new ArrayList<>();

	IEvaluationEngine engine = EngineFactory.getEngine();

	Map<String, Queue<MonitoredEvent>> monitoredData = new ConcurrentHashMap<>();

	private Map<String, Object> values = new HashMap<>();

	public MonitoringValidator(String uavid) {
		NullUtil.checkNull(uavid);
		this.uavid = uavid;
		addUtilFunctions();
	}

	private void addUtilFunctions() {
		try {
			engine.createFunction(
					"function getLast(key,param,hist){var len = hist[key].length; return hist[key][len-1][param];}");
			engine.createFunction(
					"function diffToPrevious(key,param,history){ var hist = JSON.parse(history); if(hist[key]!=null){var len = hist[key].length}else{return null}; return hist[key][len-1][param] - hist[key][len-2][param];}");

			engine.createFunction("var uav_mode = 'in_air';");
			engine.createFunction(
					"function checkMode(toCheck){return new String(toCheck).valueOf() == new String(uav_mode).valueOf();}");

		} catch (EvaluationEngineException e) {
			LOGGER.error(e);
		}

	}

	public void addMapping(String param, String mappedParam) {
		NullUtil.checkNull(param, mappedParam);
		mapping.put(param, mappedParam);

	}

	public void addValue(String param, Object value) {
		NullUtil.checkNull(param, value);
		if (values.containsKey(param)) {
			LOGGER.warn("Ambiguous Parameter found: " + param + " - " + values.get(param).toString() + "/"
					+ value.toString());
		}
		values.put(param, value);

	}

	public String getUavId() {
		return uavid;
	}

	public void validate(UAVMonitoringMessage monitoringMesasge) {
		NullUtil.checkNull(monitoringMesasge);
		doPreEvaluation();
		evaluateCustomFunctions(monitoringMesasge);
		for (EvalFunction f : functions) {
			try {
				evaluate(f, monitoringMesasge);
			} catch (EvaluationException e) {
				LOGGER.error(e.getMessage());
			}

		}
		storeMessageData(monitoringMesasge);
	}

	private void evaluateCustomFunctions(UAVMonitoringMessage monitoringMesasge) {
		// TODO...
	}

	private void doPreEvaluation() {
		ManagedDrone drone;
		try {
			drone = DroneFleetManager.getInstance().getRegisteredDrone(uavid);
			String updateMode = "uav_mode ='" + drone.getFlightModeState().getStatus() + "'";
			engine.createFunction(updateMode);

		} catch (DroneException | EvaluationEngineException e) {
			LOGGER.error(e);
		}

	}

	private void storeMessageData(UAVMonitoringMessage monitoringMesasge) {
		long ts = monitoringMesasge.getTimestamp().getTimestamp();
		synchronized (monitoredData) {

			for (Entry<String, Object> s : monitoringMesasge.getProperties()) {

				Queue<MonitoredEvent> list = monitoredData.get(s.getKey());
				if (list == null) {
					list = new CircularFifoQueue<MonitoredEvent>(MAX_STORE);
					monitoredData.put(s.getKey(), list);
				}
				list.add(MonitoredEvent.create(ts, s.getValue()));
			}
		}

	}

	private void evaluate(EvalFunction f, UAVMonitoringMessage monitoringMesasge) throws EvaluationException {
		PreciseTimestamp ts = PreciseTimestamp.create();
		StringBuilder params = new StringBuilder();
		long startTimestamp = System.nanoTime();
		for (String param : f.getParameters()) {
			if (SafetyCaseValidator.isISACParam(param)) {
				ISACAssumption ass = SafetyCaseGeneration.getSafetyCase().getAssumption(f.getId());

				params.append(ass.getParameterValue(param));
			} else {
				String mappedParam = mapping.get(param);
				if (mappedParam == null && values.get(param) == null) {
					throw new EvaluationException("No parameter mapping for '" + param + "'");
				}
				Object value;
				if (mappedParam != null) {
					value = monitoringMesasge.getProperty(mappedParam);
					if (value == null) {
						ValidationResultManager.getInstance().forwardResult(uavid,
								new ValidationEntry(f.getId(), f.getWeight(), Result.MONITORING_CHECK_ERROR));
						throw new EvaluationException(
								"Parameter '" + mappedParam + "' not found in monitoring message");
					}

				} else {
					value = values.get(param);
					if (value == null) {
						ValidationResultManager.getInstance().forwardResult(uavid,
								new ValidationEntry(f.getId(), f.getWeight(), Result.MONITORING_CHECK_ERROR));
						throw new EvaluationException("Parameter '" + mappedParam + "' not available");
					}
				}
				String paramValue = value.toString();
				if (paramValue == null) {
					throw new EvaluationException("Param '" + param + "' not found");
				}
				params.append(paramValue);

			}
			params.append(",");
		}
		params.append("'");
		String paramString = params.toString();
		params.append(GSON.toJson(monitoredData));
		params.append("'");
		String callString = f.getId() + "(" + params.substring(0, params.length()) + ")";
		 //LOGGER.info("Calling " + callString);
		Boolean result;
		try {
			Object returnvalue = engine.evaluateFunction(callString);
			if (!(returnvalue instanceof Boolean)) {
				result = null;
				LOGGER.info(returnvalue.toString());
			} else {
				result = (Boolean) returnvalue;
			}

			Result res;
			if (result == null) {
				res = Result.MONITORING_CHECK_ERROR;
			} else if (result.booleanValue()) {
				res = Result.MONITORING_CHECK_PASSED;
			} else {
				res = Result.MONITORING_CHECK_FAILED;
			}
			ValidationEntry validationResult = new ValidationEntry(f.getId(), f.getWeight(), res);
			validationResult.setTimestamp(ts);
			long endTimestamp = System.nanoTime();
			BenchmarkLogger.reportMonitor(uavid, f.getId(), (endTimestamp - startTimestamp), result.toString());
			if (!result.booleanValue()) {
				//LOGGER.warn("Evaluation failed: " + f.getFunctionString() + " with parameters " + paramString);
			} else {
				//LOGGER.info("Evaluation passed: " + f.getFunctionString() + " with parameters " + paramString);
			}
			ValidationResultManager.getInstance().forwardResult(uavid, validationResult);
		} catch (EvaluationEngineException e) {
			LOGGER.error(e.getMessage());
		}

	}

	public void addFunction(EvalFunction function) throws EvaluationEngineException {
		NullUtil.checkNull(function);
		functions.add(function);
		// LOGGER.info("Creating function: " + function.getFunctionString());
		Object createFunction = engine.evaluateFunction(function.getFunctionString());

	}

	public void process(UAVStateMessage message) {
		long ts = message.getTimestamp().getTimestamp();
		synchronized (monitoredData) {

			for (Entry<String, Object> s : message.getProperties()) {

				Queue<MonitoredEvent> list = monitoredData.get(s.getKey());
				if (list == null) {
					list = new CircularFifoQueue<MonitoredEvent>(MAX_STORE);
					monitoredData.put(s.getKey(), list);
				}
				list.add(MonitoredEvent.create(ts, s.getValue()));
			}
		}

	}

}
