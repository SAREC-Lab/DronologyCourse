package edu.nd.dronology.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Creates threads based on the given name.
 * <p/>
 * Useful to name threads created with the {@link Executors} class.
 */
public class NamedThreadFactory implements ThreadFactory {

	private final String baseName;
	private int runningNr = 0;

	/**
	 * 
	 * @param baseName
	 *          The common string share by all thread names created with this factory.
	 */
	public NamedThreadFactory(final String baseName) {
		NullUtil.checkNull(baseName);
		this.baseName = baseName;
	}

	@Override
	public synchronized Thread newThread(Runnable runnable) {
		NullUtil.checkNull(runnable);
		return new Thread(runnable, baseName + "-" + runningNr++);
	}

}
