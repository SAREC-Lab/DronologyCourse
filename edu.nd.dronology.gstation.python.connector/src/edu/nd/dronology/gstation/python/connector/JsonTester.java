package edu.nd.dronology.gstation.python.connector;

import edu.nd.dronology.core.vehicle.commands.TakeoffCommand;

public class JsonTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(new TakeoffCommand("drone1", 20).toJsonString());
		
		
	}

}
