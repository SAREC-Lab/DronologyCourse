package edu.nd.dronology.services.core.base;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.api.ServiceStatus;
import edu.nd.dronology.services.core.listener.IServiceListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.util.NamedThreadFactory;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * 
 * Base class for all service instance implementations.
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class AbstractServiceInstance implements IServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractServiceInstance.class);
	private static final Properties EMPTY_PROPERTIES = new Properties();
	protected static final ExecutorService servicesExecutor = Executors.newFixedThreadPool(25,
			new NamedThreadFactory("Service-Threads"));

	private final String ID;
	private String description;
	private ServiceStatus status = ServiceStatus.STOPPED;
	private List<IServiceListener> serviceListener = new ArrayList<>();

	private Properties properties;
	protected String PORT_PROPERTY = "";

	/**
	 * 
	 * @param ID
	 *            The id of the service.
	 */
	public AbstractServiceInstance(String ID) {
		this(ID, "");
	}

	/**
	 * 
	 * @param ID
	 *            The id of the service.
	 * @param description
	 *            A description for the service.
	 */
	public AbstractServiceInstance(String ID, String description) {
		NullUtil.checkNull(ID, description);
		this.ID = ID;
		this.description = description;
		PORT_PROPERTY = "port-" + ID;
	}

	@Override
	public String getServiceID() {
		return ID;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ServiceStatus getStatus() {
		return status;
	}

	@Override
	public final boolean addServiceListener(IServiceListener listener) {
		return serviceListener.add(listener);
	}

	@Override
	public final boolean remoteServiceListener(IServiceListener listener) {
		return serviceListener.remove(listener);
	}

	@Override
	public final void startService() throws DronologyServiceException {
		if (getStatus() != ServiceStatus.RUNNING) {
			try {
				doStartService();
				setStatus(ServiceStatus.RUNNING);
				return;
			} catch (Exception e) {
				setStatus(ServiceStatus.ERROR);
				throw new DronologyServiceException("Error when starting service " + this.getClass().getSimpleName(),
						e);
			}
		}
		throw new DronologyServiceException("Service already running");
	}

	@Override
	public final void stopService() throws DronologyServiceException {
		if (getStatus() == ServiceStatus.RUNNING) {
			try {
				doStopService();
				setStatus(ServiceStatus.STOPPED);
				return;
			} catch (Exception e) {
				setStatus(ServiceStatus.ERROR);
				throw new DronologyServiceException("Error when stopping service", e);
			}
		}
		throw new DronologyServiceException("Service not running");
	}

	@Override
	public Properties getConfigurationProperties() {
		if (!hasProperties()) {
			return EMPTY_PROPERTIES;
		}
		if (properties == null) {
			properties = new Properties();
			// addGlobalProperties();
			if (getPropertyPath() == null || StringUtils.isEmpty(getPropertyPath())) {
				LOGGER.warn("No property path defined for " + this.getClass().getSimpleName());
				return properties;
			}

		}
		return properties;
	}

	/**
	 * Sets the status of the service. <br>
	 * May be called from a subsequent server-thread or delegate.<br>
	 * Not an interface method and therefore not intended to be called from
	 * outside!
	 * 
	 * @param status
	 */
	public final void setStatus(ServiceStatus status) {
		NullUtil.checkNull(status);
		// LOGGER.trace(this.getClass().getSimpleName() + " status set to: " +
		// status);
		this.status = status;
		for (IServiceListener listener : serviceListener) {
			listener.statusChanged(status);
		}
	}

	@Override
	public ServiceInfo getServiceInfo() {
		Map<String, String> attributes = new HashMap<>();
		Map<String, String> properties = new HashMap<>();

		for (Entry<Object, Object> e : getConfigurationProperties().entrySet()) {
			properties.put(e.getKey().toString(), e.getValue().toString());
		}
		ServiceInfo sInfo = new ServiceInfo(this.getServiceID(), this.getStatus(), this.getDescription(), attributes,
				properties, getOrder());
		sInfo.setServiceClass(getServiceClass().getCanonicalName());
		return sInfo;

	}

	protected void submitServiceThread(Runnable serverThread) {
		servicesExecutor.submit(serverThread);
	}

	protected void checkRunning() throws DronologyServiceException {
		if (getStatus() != ServiceStatus.RUNNING)
			throw new DronologyServiceException("Service not running!");
	}

	protected boolean hasProperties() {
		return true;
	}

	protected abstract Class<?> getServiceClass();

	protected abstract int getOrder();

	protected abstract String getPropertyPath();

	protected abstract void doStartService() throws Exception;

	protected abstract void doStopService() throws Exception;

}
