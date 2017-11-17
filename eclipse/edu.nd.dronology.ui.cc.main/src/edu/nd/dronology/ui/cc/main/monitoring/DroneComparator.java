package edu.nd.dronology.ui.cc.main.monitoring;

import java.util.Comparator;

import edu.nd.dronology.core.status.DroneStatus;

public class DroneComparator implements Comparator<DroneStatus> {

	@Override
	public int compare(DroneStatus arg0, DroneStatus arg1) {
		return arg0.getID().compareTo(arg1.getID());
	}

}
