package edu.nd.dronology.gstation.python.connector.dispatch;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Writer Thread takes items from the outbound queue and writes it to the socket.
 * 
 * @author Michael
 *
 */
public class WriteDispatcher implements Runnable {

	private OutputStream outputStream;
	private AtomicBoolean cont = new AtomicBoolean(true);
	private BlockingQueue<IDroneCommand> outputQueue;
	private static final ILogger LOGGER = LoggerProvider.getLogger(WriteDispatcher.class);

	public WriteDispatcher(Socket pythonSocket, BlockingQueue<IDroneCommand> outputQueue) {
		try {
			outputStream = pythonSocket.getOutputStream();
			this.outputQueue = outputQueue;
			cont.set(true);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void run() {
		LOGGER.info("Write-Dispatcher started");
		try {
		while (cont.get()) {
			try {
				IDroneCommand toSend = outputQueue.take();
				if (toSend instanceof MarkerObject) {
					continue;
				}
				LOGGER.hwInfo("Sending Command to UAV -" + toSend.toString());
				toSend.timestamp();
				outputStream.write(toSend.toJsonString().getBytes());
				outputStream.write(System.getProperty("line.separator").getBytes());
				outputStream.flush();
			} catch (IOException | InterruptedException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.info("Writer Thread shutdown");
		try {
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public void tearDown() {
		cont.set(false);
		try {
			outputQueue.put(new MarkerObject());
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	public class MarkerObject implements IDroneCommand {

		@Override
		public String toJsonString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void timestamp() {
			// TODO Auto-generated method stub

		}

	}

}
