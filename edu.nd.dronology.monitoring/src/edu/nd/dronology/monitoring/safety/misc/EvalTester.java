package edu.nd.dronology.monitoring.safety.misc;

import edu.nd.dronology.monitoring.validation.SafetyCaseValidator;

public class EvalTester {

	
	public static void main(String[] args) {
		new SafetyCaseValidator(SafetyCaseGeneration.getUAVSafetyCase()).validate();
	}
	
}
