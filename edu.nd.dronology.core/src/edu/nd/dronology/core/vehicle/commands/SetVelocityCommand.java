package edu.nd.dronology.core.vehicle.commands;

public class SetVelocityCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetVelocityCommand(String uavid, double x, double y, double z) {
		super(uavid, CommandIds.SET_VELOCITY_COMMAND);
		data.put(ATTRIBUTE_X, x);
		data.put(ATTRIBUTE_Y, y);
		data.put(ATTRIBUTE_Z, z);
	}

}
