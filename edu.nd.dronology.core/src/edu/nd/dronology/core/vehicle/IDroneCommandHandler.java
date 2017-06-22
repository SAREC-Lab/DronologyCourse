package edu.nd.dronology.core.vehicle;

import edu.nd.dronology.core.IDroneStatusUpdateListener;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;

public interface IDroneCommandHandler {
	
	
	public void sendCommand(IDroneCommand command) throws DroneException;

	void setStatusCallbackListener(String id, IDroneStatusUpdateListener listener) throws DroneException;

}
