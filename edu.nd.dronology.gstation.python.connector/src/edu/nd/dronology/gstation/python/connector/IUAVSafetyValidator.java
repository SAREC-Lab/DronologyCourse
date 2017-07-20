package edu.nd.dronology.gstation.python.connector;

public interface IUAVSafetyValidator {

	boolean validate(String uavid, String safetyCase);

}
