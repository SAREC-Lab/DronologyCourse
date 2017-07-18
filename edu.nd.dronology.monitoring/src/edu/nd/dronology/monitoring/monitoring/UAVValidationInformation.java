package edu.nd.dronology.monitoring.monitoring;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.nd.dronology.monitoring.validation.ValidationEntry;

public class UAVValidationInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5879764867622165175L;
	private final String uavid;
	Map<String, ConstraintValidationInfo> infos = new ConcurrentHashMap<>();

	public UAVValidationInformation(String uavid) {
		this.uavid = uavid;
	}

	public void addResult(ValidationEntry validationResult) {
		String resultid = validationResult.getAssumptionid();
		if (infos.containsKey(resultid)) {
			infos.get(resultid).addResult(validationResult);
		} else {
			ConstraintValidationInfo info = new ConstraintValidationInfo(resultid);
			infos.put(resultid, info);
			info.addResult(validationResult);
		}
	}

	public Collection<ConstraintValidationInfo> getConstraintValidationInfos() {
		return Collections.unmodifiableCollection(infos.values());
	}

	public String getUAVId() {
		return uavid;
	}

}
