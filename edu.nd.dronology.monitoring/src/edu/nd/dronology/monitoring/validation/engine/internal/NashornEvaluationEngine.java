package edu.nd.dronology.monitoring.validation.engine.internal;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.nd.dronology.monitoring.validation.engine.EvaluationEngineException;
import edu.nd.dronology.monitoring.validation.engine.IEvaluationEngine;

public class NashornEvaluationEngine implements IEvaluationEngine {

	ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = manager.getEngineByName("nashorn");

	@Override
	public Object createFunction(String functionString) throws EvaluationEngineException{
		try {
			return engine.eval(functionString);
		} catch (ScriptException e) {
			throw new EvaluationEngineException(e);
		}
	}

	@Override
	public Object evaluateFunction(String callString) throws EvaluationEngineException{
		try {
			return engine.eval(callString);
		} catch (ScriptException e) {
			throw new EvaluationEngineException(e);
		}
	}

}
