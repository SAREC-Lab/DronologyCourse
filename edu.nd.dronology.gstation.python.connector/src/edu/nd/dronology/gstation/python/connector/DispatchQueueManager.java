package edu.nd.dronology.gstation.python.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import edu.nd.dronology.core.IDroneStatusUpdateListener;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.core.vehicle.internal.PhysicalDrone;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.dronesetup.DroneSetupService;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;
/**
 *  The {@link DispatchQueueManager} handles both <i>incoming</i> and <i>outgoing</i> queues. </br> 
 * 	Incoming queues contain {@link UAVState} received from the UAV to be dispatched to the {@link PhysicalDrone}.<br>
 * 	The outgoing queue contains {@link IDroneCommand}s being sent to the UAV.
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

	Map<String, BlockingQueue<UAVState>> queueMap = new ConcurrentHashMap<>();
	List<StatusDispatchThread> dispatchThreads = new ArrayList<>();

	private BlockingQueue<IDroneCommand> outgoingCommandQueue = new LinkedBlockingDeque<>(100);

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static DispatchQueueManager getInstance() {

		if (INSTANCE == null) {
			synchronized (DispatchQueueManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new DispatchQueueManager();
				}
			}
		}
		return INSTANCE;
	}

	public void postDroneStatusUpdate(String id, UAVState status) {
	
		synchronized (queueMap) {
			boolean success = false;
			if (queueMap.containsKey(id)) {
				success = queueMap.get(id).offer(status);
			} else {
				LinkedBlockingQueue<UAVState> newQueue = new LinkedBlockingQueue<>(100);
				queueMap.put(id, newQueue);
				registerNewDrone(id, status);
				success = true;
			}
			if (!success) {
				LOGGER.hwFatal("Buffer overflow! '" + id + "'");
			}
		}
		

	}

	private void registerNewDrone(String id, UAVState status) {
		LOGGER.hwInfo("New drone registered with  '" + id + "' -> " + status.toString());
		DroneInitializationInfo info = new DroneInitializationInfo(id, id, status.getLocation());
		try {
			DroneSetupService.getInstance().initializeDrones(info);
		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		}

	}

	public void createDispatchThread(String id, IDroneStatusUpdateListener listener) {
		BlockingQueue<UAVState> queue;
		synchronized (queueMap) {
			if (queueMap.containsKey(id)) {
				queue = queueMap.get(id);
			} else {
				queue = new LinkedBlockingQueue<>();
			}
		}
		StatusDispatchThread thread = new StatusDispatchThread(queue, listener);
		dispatchThreads.add(thread);
		LOGGER.hwInfo("New Dispatch-Thread for UAV '" + id + "' created");
		SERVICE_EXECUTOR.submit(thread);
	}

	public void tearDown() {
		for (StatusDispatchThread th : dispatchThreads) {
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

}
