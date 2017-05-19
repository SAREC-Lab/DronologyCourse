package edu.nd.dronology.core.vehicle.commands;

public class SetModeCommand extends AbstractDroneCommand implements IDroneCommand {

	public static final transient String MODE_LAND ="LAND";
	
	
	public SetModeCommand(String droneId, String mode) {
		super(droneId, CommandIds.SET_MODE_COMMAND);
		innerdata.put("mode", mode);
	}

}
