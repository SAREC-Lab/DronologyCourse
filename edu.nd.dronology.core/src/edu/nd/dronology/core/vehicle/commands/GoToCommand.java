package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.util.LlaCoordinate;
@CoordinateChange
public class GoToCommand extends AbstractDroneCommand implements IDroneCommand {

	public GoToCommand(String droneId, LlaCoordinate coord) {
		super(droneId, CommandIds.GOTO_LOCATION_COMMAND);
		innerdata.put("x", coord.getLatitude());
		innerdata.put("y", coord.getLongitude());
		innerdata.put("z", coord.getAltitude());
	}


	public double coordLongToFloat(long coord) {
		double floatScaled = coord;
		return floatScaled / 1000000.0;
	}

}
