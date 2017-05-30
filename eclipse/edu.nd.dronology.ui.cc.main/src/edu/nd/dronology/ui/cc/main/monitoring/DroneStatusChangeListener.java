package edu.nd.dronology.ui.cc.main.monitoring;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;

public class DroneStatusChangeListener extends  UnicastRemoteObject implements IDroneStatusChangeListener {

	protected DroneStatusChangeListener() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9006236297389463545L;

	@Override
	public void droneStatusChanged(DroneStatus stat) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("DRONE ADDED: "+ stat.getID());

	}

}
