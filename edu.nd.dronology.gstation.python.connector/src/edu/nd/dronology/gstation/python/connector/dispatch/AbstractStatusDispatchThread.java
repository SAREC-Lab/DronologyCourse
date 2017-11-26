package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractStatusDispatchThread<MESSAGE_TYPE> implements Callable {

	protected AtomicBoolean cont = new AtomicBoolean(true);
	protected BlockingQueue<MESSAGE_TYPE> queue;
	
	
	public AbstractStatusDispatchThread(BlockingQueue<MESSAGE_TYPE> queue) {
		this.queue = queue;
	}

	int getQueueSize() {
		return queue.size();
	}

	public void tearDown() {
		cont.set(false);
	}

	
	
}
