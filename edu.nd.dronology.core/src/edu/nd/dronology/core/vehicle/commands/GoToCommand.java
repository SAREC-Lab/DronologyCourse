package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.util.LlaCoordinate;

@CoordinateChange
public class GoToCommand extends AbstractDroneCommand implements IDroneCommand {

	public GoToCommand(String uavid, LlaCoordinate coord) {
		super(uavid, CommandIds.GOTO_LOCATION_COMMAND);
		data.put(ATTRIBUTE_X, coord.getLatitude());
		data.put(ATTRIBUTE_Y, coord.getLongitude());
		data.put(ATTRIBUTE_Z, coord.getAltitude());
	}

}
