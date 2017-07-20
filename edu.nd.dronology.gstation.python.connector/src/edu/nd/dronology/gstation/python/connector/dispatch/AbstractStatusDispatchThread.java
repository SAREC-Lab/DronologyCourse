package edu.nd.dronology.gstation.python.connector.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbstractStatusDispatchThread<MESSAGE_TYPE> {

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
