package edu.nd.dronology.core.vehicle.commands;

public class TakeoffCommand extends AbstractDroneCommand implements IDroneCommand {

	public TakeoffCommand(String droneId, double altitude) {
		super(droneId, CommandIds.TAKEOFF_COMMAND);
		innerdata.put("altitude", altitude);
	}


}
