package edu.nd.dronology.monitoring.validation.engine;

public interface IEvaluationEngine {

	Object createFunction(String functionString) throws EvaluationEngineException;

	Object evaluateFunction(String callString)throws EvaluationEngineException; 

}
