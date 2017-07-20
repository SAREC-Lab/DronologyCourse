package edu.nd.dronology.core.vehicle.commands;

public class SetMonitoringFrequencyCommand extends AbstractDroneCommand implements IDroneCommand {

	public SetMonitoringFrequencyCommand(String droneId, long frequency) {
		super(droneId, CommandIds.SET_MONITOR_FREQUENCY_COMMAND);
		data.put(ATTRIBUTE_FREQUENCY, frequency);
	}

}
