package edu.nd.dronology.core.start;
import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.gui.JavaFXGUILauncher;
import edu.nd.dronology.core.home_bases.BaseManager;
import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import edu.nd.dronology.core.zone_manager.ZoneBounds;

/**
 * Starts up the Dronology System
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class DronologyRunner{
		
	FlightZoneManager flightManager;
	BaseManager baseManager;
	Flights flights;
		
	public static void main(String[] args) throws InterruptedException, FlightZoneException {		
		new DronologyRunner(args);
	}

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
		flightManager.loadFlightFromXML(); // Just for testing.
	}
	
	/**
	 * Starts the flight manager.  Sets zone bounds for the simulation.
	 * @throws InterruptedException
	 * @throws FlightZoneException 
	 */
	public void startFlightManager() throws InterruptedException, FlightZoneException{
		ZoneBounds zoneBounds = ZoneBounds.getInstance();
	    zoneBounds.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
		constructBases(5);
		flightManager = new FlightZoneManager(this, baseManager);
		flights = flightManager.getFlights();
		flightManager.startThread();
	}
	
	public void constructBases(int baseCount) throws FlightZoneException{
		baseManager = new BaseManager(5);
	}
	
}	


