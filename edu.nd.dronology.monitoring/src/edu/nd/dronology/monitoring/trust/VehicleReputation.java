package edu.nd.dronology.monitoring.trust;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * The reputation of a vehicle, defined by the weighted mean 
 * of the reputation of all assumptions
 * @author seanbayley
 *
 */
public class VehicleReputation {
	private Map<String, ReputationRating> assumptions;

	public ReputationRating getReputationRating(String assumptionId) throws IllegalArgumentException {
		if (!assumptions.containsKey(assumptionId))
			throw new IllegalArgumentException(String.format("unrecognized assumptionId %s", assumptionId));
		return assumptions.get(assumptionId);
	}
	public double getReputation() {
		// TODO: implement this (figure out where weights come from)
		return 0.0;
	}
	/**
	 * Get all assumption ids registered with this vehicle.
	 * @return
	 */
	public Iterable<String> getAssumptionIds() {
		return assumptions.keySet();
	}
	
	/**
	 * Get the assumption K, V pairs.
	 * @return
	 */
	public Iterable<Entry<String, ReputationRating>> getAssumptionEntrySet() {
		return assumptions.entrySet();
	}

	@Override
	public String toString() {
		return assumptions.entrySet()
			          .stream()
			          .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue().toString()))
			          .collect(Collectors.joining(", "));
	}
}