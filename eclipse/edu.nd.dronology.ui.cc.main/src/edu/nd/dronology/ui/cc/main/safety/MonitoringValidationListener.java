package edu.nd.dronology.ui.cc.main.safety;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edu.nd.dronology.monitoring.service.IMonitoringValidationListener;
import edu.nd.dronology.monitoring.validation.ValidationResult.Result;

public class MonitoringValidationListener extends UnicastRemoteObject implements IMonitoringValidationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3562469810292978052L;
	private SafetyViewer safetyViewer;

	protected MonitoringValidationListener() throws RemoteException {
		super();

	}

	public MonitoringValidationListener(SafetyViewer safetyViewer) throws RemoteException {
		super();
		this.safetyViewer = safetyViewer;
	}

	@Override
	public void constraintEvaluated(String uavid, String assumptionid,double weight, String message, Result result)
			throws RemoteException {
		safetyViewer.newEvaluationMessage(message);

	}

}
