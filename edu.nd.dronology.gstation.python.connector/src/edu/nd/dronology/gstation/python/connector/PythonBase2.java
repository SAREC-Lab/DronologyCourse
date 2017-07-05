package edu.nd.dronology.gstation.python.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.IDroneStatusUpdateListener;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class PythonBase2 implements IDroneCommandHandler {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PythonBase2.class);

	protected static final ExecutorService servicesExecutor = Executors.newFixedThreadPool(5,
			new NamedThreadFactory("Groundstation-Threads"));

	// socket for communication with python ground station
	private Socket pythonSocket;

	private final Map<String, IDroneStatusUpdateListener> registeredListeners = new ConcurrentHashMap<>();

	private ReadDispatcher readDispatcher;

	private WriteDispatcher writeDispatcher;

	@Discuss(discuss = "port+ip should be specified in property file/passed to base when initialized")
	public PythonBase2() {
		try {

			//InetAddress hostAddr = InetAddress.getByName("dewey.cse.nd.edu");
			InetAddress hostAddr = InetAddress.getByName("10.13.59.197");
			int port = 1234;
			String hostStr = hostAddr.toString();

			LOGGER.info("Connecting to Python base " + hostStr + "@" + port);
			pythonSocket = new Socket();
			pythonSocket.connect(new InetSocketAddress(hostAddr, port), 5000);
			pythonSocket.setSoTimeout(20000);

			LOGGER.hwInfo("Connected to " + pythonSocket.getInetAddress().toString() + "@" + pythonSocket.getPort());
			readDispatcher = new ReadDispatcher(pythonSocket);
			writeDispatcher = new WriteDispatcher(pythonSocket, DispatchQueueManager.getInstance().getOutgoingCommandQueue());
			servicesExecutor.submit(readDispatcher);
			servicesExecutor.submit(writeDispatcher);

		} catch (UnknownHostException e) {
			LOGGER.hwFatal("Can't connect to Python Groundstation ");
		} catch (Throwable e) {
			LOGGER.hwFatal("Can't connect to Python Groundstation " + e.getMessage());
		}
	}

	@Override
	public void sendCommand(IDroneCommand cmd) throws DroneException {
		LOGGER.hwInfo("Sending Command to UAV " + cmd.toString());
		DispatchQueueManager.getInstance().send(cmd);
	}

	@Override
	public void setStatusCallbackListener(String id, IDroneStatusUpdateListener listener) throws DroneException {
		if (registeredListeners.containsKey(id)) {
			throw new DroneException("An listener with '" + id + "' is already registered");
		}
		registeredListeners.put(id, listener);
		DispatchQueueManager.getInstance().createDispatchThread(id, listener);
	}

	public void tearDown() {
		try {
			pythonSocket.close();
			readDispatcher.tearDonw();
			writeDispatcher.tearDown();
			DispatchQueueManager.getInstance().tearDown();
		} catch (IOException e) {
			LOGGER.hwFatal(e);
		}
	}

}
