package edu.nd.dronology.core.start;
import java.util.ArrayList;
import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightPlan;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.physical_environment.BaseManager;
import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import edu.nd.dronology.core.zone_manager.ZoneBounds;
import helloworld.HelloWorld;
import javafx.application.Application;
import view.DefaultLocalView;


/**
 * Starts up the drone simulation
 * For eclipse:  Help / Add new Software / http://download.eclipse.org/efxclipse/updates-released/2.4.0/site
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class DronologyRunner{
		
	FlightZoneManager flightManager;
	BaseManager baseManager;
	Flights flights;
	static long xRange = 1600;
	static long yRange = 960;
			 	
	static int LeftDivider = 180;
//	ArrayList<FlightPlan> currentFlights;
//	ArrayList<FlightPlan> pendingFlights;
	ArrayList<FlightPlan> completedFlights;
	
	public static void main(String[] args) throws InterruptedException, FlightZoneException {		
		new DronologyRunner(args);
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
		 
		Application.launch(HelloWorld.class, args);
		startFlightManager();	
		flightManager.loadFlightFromXML();
	}
	
	/**
	 * Starts the flight manager.  Sets zone bounds for the simulation.
	 * @throws InterruptedException
	 * @throws FlightZoneException 
	 */
	public void startFlightManager() throws InterruptedException, FlightZoneException{
		ZoneBounds zoneBounds = ZoneBounds.getInstance();
	    //zoneBounds.setZoneBounds(42722381, -86290828, 41660473, -86140256, 100);
	    zoneBounds.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
		DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider);  //Setup happens only once.  Must happen after Zonebounds are set.
		constructBases(1);
		flightManager = new FlightZoneManager(this, baseManager);
		flights = flightManager.getFlights();
		flightManager.startThread();
	}
	
	public void constructBases(int baseCount) throws FlightZoneException{
		baseManager = new BaseManager(5);
	}
	
}	


