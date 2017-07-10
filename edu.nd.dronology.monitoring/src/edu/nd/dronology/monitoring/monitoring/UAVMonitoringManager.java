package edu.nd.dronology.monitoring.monitoring;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVMonitoringManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVMonitoringManager.class);

	private static volatile UAVMonitoringManager INSTANCE;
	private static final BlockingQueue<String> queue = new ArrayBlockingQueue<>(500);

	private static final int NUM_THREADS = 5;

	private static final ExecutorService SERVICE_EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS,
			new NamedThreadFactory("Monitoring-Threads"));

	public UAVMonitoringManager(){
		
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

	public void notify(String id, String status) {
		boolean taken = queue.offer(status);
		if (!taken) {
			LOGGER.error("Monitoring queue full!");
		}
	}

}
