package edu.nd.dronology.monitoring.monitoring;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.monitoring.service.DroneSafetyService;
import edu.nd.dronology.monitoring.validation.ValidationEntry;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ValidationResultManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ValidationResultManager.class);

	private static volatile ValidationResultManager INSTANCE;
	private static final BlockingQueue<UAVMonitoringMessage> queue = new ArrayBlockingQueue<>(500);

	private static final int NUM_THREADS = 5;

	Map<String, UAVValidationInformation> validationInformation = new ConcurrentHashMap<>();

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static ValidationResultManager getInstance() {

		if (INSTANCE == null) {
			synchronized (ValidationResultManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new ValidationResultManager();
				}
			}
		}
		return INSTANCE;
	}

	public void forwardResult(String uavid, ValidationEntry validationResult) {
		if (validationInformation.containsKey(uavid)) {
			validationInformation.get(uavid).addResult(validationResult);
		} else {
			UAVValidationInformation info = new UAVValidationInformation(uavid);
			validationInformation.put(uavid, info);
			info.addResult(validationResult);
		}
		DroneSafetyService.getInstance().notifyValidationListeners(uavid,validationResult);

	}

	public UAVValidationInformation getValidationInfos(String uavid) throws DronologyServiceException {
		if (validationInformation.containsKey(uavid)) {
			return validationInformation.get(uavid);
		}
		throw new DronologyServiceException("No validation info for '" + uavid + "' available");
	}

	public Collection<UAVValidationInformation> getValidationInfos() {
		return Collections.unmodifiableCollection(validationInformation.values());
	}

}
