package edu.nd.dronology.gstation.python.connector.connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.gstation.python.connector.GroundstationConnector;
import edu.nd.dronology.gstation.python.connector.dispatch.DispatchQueueManager;
import edu.nd.dronology.gstation.python.connector.dispatch.ReadDispatcher;
import edu.nd.dronology.gstation.python.connector.messages.AbstractUAVMessage;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * 
 */

public class IncommingGroundstationConnectionServer implements Runnable {
	private int port = 1234;
	private ServerSocket serverSocket;
	private boolean cont = true;
	private static final ILogger LOGGER = LoggerProvider.getLogger(IncommingGroundstationConnectionServer.class);
	private String URL = "127.0.0.1";
	private Map<String, Future> activeConnections = new HashMap<>();

	ExecutorService connectionExecutor = Executors.newFixedThreadPool(DronologyConstants.MAX_GROUNDSTATIONS,
			new NamedThreadFactory("Connection-Socket-Threads"));

	public IncommingGroundstationConnectionServer() {

	}

	@Override
	public void run() {

		serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 3000);
			// server.setReuseAddress(true);

			LOGGER.info("Incomming-Groundstation Connection Server listening on port: " + port);
			// server.setSoTimeout(1000);

			while (cont) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
					GroundstationConnector handler = new GroundstationConnector(this,socket);
					handleConnection(handler);
				} catch (SocketException e) {
					LOGGER.info("Socket was closed!");
				} catch (IOException e) {
					LOGGER.error(e);

				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

	}

	public void stop() {
		cont = false;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
		closeConnections();

	}

	private void closeConnections() {
		synchronized (activeConnections) {
			for (Future f : activeConnections.values()) {
				f.cancel(true);
			}
			activeConnections.clear();
		}

	}

//	protected void handleConnection(ReadDispatcher connectionHandler) {
//		if (activeConnections.size() >= DronologyConstants.MAX_GROUNDSTATIONS) {
//			LOGGER.warn("Connection Limit reached - no new parallel connections can be added!");
//			return;
//		}
//		Future<?> future = connectionExecutor.submit(connectionHandler);
//		activeConnections.put(connectionHandler.getConnectionId(), future);
//	}

	protected void removeConnection(String connectionId) {
		if (activeConnections.containsKey(connectionId)) {
			LOGGER.info("Removing connection!" + connectionId);
			Future<?> conn = activeConnections.remove(connectionId);
			conn.cancel(true);
		} else {
			LOGGER.warn("Connection with id " + connectionId + " not found");
		}
	}

	public void handleConnection(GroundstationConnector connectionHandler) {
		if (activeConnections.size() >= DronologyConstants.MAX_GROUNDSTATIONS) {
			LOGGER.warn("Connection Limit reached - no new parallel connections can be added!");
			return;
		}
		
	Future<?> future = connectionExecutor.submit(connectionHandler);		
	}
	
	public void registerConnection(GroundstationConnector connector, ConnectionRequestMessage msg) {
		LOGGER.info("Connection established with groundstation '"+msg.getUavid()+"'");
		activeConnections.put(msg.getUavid(), null);
	}

}
