package edu.nd.dronology.services.launcher;

import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.flightpath.FlightPathService;
import edu.nd.dronology.services.remote.RemoteService;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DronologyServiceRunner {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronologyServiceRunner.class);

	public static void main(String[] args) {
		try {
			RemoteService.getInstance().startService();
			FlightPathService.getInstance().startService();

		} catch (DronologyServiceException e) {
			LOGGER.error(e);
		}

	}

}
