package edu.nd.dronology.gstation.python.connector;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.commands.GoToCommand;
import edu.nd.dronology.core.vehicle.commands.SetModeCommand;
import edu.nd.dronology.core.vehicle.commands.TakeoffCommand;

public class JsonTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(new TakeoffCommand("1", 20).toJsonString());
		System.out.println(new SetModeCommand("1", "LAND").toJsonString());
		System.out.println(new GoToCommand("1", new Coordinate(41732957,-86180883,20)).toJsonString());
		System.out.println(new TakeoffCommand("1", 20).toJsonString());
		
		
	}

}
