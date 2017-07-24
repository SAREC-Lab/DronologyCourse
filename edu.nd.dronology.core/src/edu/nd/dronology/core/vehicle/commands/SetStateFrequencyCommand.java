package edu.nd.dronology.core.vehicle.commands;

public class SetStateFrequencyCommand extends AbstractDroneCommand implements IDroneCommand {

	protected SetStateFrequencyCommand(String droneId, long frequency) {
		super(droneId, CommandIds.SET_STATE_FREQUENCY_COMMAND);
		data.put(ATTRIBUTE_FREQUENCY, frequency);
	}

}
