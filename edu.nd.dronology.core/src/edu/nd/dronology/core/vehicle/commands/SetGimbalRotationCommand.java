package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.util.LlaCoordinate;

@CoordinateChange
public class SetGimbalRotationCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetGimbalRotationCommand(String uavid, LlaCoordinate coord) {
		super(uavid, "");
	}

}
