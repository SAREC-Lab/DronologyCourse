package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.util.Coordinate;

public class GoToCommand extends AbstractDroneCommand implements IDroneCommand {

	public GoToCommand(String droneId, Coordinate coord) {
		super(droneId, CommandIds.GOTO_LOCATION_COMMAND);
		innerdata.put("x", coordLongToFloat(coord.getLatitude()));
		innerdata.put("y", coordLongToFloat(coord.getLongitude()));
		innerdata.put("z", coord.getAltitude());
	}


	public double coordLongToFloat(long coord) {
		double floatScaled = coord;
		return floatScaled / 1000000.0;
	}

}
