package edu.nd.dronology.monitoring.trust;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import edu.nd.dronology.monitoring.util.BenchmarkLogger;

/**
 * The reputation of a vehicle, defined by the weighted mean 
 * of the reputation of all assumptions
 * 
 * @author seanbayley
 *
 */
public class VehicleReputation {
	private String id;
	private Map<String, ReputationRating> assumptions;
	
	public VehicleReputation(String vid) {
		this.id = vid;
		assumptions = new HashMap<String, ReputationRating>();
	}
	
	public void addFeedback(String assumptionid, double r, double s) {
		if (!assumptions.containsKey(assumptionid))
			assumptions.put(assumptionid, new ReputationRating(assumptionid));
		assumptions.get(assumptionid).addFeedback(r, s);
	}
	
	/**
	 * Get the reputation rating of the vehicle.
	 * @return
	 */
	public double getReputation() {
		long start = System.currentTimeMillis();
		ReputationRating vehicleRep = new ReputationRating("");
		assumptions.entrySet()
			       .stream()
			       .forEach(entry -> {
			    	   String aId = entry.getKey();
			    	   ReputationRating rating = entry.getValue();
			    	   BenchmarkLogger.reportTrust(id, aId, rating.getReputationRating(), 0);
			    	   vehicleRep.addFeedback(rating.getR(), rating.getS());
			       });
		double vehicleRating = vehicleRep.getReputationRating();
		long duration = System.currentTimeMillis() - start;
		BenchmarkLogger.reportUAVTrust(this.id, vehicleRating, duration);
		return vehicleRating;
		
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