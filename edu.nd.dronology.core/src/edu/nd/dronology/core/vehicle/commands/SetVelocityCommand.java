package edu.nd.dronology.core.vehicle.commands;

public class SetVelocityCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetVelocityCommand(String droneId, double x, double y, double z) {
		super(droneId, CommandIds.SET_VELOCITY_COMMAND);
		innerdata.put("x", x);
		innerdata.put("y", y);
		innerdata.put("z", z);
	}


	public double coordLongToFloat(long coord) {
		double floatScaled = coord;
		return floatScaled / 1000000.0;
	}
}
