package edu.nd.dronology.monitoring.monitoring;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.nd.dronology.gstation.python.connector.IMonitoringMessageHandler;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.monitoring.validation.MonitoringValidator;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVMonitoringManager implements IMonitoringMessageHandler {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVMonitoringManager.class);

	private static volatile UAVMonitoringManager INSTANCE;
	private static final BlockingQueue<UAVMonitoringMessage> queue = new ArrayBlockingQueue<>(500);

	private static final int NUM_THREADS = 5;

	private static final ExecutorService SERVICE_EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS,
			new NamedThreadFactory("Monitoring-Threads"));

	private final Map<String, MonitoringValidator> validators = new ConcurrentHashMap<>();

	public UAVMonitoringManager() {

		SERVICE_EXECUTOR.submit(new MonitoringDataHandler(queue));
	}

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static UAVMonitoringManager getInstance() {

		if (INSTANCE == null) {
			synchronized (UAVMonitoringManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVMonitoringManager();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public void notify(UAVMonitoringMessage message) {
		boolean taken = queue.offer(message);
		if (!taken) {
			LOGGER.error("Monitoring queue full!");
		}

	}

	public void registerValidator(MonitoringValidator monitoringValidator) {
		LOGGER.info("Registering new Monitoring Validator '" + monitoringValidator.getUavId() + "'");
		validators.put(monitoringValidator.getUavId(), monitoringValidator);

	}

	public MonitoringValidator getValidator(String uavid) {
		return validators.get(uavid);
	}

}
