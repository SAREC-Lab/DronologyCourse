package edu.nd.dronology.gstation.python.connector;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.util.PreciseTimestamp;

public class UAVMessage implements Serializable {

	static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setVersion(1.0)
			.serializeSpecialFloatingPointValues().create();

	private transient PreciseTimestamp timestamp;

	String type;
	private final Map<String, UAVState> data = new HashMap<>();

	public UAVMessage(String message) {
		this.timestamp = PreciseTimestamp.create();
	}

	public PreciseTimestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return GSON.toJson(this);
	}

	public Object readResolve() {
		this.timestamp = PreciseTimestamp.create();
		return this;
	}

	public void timestamp() {
		this.timestamp = PreciseTimestamp.create();

	}

	public Collection<UAVState> getStats() {
		return data.values();
	}

}
