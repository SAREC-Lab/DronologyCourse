package edu.nd.dronology.gstation.python.connector.dispatch;

public class PysicalDroneIdGenerator {

	public static String generate(String id, String groundstationid) {
		return id + ":" + groundstationid;
	}

}
