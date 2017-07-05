package edu.nd.dronology.gstation.python.connector;

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
import com.google.gson.stream.MalformedJsonException;

import edu.nd.dronology.core.util.FormatUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ReadDispatcher implements Runnable {

	private InputStream inputStream;
	private AtomicBoolean cont = new AtomicBoolean(false);
	private static final ILogger LOGGER = LoggerProvider.getLogger(ReadDispatcher.class);

	static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setVersion(1.0)
			.serializeSpecialFloatingPointValues().create();
	private BufferedReader reader;

	public ReadDispatcher(Socket pythonSocket) {
		try {
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
			while (cont.get()) {
				String line = reader.readLine();
				if (line != null) {
					// TODO: create the timestamp before deserializing the object....
					try {
						UAVMessage message = GSON.fromJson(line, UAVMessage.class);
						message.timestamp();
						processMessage(message);
					} catch (Exception ex) {
						LOGGER.hwFatal("Error when parsing incomming message '" + line + "'");
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
		}
	}

	private void processMessage(UAVMessage message) {
		LOGGER.hwInfo(FormatUtil.formatTimestamp(message.getTimestamp(), FormatUtil.FORMAT_YEAR_FIRST_MILLIS) + " - "
				+ message.toString());

		for (UAVState state : message.getStats()) {
			DispatchQueueManager.getInstance().postDroneStatusUpdate(state.getId(), state);
		}
	}

	public void tearDonw() {
		cont.set(false);
	}
}
