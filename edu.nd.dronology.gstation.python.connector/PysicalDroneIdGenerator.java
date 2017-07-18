package edu.nd.dronology.core.mission;

public class PysicalDroneIdGenerator {

	public static String generate(String id, String groundstationid) {
		return id + ":" + groundstationid;
	}

}
