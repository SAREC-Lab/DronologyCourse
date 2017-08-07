package edu.nd.dronology.gstation.python.connector.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.nd.dronology.core.util.PreciseTimestamp;

public class AbstractUAVMessage<T> implements Serializable {

	private static final long serialVersionUID = 8470856533906132618L;

	public AbstractUAVMessage(String type, String uavid) {
		this.type = type;
		this.uavid = uavid;
		this.receiveTimestamp = PreciseTimestamp.create();
	}

	private transient PreciseTimestamp receiveTimestamp;
	private long sendtimestamp;

	protected String type;
	protected final Map<String, T> data = new HashMap<>();
	protected String uavid;


	public void timestamp() {
		this.receiveTimestamp = PreciseTimestamp.create();

	}

	public PreciseTimestamp getTimestamp() {
		return receiveTimestamp;
	}

	public long getSendtimestamp() {
		return sendtimestamp;
	}

	@Override
	public String toString() {
		return UAVMessageFactory.GSON.toJson(this);
	}

	public String getUavid() {
		return uavid;
	}

	public void setUavid(String uavid) {
		this.uavid = uavid;
	}

	public void addPropery(String key, T value) {
		data.put(key, value);

	}

	public T getProperty(String key) {
		return data.get(key);

	}
	
	public Set<Entry<String, T>> getProperties() {
		return data.entrySet();
	}
	

}
