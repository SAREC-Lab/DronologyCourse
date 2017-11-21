//package at.jku.mevss.eventdistributor.server.impls.msocket;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.net.Socket;
//import java.util.UUID;
//
//import at.jku.mevss.eventdistributor.core.DistributionException;
//import at.jku.mevss.eventdistributor.core.transmit.ConnectionInfoObject;
//import at.jku.mevss.eventdistributor.core.transmit.TransmittableEventObject;
//import at.jku.mevss.eventdistributor.core.transmit.TransmittableObject;
//import at.jku.mevss.eventdistributor.core.util.TimingManager;
//import at.jku.mevss.eventdistributor.server.services.MonitoringService;
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
///**
// * 
// * @author Michael Vierhauser
// * 
// *         Runnable handling new incoming connections requests from the {@link MultiThreadedSocketDistributionServer}.
// * 
// *         A Connection request starts with a {@link ConnectionInfoObject} followed by the actual data.
// * 
// */
//public class ConnectionHandler implements Runnable {
//	private static final ILogger LOGGER = LoggerProvider.getLogger(ConnectionHandler.class);
//	private Socket socket;
//	private ObjectInputStream input;
//	private boolean isAlive = true;
//	private String id;
//	private String connectionid;
//
//	public ConnectionHandler(Socket socket) {
//		this.socket = socket;
//		this.connectionid = UUID.randomUUID().toString();
//	}
//
//	@Override
//	public void run() {
//		try {
//			LOGGER.info("Creating TransmitChannel @ port " + socket.getPort() + " ip address: "
//					+ socket.getInetAddress().toString());
//
//			try {
//				input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
//			} catch (Exception e) {
//				LOGGER.error("Error when retrieving input stream", e);
//				isAlive = false;
//			}
//
//			isAlive = doSetup();
//
//			while (isAlive && !Thread.currentThread().interrupted()) {
//				Object o = null;
//				try {
//					o = input.readObject();
//					// input.reset();
//
//				} catch (ClassNotFoundException e) {
//					LOGGER.error("Error when retrieving object", e);
//				} catch (IOException e) {
//					LOGGER.error("Error when retrieving object", e);
//					MonitoringService.getInstance().unregisterConnection(id);
//					LOGGER.info("Unregistering Connection " + id);
//				}
//
//				if (o instanceof TransmittableEventObject) {
//					distributor.put((TransmittableEventObject) o);
//					if (TimingManager.trackStatistics()) {
//						MonitoringService.getInstance().addCount(id);
//					}
//				} else if (o instanceof TransmittableObject[]) {
//					TransmittableObject[] elems = (TransmittableObject[]) o;
//					for (TransmittableObject ob : elems) {
//						distributor.put(ob);
//					}
//					if (TimingManager.trackStatistics()) {
//						MonitoringService.getInstance().addCount(id, elems.length);
//					}
//
//				} else if (o == null) {
//					LOGGER.error("Null received");
//				} else {
//					LOGGER.error("Unprocessable element from type " + o.getClass() + " received");
//				}
//			}
//			LOGGER.info("Connection thread @" + socket.getPort() + " terminated.");
//		} catch (Exception e) {
//			LOGGER.error("Failed creating transmit channel!", e);
//		} finally {
//			try {
//				MonitoringService.getInstance().unregisterConnection(id);
//				LOGGER.info("Unregistering Connection " + id);
//			} catch (DistributionException e) {
//				LOGGER.error(e);
//			}
//			try {
//				if (socket != null) {
//					socket.close();
//				}
//
//			} catch (IOException e) {
//				LOGGER.error(e);
//			}
//			try {
//				if (input != null) {
//					input.close();
//				}
//			} catch (IOException e) {
//				LOGGER.error(e);
//			}
//		}
//	}
//
//	private boolean doSetup() {
//		Object o = null;
//		try {
//			o = input.readObject();
//			// input.reset();
//
//		} catch (ClassNotFoundException e) {
//			LOGGER.error("Error when retrieving object", e);
//		} catch (IOException e) {
//			LOGGER.error("Error when retrieving object", e);
//		} catch (Throwable e) {
//			LOGGER.error("Error when retrieving object", e);
//		}
//		if (o instanceof ConnectionInfoObject) {
//			ConnectionInfoObject cinfo = (ConnectionInfoObject) o;
//			id = cinfo.getId();
//			MonitoringService.getInstance().registerConnection(cinfo.getId(), cinfo.getId(), cinfo.getIPAddress(),
//					socket.getPort());
//			return true;
//		} else {
//			LOGGER
//					.error("First object to be received needs to be of type " + ConnectionInfoObject.class + "but was " + o != null ? o
//							.getClass() : "null");
//			return false;
//		}
//	}
//
//	public String getConnectionId() {
//		return connectionid;
//	}
//
//}
