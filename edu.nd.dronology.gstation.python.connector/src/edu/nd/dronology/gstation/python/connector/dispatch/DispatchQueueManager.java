package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.core.vehicle.internal.PhysicalDrone;
import edu.nd.dronology.gstation.python.connector.IMonitoringMessageHandler;
import edu.nd.dronology.gstation.python.connector.IUAVSafetyValidator;
import edu.nd.dronology.gstation.python.connector.messages.UAVHandshakeMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.info.DroneInitializationInfo.DroneMode;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * The {@link DispatchQueueManager} handles both <i>incoming</i> and
 * <i>outgoing</i> queues. </br>
 * Incoming queues contain {@link UAVState} received from the UAV to be
 * dispatched to the {@link PhysicalDrone}.<br>
 * The outgoing queue contains {@link IDroneCommand}s being sent to the UAV.
 * 
 * @author Michael Vierhauser
 *
 */
public class DispatchQueueManager {

	private static volatile DispatchQueueManager INSTANCE = null;

	private static final ILogger LOGGER = LoggerProvider.getLogger(DispatchQueueManager.class);

	private static final int NUM_THREADS = 20;
	private static final ExecutorService SERVICE_EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS,
			new NamedThreadFactory("Dispatch-Threads"));

	private static final boolean USE_MONITORING = true;

	Map<String, BlockingQueue<UAVStateMessage>> queueMap = new ConcurrentHashMap<>();
	List<AbstractStatusDispatchThread> dispatchThreads = new ArrayList<>();

	private BlockingQueue<IDroneCommand> outgoingCommandQueue = new LinkedBlockingDeque<>(100);
	private BlockingQueue<UAVMonitoringMessage> monitoringQueue = new LinkedBlockingDeque<>(100);
	private List<IMonitoringMessageHandler> handlers = new ArrayList<>();

	private final String groundstationid;

	private IUAVSafetyValidator validator;

	public DispatchQueueManager(String groundstationid) {
		this.groundstationid = groundstationid;
		if (USE_MONITORING) {
			createMonitoringDispatchThread(monitoringQueue);
		}
	}

	public void postDroneStatusUpdate(String id, UAVStateMessage status) {
		LOGGER.info("Message " + status.getClass().getSimpleName() + " received :: " + groundstationid);

		synchronized (queueMap) {
			boolean success = false;
			if (queueMap.containsKey(id)) {
				success = queueMap.get(id).offer(status);
			} else {
				// LinkedBlockingQueue<UAVStateMessage> newQueue = new
				// LinkedBlockingQueue<>(100);
				// queueMap.put(id, newQueue);
				// registerNewDrone(id, status);
				// success = true;
				LOGGER.hwFatal("No uav with id '" + id + "' registered!");
			}
			if (!success) {
				LOGGER.hwFatal("Buffer overflow! '" + id + "'");
			}
		}
	}

	// private void registerNewDrone(String id, UAVStateMessage status) {
	// LOGGER.hwInfo("New drone registered with '" + id + "' -> " +
	// status.toString());
	// DroneInitializationInfo info = new DroneInitializationInfo(
	// PysicalDroneIdGenerator.generate(id, groundstationid),
	// DroneMode.MODE_PHYSICAL, id,
	// status.getLocation());
	// try {
	// DroneSetupService.getInstance().initializeDrones(info);
	// } catch (DronologyServiceException e) {
	// LOGGER.error(e);
	// }
	//
	// }

	private void registerNewDrone(String uavid, UAVHandshakeMessage message) {
		LOGGER.hwInfo("New drone registered with  '" + uavid + "' -> " + message.toString());
		DroneInitializationInfo info = new DroneInitializationInfo(
				PysicalDroneIdGenerator.generate(uavid, groundstationid), DroneMode.MODE_PHYSICAL, uavid,
				message.getHome());
		try {
			DroneSetupService.getInstance().initializeDrones(info);
		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		}

	}

	public void createDispatchThread(String id, IUAVPropertyUpdateNotifier listener) {
		try {
			BlockingQueue<UAVStateMessage> queue;
			synchronized (queueMap) {
				if (queueMap.containsKey(id)) {
					queue = queueMap.get(id);
				} else {
					queue = new LinkedBlockingQueue<>(DronologyConstants.NR_MESSAGES_IN_QUEUE);
					queueMap.put(id, queue);
				}
			}
			StatusDispatchThread thread = new StatusDispatchThread(queue, listener);
			dispatchThreads.add(thread);
			LOGGER.hwInfo("New Dispatch-Thread for UAV '" + id + "' created");
			SERVICE_EXECUTOR.submit(thread);
		} catch (Throwable t) {
			LOGGER.error(t);
		}
	}

	private void createMonitoringDispatchThread(BlockingQueue<UAVMonitoringMessage> queue) {
		MonitoringDispatchThread thread = new MonitoringDispatchThread(queue, handlers);
		dispatchThreads.add(thread);
		LOGGER.hwInfo("New Monitoring Dispatch-Thread created");
		SERVICE_EXECUTOR.submit(thread);
	}

	public void tearDown() {
		for (AbstractStatusDispatchThread<?> th : dispatchThreads) {
			th.tearDown();
		}
		SERVICE_EXECUTOR.shutdown();
	}

	public BlockingQueue<IDroneCommand> getOutgoingCommandQueue() {
		return outgoingCommandQueue;
	}

	public void send(IDroneCommand cmd) {
		boolean taken = outgoingCommandQueue.offer(cmd);
		LOGGER.hwInfo("Command added to queue!");
		if (!taken) {
			LOGGER.hwFatal("Outgoing Command queue limit reached - command dropped!");
		}

	}

	public void postMonitoringMessage(UAVMonitoringMessage message) {
		if (!USE_MONITORING) {
			return;
		}
		LOGGER.info("Message " + message.getClass().getSimpleName() + " received :: " + groundstationid);
		boolean success = false;
		success = monitoringQueue.offer(message);
		if (!success) {
			LOGGER.warn("MonitoringQueue is Full!");
		}
	}

	public void registerMonitoringMessageHandler(IMonitoringMessageHandler handler) {
		synchronized (handlers) {
			handlers.add(handler);
		}
	}

	public void postDoneHandshakeMessage(String uavid, UAVHandshakeMessage message) {
		registerNewDrone(uavid, message);
		LOGGER.info("Message " + message.getClass().getSimpleName() + " received :: " + groundstationid);
		if (validator != null) {
			if (message.getSafetyCase() == null) {
				LOGGER.error("No safety information provided");
			} else {
				validator.validate(uavid, message.getSafetyCase());
			}

		}
	}

	public void registerSafetyValidator(IUAVSafetyValidator validator) {
		this.validator = validator;

	}

}
