package edu.nd.dronology.core.start;
import java.util.ArrayList;

import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.gui.JavaFXGUILauncher;
import edu.nd.dronology.core.gui_middleware.DronologySetupDronesAccessPoint;
import edu.nd.dronology.core.zone_manager.FlightZoneException;

/**
 * Starts up the Dronology System
 * @author Jane Cleland-Huang
 * @version 0.11
 *
 */
public class DronologyRunner{
		
	FlightZoneManager flightManager;

	Flights flights;
		
	public static void main(String[] args) throws InterruptedException, FlightZoneException {		
		new DronologyRunner(args);
	}

	//  This will get launched by the external client
	public void startLocalGUIs(String[] args){
		(new Thread(new JavaFXGUILauncher(args))).start();
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
		 
		startLocalGUIs(args);
		startFlightManager();	
				
		///  The following statements are temporary.  The functionality should be provided by external clients.
		testLoad();
		flightManager.loadFlightFromXML(); // Just for testing.
		System.out.println("Running Dronology Test 2.1.");
	}
	
	/**
	 * Starts the flight manager.  Sets zone bounds for the simulation.
	 * @throws InterruptedException
	 * @throws FlightZoneException 
	 */
	public void startFlightManager() throws InterruptedException, FlightZoneException{
		flightManager = new FlightZoneManager();
		flights = flightManager.getFlights();
		flightManager.startThread();
	}
	
	
	public void testLoad(){
			ArrayList<String[]> newDrones = new ArrayList<String[]>();
			String[] D1 = {"DRN1","Iris3DR","41760000","-86222901","0"};
			String[] D2 = {"DRN2","Iris3DR","41750802","-86202481","10"}; 
			String[] D3 = {"DRN3","Iris3DR","41740893","-86182505","0"};
			newDrones.add(D1);
			newDrones.add(D2);
			newDrones.add(D3);
			DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
			setup.initializeDrones(newDrones,false);
		
	}
	
}	


