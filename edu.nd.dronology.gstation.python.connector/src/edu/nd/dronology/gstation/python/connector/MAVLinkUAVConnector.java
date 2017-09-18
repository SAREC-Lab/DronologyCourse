//package edu.nd.dronology.gstation.python.connector;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
//import edu.nd.dronology.core.exceptions.DroneException;
//import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
//import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
//import edu.nd.dronology.gstation.python.connector.dispatch.DispatchQueueManager;
//import edu.nd.dronology.gstation.python.connector.dispatch.ReadDispatcher;
//import edu.nd.dronology.gstation.python.connector.dispatch.WriteDispatcher;
//import edu.nd.dronology.util.NamedThreadFactory;
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
//public class MAVLinkUAVConnector implements IDroneCommandHandler {
//
//	private static final ILogger LOGGER = LoggerProvider.getLogger(MAVLinkUAVConnector.class);
//
//	protected static final ExecutorService servicesExecutor = Executors.newFixedThreadPool(10,
//			new NamedThreadFactory("Groundstation-Threads"));
//
//	// socket for communication with python ground station
//	private Socket pythonSocket;
//	private final Map<String, IUAVPropertyUpdateNotifier> registeredListeners = new ConcurrentHashMap<>();
//	private ReadDispatcher readDispatcher;
//	private WriteDispatcher writeDispatcher;
//	private final String groundstationid;
//	private final DispatchQueueManager dispatchQueueManager;
//	private final String host;
//	private final int port;
//	private boolean connected;
//
//	public MAVLinkUAVConnector(String groundstationid, String host, int port) {
//		this.groundstationid = groundstationid;
//		dispatchQueueManager = new DispatchQueueManager(groundstationid);
//		this.host = host;
//		this.port = port;
//		this.connected = false;
//		connect();
//	}
//
//	private void connect() {
//		try {
//			InetAddress hostAddr = InetAddress.getByName(host);
//
//			String hostStr = hostAddr.toString();
//
//			LOGGER.info("Connecting to Python base " + hostStr + "@" + port);
//			pythonSocket = new Socket();
//			pythonSocket.connect(new InetSocketAddress(hostAddr, port), 5000);
//			// pythonSocket.setSoTimeout(20000);
//
//			LOGGER.hwInfo("Connected to " + pythonSocket.getInetAddress().toString() + "@" + pythonSocket.getPort());
//		//	readDispatcher = new ReadDispatcher(pythonSocket, dispatchQueueManager);
//			writeDispatcher = new WriteDispatcher(pythonSocket, dispatchQueueManager.getOutgoingCommandQueue());
//			servicesExecutor.submit(readDispatcher);
//			servicesExecutor.submit(writeDispatcher);
//			connected = true;
//		} catch (UnknownHostException e) {
//			LOGGER.hwFatal("Can't connect to Python Groundstation ");
//			scheduleReconnect();
//		} catch (Throwable e) {
//			LOGGER.hwFatal("Can't connect to Python Groundstation " + e.getMessage());
//			scheduleReconnect();
//		}
//	}
//
//	private void scheduleReconnect() {
//		// TODO implement me...
//
//	}
//
//	@Override
//	public void sendCommand(IDroneCommand cmd) throws DroneException {
//		LOGGER.hwInfo(groundstationid + " Sending Command to UAV " + cmd.toString());
//		dispatchQueueManager.send(cmd);
//	}
//
//	@Override
//	public void setStatusCallbackNotifier(String id, IUAVPropertyUpdateNotifier listener) throws DroneException {
//		if (registeredListeners.containsKey(id)) {
//			throw new DroneException("An listener with '" + id + "' is already registered");
//		}
//		registeredListeners.put(id, listener);
//		dispatchQueueManager.createDispatchThread(id, listener);
//	}
//
//	public void tearDown() {
//		try {
//			pythonSocket.close();
//			readDispatcher.tearDonw();
//			writeDispatcher.tearDown();
//			dispatchQueueManager.tearDown();
//		} catch (IOException e) {
//			LOGGER.hwFatal(e);
//		}
//	}
//
//	@Override
//	public String getHandlerId() {
//		return groundstationid;
//	}
//
//	public void registerMonitoringMessageHandler(IMonitoringMessageHandler monitoringhandler) {
//		dispatchQueueManager.registerMonitoringMessageHandler(monitoringhandler);
//
//	}
//
//	public void registerSafetyValidator(IUAVSafetyValidator validator) {
//		dispatchQueueManager.registerSafetyValidator(validator);
//
//	}
//
//}
