package edu.nd.dronology.core.vehicle.commands;

public class SetGroundSpeedCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetGroundSpeedCommand(String uavid, double speed) {
		super(uavid, CommandIds.SET_GROUND_SPEED_COMMAND);
		data.put(ATTRIBUTE_SPEED, speed);
	}
}
