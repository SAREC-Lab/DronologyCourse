package edu.nd.dronology.services.instances.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractRegisterableUnit;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.remote.RemoteInfo;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.remote.RemoteService;
import edu.nd.dronology.services.remote.rmi.RemoteManagerFactory;
import edu.nd.dronology.util.Immutables;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class RemoteConnectionServiceInstance extends AbstractServiceInstance implements
		IRemoteConnectionServiceInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8000279798861818920L;
	boolean started = false;
	// private AbstractRMIRemoteObject remoteObject;
//	private RemoteLogger remoteLogger;
	//private RemoteConnectionServiceInstance.CheckConnectionTask connectionTask;
	private static final ILogger LOGGER = LoggerProvider.getLogger(RemoteConnectionServiceInstance.class);

	List<IRemoteManager> remoteManager = new ArrayList<>();

	private static final Map<String, RemoteInfo> clients = new Hashtable<>();
	private Timer timer;

	public RemoteConnectionServiceInstance() {
		super(ServiceIds.SERVICE_REMOTE, "Managing remote connections");
		timer = new Timer();
	}

	@Override
	protected void doStartService() throws Exception {
		Properties prp = getConfigurationProperties();
		List<IRemoteManager> rmiObjects;
		if (prp.get(PORT_PROPERTY) != null) {
			// remoteObject = RemoteRMIRemoteObject.getInstance(Integer.parseInt((String) prp.get(PORT_PROPERTY)));
			rmiObjects = RemoteManagerFactory.createRMIObjects(Integer.parseInt((String) prp.get(PORT_PROPERTY)));

		} else {
			// remoteObject = RemoteRMIRemoteObject.getInstance(null);
			rmiObjects = RemoteManagerFactory.createRMIObjects(null);
		}
		remoteManager.addAll(rmiObjects);

		started = true;
//		remoteLogger = new RemoteLogger();
//		remoteLogger.start();

		for (IRemoteManager mng : remoteManager) {
			mng.initialize();
		}

//		connectionTask = new CheckConnectionTask();
//		timer.scheduleAtFixedRate(connectionTask, 10000, 60000);
	}

	@Override
	protected void doStopService() throws Exception {
		started = false;

		for (IRemoteManager mng : remoteManager) {
			mng.tearDown();
		}


	}

	@Override
	protected String getPropertyPath() {
		return "config/remoteServer.properties";
	}

	@Override
	public void register(RemoteInfo rInfo) {
		long timeStamp = System.currentTimeMillis();
		rInfo.addAttribute(AbstractRegisterableUnit.TIMESTAMP, Long.toString(timeStamp));
		if (clients.containsKey(rInfo.getID())) {
			LOGGER.info("Keep alive received: " + rInfo.toString());
		} else {
			LOGGER.info("Remote client registered: " + rInfo.toString());
		}
		clients.put(rInfo.getID(), rInfo);
	}

	@Override
	public void unregister(RemoteInfo rInfo) {
		clients.remove(rInfo);
	}

	@Override
	public Collection<RemoteInfo> getRegisteredRemoteClients() {
		synchronized (clients) {
			return Immutables.linkedListCopy(clients.values());
		}
	}

	@Override
	protected Class<?> getServiceClass() {
		return RemoteService.class;
	}

//	@Override
//	public void logExternal(LogEventAdapter event) {
//		remoteLogger.logExternal(event);
//	}

	@Override
	protected int getOrder() {
		return 0;
	}

	@Override
	public ServiceInfo getServiceInfo() {
		ServiceInfo sInfo = super.getServiceInfo();
		sInfo.addAttribute(ServiceInfo.ATTRIBUTE_TYPE, ServiceInfo.ATTRIBUTE_REMOTE);
		return sInfo;
	}

//	private class CheckConnectionTask extends TimerTask {
//
//		@Override
//		public void run() {
//			for (RemoteInfo info : getRegisteredRemoteClients()) {
//				if (!UnitUtil.isAlive(info)) {
//					unregister(info);
//				}
//			}
//		}
//	}

	@Override
	public void addRemoteManager(IRemoteManager manager) {
		remoteManager.add(manager);
		
	}
}
