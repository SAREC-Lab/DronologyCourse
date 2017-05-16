package edu.nd.dronology.ui.javafx;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import edu.nd.dronology.services.core.api.IBaseServiceProvider;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * Configurable utility class providing remote services via RMI.
 * 
 * @author Michael Vierhauser
 * 
 */
public class BaseServiceProvider implements IBaseServiceProvider {

	private static String ADDRESS_SCHEME = "rmi://%s:%s/Remote";

	private static IBaseServiceProvider INSTANCE = new BaseServiceProvider();

	private static final String DEFAULT_ADDRESS = "localhost";
	private static final int DEFAULT_PORT = 9898;

	private String ip;
	private int port;

	/**
	 * 
	 * @param ip
	 *          The ip address of the server
	 * @param port
	 *          The port of the remote service.
	 */
	public BaseServiceProvider(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Default constructor using standard port and localhost.
	 */
	public BaseServiceProvider() {
		this(DEFAULT_ADDRESS, DEFAULT_PORT);
	}

	/**
	 * @return The remote manager instance via RMI
	 * @throws DistributionException
	 *           if the service can not be retrieved.
	 */
	@Override
	public IRemoteManager getRemoteManager() throws DronologyServiceException {
		try {
			IRemoteManager manager = (IRemoteManager) Naming.lookup(String.format(ADDRESS_SCHEME, ip, port));
			return manager;
		} catch (MalformedURLException e) {
			throw new DronologyServiceException(e);
		} catch (RemoteException e) {
			throw new DronologyServiceException(e);
		} catch (NotBoundException e) {
			throw new DronologyServiceException(e);
		}

	}

	@Override
	public void init(String ip, int port) {
		this.ip = ip;
		this.port = port;

	}

	public static IBaseServiceProvider getInstance() {
		return INSTANCE;
	}

}
