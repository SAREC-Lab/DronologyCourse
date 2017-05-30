package edu.nd.dronology.ui.javafx.start;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.services.launcher.DronologyServiceRunner;

public class DronologyFullRunner {

	public static void main(String[] args) {

		try {
			DronologyServiceRunner.main(args);
			DronologyFXUIRunner.main(args);
		} catch (InterruptedException | FlightZoneException e) {
			e.printStackTrace();
		}

	}

}
