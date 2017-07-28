package edu.nd.dronology.monitoring.validation;

public class MonitoredEvent<T> {

	public long timestamp;
	public T value;

	public MonitoredEvent(long timestamp, T value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public static <T> MonitoredEvent create(long timestamp, T value) {
		return new MonitoredEvent(timestamp, value);
	}

}
