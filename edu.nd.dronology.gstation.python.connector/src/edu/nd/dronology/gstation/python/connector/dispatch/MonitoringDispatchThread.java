package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.gstation.python.connector.IMonitoringMessageHandler;
import edu.nd.dronology.gstation.python.connector.messages.AbstractUAVMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringDispatchThread extends AbstractStatusDispatchThread<AbstractUAVMessage> implements Callable {
	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDispatchThread.class);

	private IUAVPropertyUpdateNotifier listener;
	private List<IMonitoringMessageHandler> handlers;

	public MonitoringDispatchThread(final BlockingQueue<AbstractUAVMessage> queue,
			List<IMonitoringMessageHandler> handlers) {
		super(queue);
		this.handlers = handlers;
	}

	@Override
	public Object call() {
		while (cont.get() && !Thread.currentThread().isInterrupted()) {

			try {
				AbstractUAVMessage message = queue.take();

				for (IMonitoringMessageHandler handler : handlers) {
					if (message instanceof UAVMonitoringMessage) {
						handler.notifyMonitoringMessage((UAVMonitoringMessage) message);
					} else if (message instanceof UAVStateMessage) {
						handler.notifyStatusMessage((UAVStateMessage) message);
					} else {
						LOGGER.error("Unhandled message type: '" + message.getClass() + "'");
					}

				}

			} catch (InterruptedException e) {
				LOGGER.info("Monitoring Dispatcher shutdown! -- " + e.getMessage());

			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
		LOGGER.info("Monitoring Dispatcher shutdown!");
		return null;
	}

}
