package edu.nd.dronology.core.vehicle.commands;

public class SetGroundSpeedCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetGroundSpeedCommand(String droneId, double speed) {
		super(droneId, CommandIds.SET_GROUND_SPEED_COMMAND);
		innerdata.put("speed", speed);
	}
}
