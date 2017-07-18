package edu.nd.dronology.monitoring.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.util.NullUtil;

public class EvalFunction {

	private final String id;
	private String functionString;
	private final List<String> parameterList;

	public EvalFunction(String id) {
		NullUtil.checkNull(id);
		this.id = id;
		parameterList = new ArrayList<>();
	}

	public void setFunctionString(String functionString) {
		this.functionString = functionString;

	}

	public void addParameter(String param) {
		NullUtil.checkNull(param);
		if (!parameterList.contains(param)) {
			parameterList.add(param);
		}

	}

	public String getFunctionString() {
		return functionString;
	}

	public List<String> getParameters() {
		return Collections.unmodifiableList(parameterList);
	}

	public String getId() {
		return id;
	}

}
