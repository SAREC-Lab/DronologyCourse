package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class StatusDispatchThread extends AbstractStatusDispatchThread<UAVStateMessage> implements Callable {
	private static final ILogger LOGGER = LoggerProvider.getLogger(StatusDispatchThread.class);

	private IUAVPropertyUpdateNotifier listener;

	public StatusDispatchThread(final BlockingQueue<UAVStateMessage> queue, IUAVPropertyUpdateNotifier listener) {
		super(queue);
		this.listener = listener;
	}

	protected void notifyListener() throws Exception {

	}

	@Override
	public Object call() {
		while (cont.get()) {

			try {
				UAVStateMessage state = queue.take();
				listener.updateCoordinates(state.getLocation());
				listener.updateVelocity(state.getGroundspeed());
				listener.updateBatteryLevel(state.getBatterystatus().getBatteryVoltage());
			} catch (InterruptedException e) {
				LOGGER.warn("Status Dispatch Thread terminated");
			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
		LOGGER.info("Dispatcher shutdown!");
		return null;
	}

}
