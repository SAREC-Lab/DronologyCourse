package edu.nd.dronology.monitoring.validation.engine;

import edu.nd.dronology.monitoring.validation.engine.internal.NashornEvaluationEngine;

public class EngineFactory {

	public static IEvaluationEngine getEngine() {

		return new NashornEvaluationEngine();

	}

}
