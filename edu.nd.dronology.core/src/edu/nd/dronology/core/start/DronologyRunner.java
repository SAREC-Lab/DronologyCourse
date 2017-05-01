package edu.nd.dronology.core.start;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.gui.JavaFXGUILauncher;
import edu.nd.dronology.core.gui_middleware.DronologySetupDronesAccessPoint;
import edu.nd.dronology.core.gui_middleware.load_flights.LoadXMLFlight;
import edu.nd.dronology.core.gui_middleware.load_flights.WayPointCollection;
import edu.nd.dronology.core.utilities.Coordinates;
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
		// Three external GUIs are modeled here.
		// Dummy methods are listed below to support these operations.
		// Those dummy operations should be provided by the GUIs
		//==================================================================================================================
		
		// 1. DISPLAY DRONES AS THEY MOVE
		startLocalGUIs(args);  // Currently just starts a very basic GUI for displaying flying UAVS.
		
		// 2. Initialize DRONES on the ground!
		guiProxy1Initialize();  // Loads 
		//testLoad2b(); // testLoad2b and 2a split testLoad into two parts.
		//testLoad2a();
		
		// 3. Load one or more flight plans
		// GUI should send an ArrayList<Coordinates>
		// This example shows three flight plans loading.
		// We need a middleware class that listens for a GUI to submit a new flight -- and then makes a call onto FlightManager
		// to load it.  (Here I assume I have THE single flightManager instance.
		ArrayList<Coordinates> flight = guiProxyLoadOneFlight();
		flightManager.planFlight(flight.get(0),flight);
		flight = guiProxyLoadOneFlight2();
		flightManager.planFlight(flight.get(0),flight);
		flight = guiProxyLoadOneFlight3();
		flightManager.planFlight(flight.get(0),flight);
		
		
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
	
	
	// Test load a flight
	public ArrayList<Coordinates> guiProxyLoadOneFlight(){
		ArrayList<Coordinates> wayPoints = new ArrayList<>();
		wayPoints.add(new Coordinates(42270485,-86200000,10));
		wayPoints.add(new Coordinates(42500000,-86150000,10));	
		return wayPoints;
	}
	
	// Test load a flight
		public ArrayList<Coordinates> guiProxyLoadOneFlight2(){
			ArrayList<Coordinates> wayPoints = new ArrayList<>();
			wayPoints.add(new Coordinates(41750881,-86180000,10));
			wayPoints.add(new Coordinates(42700000,-86200000,10));	
			wayPoints.add(new Coordinates(42400000,-86165000,10));	
			return wayPoints;
		}
		
		public ArrayList<Coordinates> guiProxyLoadOneFlight3(){
			ArrayList<Coordinates> wayPoints = new ArrayList<>();
			wayPoints.add(new Coordinates(41730893,-86172481,10));
			wayPoints.add(new Coordinates(41731316,-86242201,15));
			wayPoints.add(new Coordinates(41730893,-86172587,20));
			return wayPoints;
		}
	
		
	// Test loads drones
	public void guiProxy1Initialize(){
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
	
	public void guiProxy1aInitialize(){
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D1 = {"DRN1","Iris3DR","41760000","-86222901","0"};
		String[] D2 = {"DRN2","Iris3DR","41750802","-86202481","10"}; 
		newDrones.add(D1);
		newDrones.add(D2);
		DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		setup.initializeDrones(newDrones,false);
	
	}
	
	public void guiProxy1bInitialize(){
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D3 = {"DRN3","Iris3DR","41740893","-86182505","0"};
		newDrones.add(D3);
		DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		setup.initializeDrones(newDrones,false);
	
	}
	
}	


