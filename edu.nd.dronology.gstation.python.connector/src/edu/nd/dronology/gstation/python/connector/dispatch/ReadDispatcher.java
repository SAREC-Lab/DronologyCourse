package edu.nd.dronology.gstation.python.connector.dispatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.util.FormatUtil;
import edu.nd.dronology.gstation.python.connector.GroundStationException;
import edu.nd.dronology.gstation.python.connector.connect.IncommingGroundstationConnectionServer;
import edu.nd.dronology.gstation.python.connector.messages.AbstractUAVMessage;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVHandshakeMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVMessageFactory;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ReadDispatcher implements Runnable {

	private InputStream inputStream;
	private AtomicBoolean cont = new AtomicBoolean(false);
	private static final ILogger LOGGER = LoggerProvider.getLogger(ReadDispatcher.class);

	private BufferedReader reader;
	private DispatchQueueManager dispatchQueueManager;
	private IncommingGroundstationConnectionServer server;

	public ReadDispatcher(Socket pythonSocket, DispatchQueueManager dispatchQueueManager) {
		try {
			this.server = server;
			this.dispatchQueueManager = dispatchQueueManager;
			inputStream = pythonSocket.getInputStream();
			cont.set(true);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void run() {
		try {
			LOGGER.info("Read-Dispatcher started");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			while (cont.get() && !Thread.currentThread().interrupted()) {
				String line = reader.readLine();
				if (line != null) {
					// TODO: create the timestamp before deserializing the
					// object....
					try {
						AbstractUAVMessage<?> msg = UAVMessageFactory.create(line);
						processMessage(msg);
						if (msg == null) {
							LOGGER.hwFatal("Error when parsing incomming message '" + line + "'");
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						LOGGER.hwFatal("Error when parsing incomming message '" + line + "' " + ex.getMessage());
					}

				} else {
					LOGGER.hwFatal("null message received!");
				}

			}
			LOGGER.info("Reader Thread shutdown");
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (reader != null) {
					reader.close();
				}

			} catch (IOException e) {
				LOGGER.error(e);
			}

		} catch (Throwable t) {
			LOGGER.error(t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	private void processMessage(AbstractUAVMessage<?> message) {
		if (message instanceof UAVStateMessage) {
			// LOGGER.hwInfo("[" + message.getClass().getSimpleName() + "]
			// "+FormatUtil.formatTimestamp(message.getTimestamp(),
			// FormatUtil.FORMAT_YEAR_FIRST_MILLIS)
			// + " - " + message.toString());
			dispatchQueueManager.postDroneStatusUpdate(message.getUavid(), (UAVStateMessage) message);

		} else if (message instanceof UAVHandshakeMessage) {
			LOGGER.hwInfo(FormatUtil.formatTimestamp(message.getTimestamp(), FormatUtil.FORMAT_YEAR_FIRST_MILLIS)
					+ " - " + message.toString());
			dispatchQueueManager.postDoneHandshakeMessage(message.getUavid(), (UAVHandshakeMessage) message);

		} else if (message instanceof UAVMonitoringMessage) {
			dispatchQueueManager.postMonitoringMessage((UAVMonitoringMessage) message);
		}
	}

	public void tearDonw() {
		cont.set(false);
	}

	public String getConnectionId() {
		return "ADS";
	}
}
