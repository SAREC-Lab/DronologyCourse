package edu.nd.dronology.monitoring.trust;

/**
 * The ReputationRating is based on the beta reputation system. 
 * Josang, Audun, and Roslan Ismail. "The beta reputation system."
 * 
 * Currently supports the base reputation rating (Equation 5) 
 */
public class ReputationRating {


	private double r;
	private double s;

	public double getR() {
		return r;
	}

	public double getS() {
		return s;
	}
	
	/**
	 * Add feedback based on the result of some "interaction".
	 *  @param success
	 *  	1 if the interaction was successful otherwise -1
	 */
	public void addFeedback(int success) {
		if (Math.abs(success) != 1) {
			throw new IllegalArgumentException(
					"the value of \"success\" should be 1 or -1.");
		}
		
		if (success == -1) {
			s += 1;
		}
		else {
			r += 1;
		}

	}
	
	/**
	 * Determine the reputation given:
	 * 	 
	 * 	 r (the number of positive results), and 
	 *   s (the number of negative results). 
	 *   
	 * The rating is calculated using Equation 5.
	 * Ratings range from (0, 1). 
	 */
	public double getReputationRating() {
		return (r + 1) / (r + s + 2); 
	}

	@Override
	public String toString() {
		return String.format("%f", getReputationRating());
	}
}