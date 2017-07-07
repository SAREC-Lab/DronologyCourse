package edu.nd.dronology.monitoring.monitoring;

import java.util.HashMap;
import java.util.Map;

public class RegisteredPropertyDescriptor {

	private String uavid;

	private final Map<String, String> parameterList;

	public RegisteredPropertyDescriptor(String uavid) {
		this.uavid = uavid;
		parameterList = new HashMap<>();
	}

	public void addParameter(String parameterName, String type) {
		parameterList.put(parameterName, type);
	}

}
