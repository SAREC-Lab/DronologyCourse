package edu.nd.dronology.core.vehicle;

public class DroneAttribute<T> implements IDroneAttribute<T> {

	private final String key;
	private final T value;

	public DroneAttribute(String key, T value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}
	@Override
	public T getValue() {
		return value;
	}

}
