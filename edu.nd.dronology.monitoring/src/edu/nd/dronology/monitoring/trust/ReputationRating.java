package edu.nd.dronology.monitoring.trust;

import edu.nd.dronology.monitoring.util.BenchmarkLogger;

/**
 * The ReputationRating is based on the beta reputation system. Josang, Audun,
 * and Roslan Ismail. "The beta reputation system."
 * 
 * Currently supports the base reputation rating (Equation 5)
 * 
 * @author seanbayley
 */
public class ReputationRating {
	private static final int MIN_REQUIRED_FEEDBACK = 10;
	private String id;
	private int n;
	private double r;
	private double s;
	
	public ReputationRating(String id) {
		this.id = id;
		this.n = 0;
		this.r = 0.0;
		this.s = 0.0;
	}
	
	public double getR() {
		return r;
	}

	public double getS() {
		return s;
	}

	/**
	 * Add feedback based on the result of some "interaction".
	 * 
	 * @param r: positive feedback
	 * @param s: negative feedback
	 */
	public void addFeedback(double r, double s) {
		this.n += 1;
		this.r += r;
		this.s += s;
	}

	/**
	 * Determine the reputation given:
	 * 
	 * r (the amount of positive feedback), and s (the amount of negative feedback).
	 * 
	 * The rating is calculated using Equation 5. Ratings range from (0, 1).
	 */
	public double getReputationRating() {
		double rating;
		if (n < MIN_REQUIRED_FEEDBACK) 
			rating = 0.0;
		else
			rating = (r + 1) / (r + s + 2);
		
		return rating;
	}

	@Override
	public String toString() {
		return String.format("%f", getReputationRating());
	}
}