package edu.nd.dronology.monitoring.safety;

import edu.nd.dronology.monitoring.validation.EvalFunction;

public interface ISACAssumption {

	public static final String FUNCTION_PREFIX = "$L";
	public static final String ISAC_PREFIX = "#I_";
	public static final String ESAC_PREFIX = "#E_";

	String getId();

	String getExpression();

	boolean isMonitorable();

	boolean isPlugable();

	String getContent();

	boolean isStatic();

	void setStatic(boolean isStatic);

	void setMonitorable(boolean isMonitorable);

	void setContent(String content);

	void setExpression(String expression);

	void setPlugable(boolean isPlugable);

	String getParameterValue(String param);

	void addParameterValue(String key, String value);

	String getParameterMapping(String param);

	void addParameterMapping(String param, String mappedparam);

	EvalFunction getFunction();

}
