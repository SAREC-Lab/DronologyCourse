package edu.nd.dronology.monitoring.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMonitoringValidationListener extends Remote  {

	void constraintEvaluated(String uavid, String assumptionid, String message) throws RemoteException;

}
