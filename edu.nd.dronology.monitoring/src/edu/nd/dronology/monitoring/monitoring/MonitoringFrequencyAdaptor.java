package edu.nd.dronology.monitoring.monitoring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.RateLimiter;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.fleet.DroneFleetManager;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.core.vehicle.commands.SetMonitoringFrequencyCommand;
import edu.nd.dronology.monitoring.trust.TrustManager;
import edu.nd.dronology.monitoring.util.BenchmarkLogger;
import edu.nd.dronology.monitoring.validation.MonitoringValidator;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringFrequencyAdaptor implements Runnable {

	RateLimiter limiter = RateLimiter.create(0.2);
	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringFrequencyAdaptor.class);
	private AtomicBoolean cont = new AtomicBoolean(true);
	private Map<String, Double> frequencies = new ConcurrentHashMap<>();

	public MonitoringFrequencyAdaptor() {

	}

	@Override
	public void run() {

		while (cont.get()) {
			limiter.acquire();
			try {
				// LOGGER.info("Recalculating monitoring frequencies...");

				for (MonitoringValidator validator : UAVMonitoringManager.getInstance().getValidators()) {
					String vid = validator.getUavId();

					double currentReputation = TrustManager.getInstance().getReputationRating(vid);
					double newFrequency = calculateFrequency(currentReputation);
					Double oldFrequncy = frequencies.get(vid);
					if (oldFrequncy == null || oldFrequncy != newFrequency) {
						LOGGER.info("Updating monitoring frequncy for '" + vid + " from:" + oldFrequncy + " to: "
								+ newFrequency);
						BenchmarkLogger.reportFrequency(vid, newFrequency);
						frequencies.put(vid, Double.valueOf(newFrequency));
						updateFrequency(vid, newFrequency);
					}
				}

			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
	}

	private void updateFrequency(String vid, double frequency) {
		ManagedDrone drone;
		try {
			drone = DroneFleetManager.getInstance().getRegisteredDrone(vid);
			drone.sendCommand(new SetMonitoringFrequencyCommand(vid, Double.doubleToLongBits(frequency)));
		} catch (DroneException e) {
			LOGGER.error(e);
		}

	}

	private double calculateFrequency(double currentReputation) {
		return (currentReputation * 25) + 5;
	}

}
