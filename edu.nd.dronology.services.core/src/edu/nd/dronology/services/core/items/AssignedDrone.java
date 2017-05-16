package edu.nd.dronology.services.core.items;

import edu.nd.dronology.core.util.Coordinates;

public class AssignedDrone {

	public final String droneName;
	public Coordinates startCoordinate = new Coordinates(0, 0, 0);

	public AssignedDrone(String droneName) {
		super();
		this.droneName = droneName;
	}

	public Coordinates getStartCoordinate() {
		return startCoordinate;
	}

	public void setStartCoordinate(long latitude, long longitude, int altitude) {
		startCoordinate.setLatitude(latitude);
		startCoordinate.setLongitude(longitude);
		startCoordinate.setAltitude(altitude);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((droneName == null) ? 0 : droneName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssignedDrone other = (AssignedDrone) obj;
		if (droneName == null) {
			if (other.droneName != null)
				return false;
		} else if (!droneName.equals(other.droneName))
			return false;
		return true;
	}

	public String getName() {
		return droneName;
	}

	
	
}
