package edu.nd.dronology.monitoring.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.nd.dronology.monitoring.validation.ValidationResult.Result;

public interface IMonitoringValidationListener extends Remote {

	
	void constraintEvaluated(String uavid, String assumptionid, double weight, String message, Result result) throws RemoteException;

}
