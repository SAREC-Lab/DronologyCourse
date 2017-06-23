package edu.nd.dronology.gstation.python.connector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.nd.dronology.core.IDroneStatusUpdateListener;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class StatusDispatchThread implements Callable {
	private static final ILogger LOGGER = LoggerProvider.getLogger(StatusDispatchThread.class);
	private BlockingQueue<UAVState> queue;
	private AtomicBoolean cont = new AtomicBoolean(true);
	private IDroneStatusUpdateListener listener;

	public StatusDispatchThread(final BlockingQueue<UAVState> queue, IDroneStatusUpdateListener listener) {
		this.queue = queue;
		this.listener = listener;
	}

	protected void notifyListener() throws Exception {

	}

	@Override
	public Object call() {
		while (cont.get()) {

			try {
				UAVState state = queue.take();
				listener.updateCoordinates(state.getLocation());
				listener.updateVelocity(state.getGroundspeed());
			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
		LOGGER.info("Dispatcher shutdown!");
		return null;
	}

	int getQueueSize() {
		return queue.size();
	}

	public void tearDown() {
		cont.set(false);
	}

}
