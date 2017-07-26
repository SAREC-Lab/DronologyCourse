package edu.nd.dronology.core.util;

import java.io.Serializable;

public class PreciseTimestamp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4565636114409035692L;
	private long nanotime;
	private long timestamp;
	private boolean isPrecise;
	static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

	public PreciseTimestamp(long timestamp, boolean isPrecise) {
		this.timestamp = timestamp;
		this.isPrecise = isPrecise;
		this.nanotime = System.nanoTime();
	}

	public long getNanotime() {
		return nanotime;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int compareTo(PreciseTimestamp that) {
		int result = Long.compare(this.getTimestamp(), that.getTimestamp());
		if (result == 0) {
			return compareTimeStamps(this.nanotime, that.nanotime);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (nanotime ^ (nanotime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PreciseTimestamp other = ((PreciseTimestamp) obj);
		if (timestamp != other.timestamp) {
			return false;
		}
		if (nanotime != other.nanotime) {
			return false;
		}
		return true;
	}

	private  int compareTimeStamps(long start, long comp) {
		if (start == comp) {
			return 0;
		}
		if (comp - start > 0) {
			return -1;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "Timestamp: " + timestamp + " nanotime:" + nanotime;
	}

	public static synchronized PreciseTimestamp create(long timestamp) {
		return new PreciseTimestamp(timestamp, true);
	}

	public static synchronized PreciseTimestamp create(long timestamp, boolean isPrecise) {
		return new PreciseTimestamp(timestamp, isPrecise);
	}

	public static synchronized PreciseTimestamp create() {
		return new PreciseTimestamp(System.currentTimeMillis(), true);
	}
}