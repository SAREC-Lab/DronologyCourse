package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PysicalDroneIdGenerator {

	private static final String pattern ="\\:(%s$)";
	
	public static String generate(String id, String groundstationid) {
		return id + ":" + groundstationid;
	}
	
	
	public static boolean isAssociatedWithGroundstation(String id, String groundstationid) {
		String pat = String.format(pattern, groundstationid);
		
		Matcher m = Pattern.compile(pat).matcher(id);
		return m.find();
	
	}

}

