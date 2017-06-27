package edu.nd.dronology.ui.javafx.start;

import java.util.ArrayList;

import edu.nd.dronology.core.CoordinateChange;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.ui.javafx.BaseServiceProvider;
import edu.nd.dronology.ui.javafx.JavaFXGUILauncher;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Starts up the Dronology System
 * 
 * @author Jane Cleland-Huang
 * @version 0.11
 *
 */
public class DronologyFXUIRunner {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronologyFXUIRunner.class);

	private IFlightManagerRemoteService flightManagerService;
	private IDroneSetupRemoteService setupService;

	public static BaseServiceProvider provider = new BaseServiceProvider();

	public static void main(String[] args) throws InterruptedException, FlightZoneException {
		new DronologyFXUIRunner(args); // Main start up routine!
	}

	/**
	 * Initial setup included setting simulation type
	 * 
	 * @param args
	 * @throws FlightZoneException
	 * @throws InterruptedException
	 */
	
	@CoordinateChange
	public DronologyFXUIRunner(String[] args) throws FlightZoneException, InterruptedException {

		LOGGER.info("Using '" + provider.getClass() + "' as service provider");
		provider.init("localhost", 9898);

		// 1. DISPLAY DRONES AS THEY MOVE
		startLocalGUIs(args); // Currently just starts a very basic GUI for displaying flying UAVS.

		// 2. Initialize DRONES on the ground!
		if (args.length == 0) {
			System.out.println("INIT!");
			guiProxy1Initialize(); // Loads
		}
		// testLoad2b(); // testLoad2b and 2a split testLoad into two parts.
		// testLoad2a();

		// 3. Load one or more flight plans
		// GUI should send an ArrayList<Coordinates>
		// This example shows three flight plans loading.
		// We need a middleware class that listens for a GUI to submit a new flight -- and then makes a call onto FlightManager
		// to load it. (Here I assume I have THE single flightManager instance.
		ArrayList<LlaCoordinate> flight = guiProxyLoadOneFlight();

		IFlightRouteplanningRemoteService service;

		try {
			if (args.length == 0) {
				flightManagerService = (IFlightManagerRemoteService) provider.getRemoteManager()
						.getService(IFlightManagerRemoteService.class);

				flightManagerService.planFlight("p1",flight.get(0), flight);
				flight = guiProxyLoadOneFlight2();
				flightManagerService.planFlight("p2",flight.get(0), flight);
				flight = guiProxyLoadOneFlight3();
				flightManagerService.planFlight("p3",flight.get(0), flight);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}

		LOGGER.info("Running Dronology Test 2.1.");
	}

	// To run a non-blocking version of JavaFX from internally you need to create a thread and launch the JavaFX thread from
	// within that thread!!!
	public void startLocalGUIs(String[] args) {
		(new Thread(new JavaFXGUILauncher(args))).start();
	}

	// Test load a flight
	public ArrayList<LlaCoordinate> guiProxyLoadOneFlight() {
		ArrayList<LlaCoordinate> wayPoints = new ArrayList<>();
		wayPoints.add(new LlaCoordinate(42.270485, -86.200000, 10));
		wayPoints.add(new LlaCoordinate(42.500000, -86.150000, 10));
		return wayPoints;
	}

	// Test load a flight
	public ArrayList<LlaCoordinate> guiProxyLoadOneFlight2() {
		ArrayList<LlaCoordinate> wayPoints = new ArrayList<>();
		wayPoints.add(new LlaCoordinate(41.750881, -86.180000, 10));
		wayPoints.add(new LlaCoordinate(42.700000, -86.200000, 10));
		wayPoints.add(new LlaCoordinate(42.400000, -86.165000, 10));
		return wayPoints;
	}

	public ArrayList<LlaCoordinate> guiProxyLoadOneFlight3() {
		ArrayList<LlaCoordinate> wayPoints = new ArrayList<>();
		wayPoints.add(new LlaCoordinate(41.730893, -86.172481, 10));
		wayPoints.add(new LlaCoordinate(41.731316, -86.242201, 15));
		wayPoints.add(new LlaCoordinate(41.730893, -86.172587, 20));
		return wayPoints;
	}

	// Test loads drones
	public void guiProxy1Initialize() {
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D1 = { "DRN1", "Iris3DR", "41.760000", "-86.222901", "0" };
		String[] D2 = { "DRN2", "Iris3DR", "41.750802", "-86.202481", "10" };
		String[] D3 = { "DRN3", "Iris3DR", "41.740893", "-86.182505", "0" };
		newDrones.add(D1);
		newDrones.add(D2);
		newDrones.add(D3);

		try {
			setupService = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);

			setupService.initializeDrones(newDrones, false);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		// DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		// setup.initializeDrones(newDrones, false);

	}

	public void guiProxy1aInitialize() {
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] drone1 = { "DRN1", "Iris3DR", "41760000", "-86222901", "0" };
		String[] drone2 = { "DRN2", "Iris3DR", "41750802", "-86202481", "10" };
		newDrones.add(drone1);
		newDrones.add(drone2);

		try {
			setupService = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);

			setupService.initializeDrones(newDrones, false);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		// DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();

	}

	public void guiProxy1bInitialize() {
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D3 = { "DRN3", "Iris3DR", "41740893", "-86182505", "0" };
		newDrones.add(D3);
		// DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		// setup.initializeDrones(newDrones, false);

	}

}
