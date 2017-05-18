package edu.nd.dronology.ui.cc.main.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Simple GUI Refresh Thread that performs a cyclic refresh.
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class UIRefreshThread {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UIRefreshThread.class);

	private AtomicBoolean cont = new AtomicBoolean();
	private long timeoutTime;
	private ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("UI-RefreshThread"));

	/**
	 * 
	 * @param timeoutTime
	 *          - The time between two refresh calls in seconds
	 */

	public UIRefreshThread(int timeoutTime) {
		super();
		this.timeoutTime = (1000 * timeoutTime);
		cont.set(true);
	}
	
	public UIRefreshThread(double timeoutTime) {
		super();
		this.timeoutTime = new Double(1000 * timeoutTime).longValue();
		cont.set(true);
	}

	private class RefreshRunnable implements Runnable {
		@Override
		public void run() {
			while (cont.get()) {
				try {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							doRefresh();
						}
					});
					Thread.sleep(timeoutTime);
				} catch (Exception e) {
					LOGGER.error("Error when performing UI refesh", e);
				}
			}
		}
	}

	protected abstract void doRefresh();

	public void terminate() {
		cont.set(false);
	}

	public void start() {
		executor.submit(new RefreshRunnable());
	}
}
