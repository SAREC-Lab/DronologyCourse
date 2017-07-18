package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import edu.nd.dronology.core.IDroneStatusUpdateListener;
import edu.nd.dronology.gstation.python.connector.IMonitoringMessageHandler;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringDispatchThread extends  AbstractStatusDispatchThread<UAVMonitoringMessage> implements Callable {
	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDispatchThread.class);

	private IDroneStatusUpdateListener listener;
	private List<IMonitoringMessageHandler> handlers;

	public MonitoringDispatchThread(final BlockingQueue<UAVMonitoringMessage> queue,
			List<IMonitoringMessageHandler> handlers) {
		super(queue);
		this.handlers = handlers;
	}

	@Override
	public Object call() {
		while (cont.get()) {

			try {
				UAVMonitoringMessage message = queue.take();

				for (IMonitoringMessageHandler handler : handlers) {
					handler.notify(message);
				}

			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
		LOGGER.info("Dispatcher shutdown!");
		return null;
	}



}
