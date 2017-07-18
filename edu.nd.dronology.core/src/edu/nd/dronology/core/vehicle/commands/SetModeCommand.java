package edu.nd.dronology.core.vehicle.commands;

public class SetModeCommand extends AbstractDroneCommand implements IDroneCommand {

	public static final transient String MODE_LAND = "LAND";

	public SetModeCommand(String uavid, String mode) {
		super(uavid, CommandIds.SET_MODE_COMMAND);
		data.put(ATTRIBUTE_MODE, mode);
	}

}
