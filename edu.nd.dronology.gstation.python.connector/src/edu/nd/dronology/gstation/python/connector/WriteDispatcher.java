package edu.nd.dronology.gstation.python.connector;

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
	private AtomicBoolean cont = new AtomicBoolean(false);
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
		while (cont.get()) {
			try {
				IDroneCommand toSend = outputQueue.take();

				LOGGER.hwInfo("Sending Command to UAV -" + toSend.toString());
				outputStream.write(toSend.toJsonString().getBytes());
				outputStream.write(System.lineSeparator().getBytes());
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
	}

	public void tearDown() {
		cont.set(false);
	}

}
