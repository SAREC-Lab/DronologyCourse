package edu.nd.dronology.monitoring.safety.internal;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.nd.dronology.monitoring.safety.ISACAssumption;
import edu.nd.dronology.monitoring.validation.EvalFunction;

public class Assumption implements ISACAssumption {

	private final String id;
	private String content;
	private String expression;
	private boolean isMonitorable;
	private boolean isStatic;
	private boolean isPlugable;
	private Map<String, String> params = new HashMap<>();
	private Map<String, String> mapping = new HashMap<>();

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\s*(\\w+)");

	public Assumption() {
		this.id = UUID.randomUUID().toString();
	}

	public Assumption(String id) {
		this.id = id;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public void setMonitorable(boolean isMonitorable) {
		this.isMonitorable = isMonitorable;
	}

	@Override
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	@Override
	public void setPlugable(boolean isPlugable) {
		this.isPlugable = isPlugable;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public boolean isMonitorable() {
		return isMonitorable;
	}

	@Override
	public boolean isPlugable() {
		return isPlugable;
	}

	@Override
	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getParameterValue(String param) {
		return params.get(param);
	}

	@Override
	public void addParameterValue(String key, String value) {
		params.put(key, value);

	}

	@Override
	public String getParameterMapping(String param) {
		return mapping.get(param);
	}

	@Override
	public void addParameterMapping(String param, String mappedparam) {
		mapping.put(param, mappedparam);

	}

	@Override
	public EvalFunction getFunction() {

		EvalFunction function = new EvalFunction(id);
		Matcher matcher = VARIABLE_PATTERN.matcher(expression);
		while (matcher.find()) {
			String param = matcher.group(1);
			function.addParameter("#" + param);
		}

		String functionString = expression.replace(FUNCTION_PREFIX, "function " + id);
		functionString = functionString.replaceAll("#", "");
		functionString = functionString.replace(":", "{return");
		StringBuilder sb = new StringBuilder(functionString);
		sb.append("}");
		function.setFunctionString(sb.toString());
		return function;
	}

	Object readResolve() throws ObjectStreamException {
		if (params == null) {
			params = new HashMap<>();
		}
		if (mapping == null) {
			mapping = new HashMap<>();
		}
		return this;
	}

}
