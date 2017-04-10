package edu.nd.dronology.services.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * 
 * Abstract class for RMI remote objects taking care of registry re/unbind
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class AbstractRMIRemoteObject extends UnicastRemoteObject {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractRMIRemoteObject.class);
	protected int port = 9753;
	private String bindingName;
	protected Registry registry;

	protected AbstractRMIRemoteObject(int port, String bindingName) throws RemoteException {
		super();
		this.port = port;
		this.bindingName = bindingName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1684918850552214903L;

	public void initialize() throws RemoteException {
		if (registry == null) {
			try {
				registry = LocateRegistry.createRegistry(port);
			} catch (Exception e) {
				e.printStackTrace();
				registry = LocateRegistry.getRegistry(port);
			}
		}
		registry.rebind(bindingName, this);
		LOGGER.info(">> Binding '"+bindingName+"' established on port "+port);
	}

	public void tearDown() throws Exception {
		registry.unbind(bindingName);
		LOGGER.info(">> Binding '"+bindingName+"' removed on port "+port);
	}

}
