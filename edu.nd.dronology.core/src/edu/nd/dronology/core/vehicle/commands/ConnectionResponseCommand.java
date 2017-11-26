package edu.nd.dronology.core.vehicle.commands;

public class ConnectionResponseCommand extends AbstractDroneCommand implements IDroneCommand {

	public ConnectionResponseCommand(String groundstationId, boolean success) {
		super(groundstationId, CommandIds.CONNECTION_RESPONSE);
		data.put(ATTRIBUTE_SUCCESS, Boolean.toString(success));
	}

}
