package edu.nd.dronology.monitoring.monitoring;

import java.util.HashMap;
import java.util.List;
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
	private Map<String, Double> means = new HashMap<>();

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
					updateMeanDistance(vid);
					double currentReputation = TrustManager.getInstance().getReputationRating(vid);
					double newFrequency = calculateFrequency(
							means.get(vid) != null ? means.get(vid) : 1 / DISTANCE_FACTOR, currentReputation);
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

	private void updateMeanDistance(String vid) throws DroneException {
		long start = System.currentTimeMillis();
		double dist = 0;
		int count = 0;
		ManagedDrone drone = DroneFleetManager.getInstance().getRegisteredDrone(vid);
		List<ManagedDrone> drns = DroneFleetManager.getInstance().getRegisteredDrones();
		for (ManagedDrone toCheck : drns) {
			if (toCheck == drone) {
				continue;
			}
			if (true || toCheck.getFlightModeState().isFlying() || toCheck.getFlightModeState().isInAir()) {
				dist += Math.abs(drone.getCoordinates().distance(toCheck.getCoordinates()));
			}
			count++;
		}
		double mean = dist / (count - 1);
		long duration = System.currentTimeMillis() - start;

		mean = mean / Math.pow((Math.sqrt(ZONE_WIDHT) + Math.sqrt(ZONE_HEIGHT)), 2);
		//LOGGER.info("Mean Distance: " + mean + "(" + duration + "ms)");
		if (mean > 0) {
			means.put(drone.getDroneName(), mean);
		}
	}

	private void updateFrequency(String vid, double frequency) {
		ManagedDrone drone;
		try {
			drone = DroneFleetManager.getInstance().getRegisteredDrone(vid);
			drone.sendCommand(new SetMonitoringFrequencyCommand(vid, new Double(frequency).longValue() * 1000));
		} catch (DroneException e) {
			LOGGER.error(e);
		}

	}

	private final double LOWER = 5;
	private final double UPPER = 25;
	private final double ZONE_WIDHT = 1000;
	private final double ZONE_HEIGHT = 1000;
	private final double DISTANCE_FACTOR = 2;

	private double calculateFrequency(double distance, double currentReputation) {

		// double frequency = (currentReputation / (distance * DISTANCE_FACTOR)) *
		// (UPPER - LOWER) + LOWER;
		double frequency = (currentReputation + (1 - distance) * distance) / (DISTANCE_FACTOR + 1) * (UPPER - LOWER)
				+ LOWER;
		return Math.floor(frequency);
	}

}
