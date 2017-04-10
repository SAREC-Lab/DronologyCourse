package edu.nd.dronology.services.remote;

import java.rmi.RemoteException;

import edu.nd.dronology.services.core.api.ServiceStatus;
import edu.nd.dronology.services.core.listener.IServiceListener;
import edu.nd.dronology.services.core.remote.IRemoteServiceListener;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class RemoteServerProcessListenerAdapter implements IServiceListener {

	private static final ILogger LOGGER = LoggerProvider.getLogger(RemoteServerProcessListenerAdapter.class);
	private IRemoteServiceListener listener;

	public RemoteServerProcessListenerAdapter(IRemoteServiceListener listener) {
		this.listener = listener;
	}

	@Override
	public void statusChanged(ServiceStatus newState) {
		try {
			listener.statusChanged(newState);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public boolean equals(Object thatObject) {
		if (this == thatObject) {
			return true;
		}
		if (!(thatObject instanceof RemoteServerProcessListenerAdapter)) {
			return false;
		}
		return this.listener.equals(((RemoteServerProcessListenerAdapter) thatObject).listener);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((listener == null) ? 0 : listener.hashCode());
		return result;
	}
	
	
	

}
