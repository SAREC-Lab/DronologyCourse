package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.coordinate.LlaCoordinate;

@CoordinateChange
public class SetGimbalTargetCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetGimbalTargetCommand(String uavid, LlaCoordinate coord) {
		super(uavid, "");

	}

}
