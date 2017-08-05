package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;

@CoordinateChange
public class PingPongCommand extends AbstractDroneCommand implements IDroneCommand {

	public PingPongCommand(String uavid) {
		super(uavid, CommandIds.PING_PONG_COMMAND);
	}

}
