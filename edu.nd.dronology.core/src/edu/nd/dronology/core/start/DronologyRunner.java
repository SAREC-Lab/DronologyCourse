package edu.nd.dronology.core.start;
import java.util.ArrayList;

import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.gui.JavaFXGUILauncher;
import edu.nd.dronology.core.gui_middleware.DronologySetupDronesAccessPoint;
import edu.nd.dronology.core.gui_middleware.load_flights.LoadXMLFlight;
import edu.nd.dronology.core.zone_manager.FlightZoneException;

/**
 * Starts up the Dronology System
 * @author Jane Cleland-Huang
 * @version 0.11
 *
 */
public class DronologyRunner{
		
	private FlightZoneManager flightManager;
	
	public static void main(String[] args) throws InterruptedException, FlightZoneException {		
		new DronologyRunner(args); // Main start up routine!
	}
	
	/**
	 * Initial setup included setting simulation type
	 * @param args
	 * @throws FlightZoneException 
	 * @throws InterruptedException 
	 */
	public DronologyRunner(String[] args) throws FlightZoneException, InterruptedException{		
		RuntimeDroneTypes runtimeMode = RuntimeDroneTypes.getInstance();
		try {
			runtimeMode.setVirtualEnvironment();
		} catch (FlightZoneException e) {
			e.printStackTrace();
		}		
		 		
		startFlightManager();	
				
		//==================================================================================================================
		// ALL THIS GETS MOVED TO EXTERNAL CLIENTS
		//==================================================================================================================
		startLocalGUIs(args);  // Currently just starts a very basic GUI
		testLoad();  // Loads 
		//testLoad2b(); // testLoad2b and 2a split testLoad into two parts.
		//testLoad2a();
		flightManager.loadFlightFromXML(); // Just for testing.
		//new LoadXMLFlight();
		System.out.println("Running Dronology Test 2.1.");
	}
	
	/**
	 * Starts the flight manager.  Sets zone bounds for the simulation.
	 * @throws InterruptedException
	 * @throws FlightZoneException 
	 */
	public void startFlightManager() throws InterruptedException, FlightZoneException{
		flightManager = new FlightZoneManager();
		flightManager.getFlights();
		flightManager.startThread();
	}
		
	//=========================================================================================================================
	// All remaining functions can get removed if we don't want to start anything locally
	// We might want an option at start to just run a demo with some defaults
	//=========================================================================================================================
	
	//  To run a non-blocking version of JavaFX from internally you need to create a thread and launch the JavaFX thread from
	// within that thread!!!
	public void startLocalGUIs(String[] args){
		(new Thread(new JavaFXGUILauncher(args))).start();
	}
		
	// Test loads drones
	public void testLoad(){
			ArrayList<String[]> newDrones = new ArrayList<>();
			String[] D1 = {"DRN1","Iris3DR","41760000","-86222901","0"};
			String[] D2 = {"DRN2","Iris3DR","41750802","-86202481","10"}; 
			String[] D3 = {"DRN3","Iris3DR","41740893","-86182505","0"};
			newDrones.add(D1);
			newDrones.add(D2);
			newDrones.add(D3);
			DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
			setup.initializeDrones(newDrones,false);
		
	}
	
	public void testLoad2a(){
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D1 = {"DRN1","Iris3DR","41760000","-86222901","0"};
		String[] D2 = {"DRN2","Iris3DR","41750802","-86202481","10"}; 
		newDrones.add(D1);
		newDrones.add(D2);
		DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		setup.initializeDrones(newDrones,false);
	
	}
	
	public void testLoad2b(){
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D3 = {"DRN3","Iris3DR","41740893","-86182505","0"};
		newDrones.add(D3);
		DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		setup.initializeDrones(newDrones,false);
	
	}
	
}	


