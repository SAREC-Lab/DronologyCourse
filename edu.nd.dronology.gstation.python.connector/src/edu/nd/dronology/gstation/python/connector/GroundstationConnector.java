package edu.nd.dronology.gstation.python.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.fleet.RuntimeDroneTypes;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.ConnectionResponseCommand;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.gstation.python.connector.connect.IncommingGroundstationConnectionServer;
import edu.nd.dronology.gstation.python.connector.dispatch.DispatchQueueManager;
import edu.nd.dronology.gstation.python.connector.dispatch.ReadDispatcher;
import edu.nd.dronology.gstation.python.connector.dispatch.WriteDispatcher;
import edu.nd.dronology.gstation.python.connector.messages.AbstractUAVMessage;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVMessageFactory;
import edu.nd.dronology.gstation.python.connector.service.DroneConnectorService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class GroundstationConnector implements IDroneCommandHandler, Runnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(GroundstationConnector.class);

	protected static final ExecutorService servicesExecutor = Executors.newFixedThreadPool(10,
			new NamedThreadFactory("Groundstation-Threads"));

	// socket for communication with python ground station
	private Socket socket;
	private final Map<String, IUAVPropertyUpdateNotifier> registeredListeners = new ConcurrentHashMap<>();
	private ReadDispatcher readDispatcher;
	private WriteDispatcher writeDispatcher;
	private String groundstationid;
	private DispatchQueueManager dispatchQueueManager;
	private boolean connected;
	private IncommingGroundstationConnectionServer server;

	public GroundstationConnector(IncommingGroundstationConnectionServer server, Socket socket) {

		this.connected = false;
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void sendCommand(IDroneCommand cmd) throws DroneException {
		LOGGER.hwInfo(groundstationid + " Sending Command to UAV " + cmd.toString());
		dispatchQueueManager.send(cmd);
	}

	@Override
	public void setStatusCallbackNotifier(String id, IUAVPropertyUpdateNotifier listener) throws DroneException {
		if (registeredListeners.containsKey(id)) {
			throw new DroneException("An listener with '" + id + "' is already registered");
		}
		registeredListeners.put(id, listener);
		dispatchQueueManager.createDispatchThread(id, listener);
	}

	public void tearDown() {
		readDispatcher.tearDonw();
		writeDispatcher.tearDown();
		dispatchQueueManager.tearDown();
	}

	@Override
	public String getHandlerId() {
		return groundstationid;
	}

	public void registerMonitoringMessageHandler(IMonitoringMessageHandler monitoringhandler) {
		dispatchQueueManager.registerMonitoringMessageHandler(monitoringhandler);

	}

	public void registerSafetyValidator(IUAVSafetyValidator validator) {
		dispatchQueueManager.registerSafetyValidator(validator);

	}

	@Override
	public void run() {
		LOGGER.info("GroundstationConnector started");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line = reader.readLine();
			AbstractUAVMessage msg = UAVMessageFactory.create(line);
			if (!(msg instanceof ConnectionRequestMessage)) {
				LOGGER.hwFatal("Invalid Connection Request from groundstation! " + line);
				return;
			}
			boolean connectionSuccess = false;
			try {
				DroneConnectorService.getInstance().registerConnection(this, (ConnectionRequestMessage) msg);
				this.groundstationid = msg.getUavid();
				setupConnection();
				connectionSuccess = true;
			} catch (GroundStationException ex) {
				LOGGER.hwFatal(ex.getMessage());
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			ConnectionResponseCommand ackCommand = new ConnectionResponseCommand(groundstationid, connectionSuccess);
			writer.write(ackCommand.toJsonString());
			writer.write(System.lineSeparator());
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.hwFatal("Error when establishing connection to groundstation" + e.getMessage());
		}

	}

	private void setupConnection() {
		try {
			dispatchQueueManager = new DispatchQueueManager(groundstationid);
			readDispatcher = new ReadDispatcher(socket, dispatchQueueManager);
			writeDispatcher = new WriteDispatcher(socket, dispatchQueueManager.getOutgoingCommandQueue());
			servicesExecutor.submit(readDispatcher);
			servicesExecutor.submit(writeDispatcher);
			RuntimeDroneTypes.getInstance().registerCommandHandler(this);
			connected = true;
		} catch (Throwable e) {
			LOGGER.hwFatal("Can't connect to Python Groundstation " + e.getMessage());
		}
	}

	private void scheduleReconnect() {
		// TODO Auto-generated method stub

	}

}
