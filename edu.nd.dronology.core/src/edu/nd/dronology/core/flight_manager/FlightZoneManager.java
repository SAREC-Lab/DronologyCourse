package edu.nd.dronology.core.flight_manager;

import java.util.ArrayList;

import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.fleet_manager.DroneFleet;
import edu.nd.dronology.core.gui_middleware.load_flights.LoadXMLFlight;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.FlightZoneException;

public class FlightZoneManager implements Runnable{
	
	private Flights flights;	
    private DroneSeparationMonitor safetyMgr;
    private DroneFleet droneFleet;

	/**
	 * Constructs a new FlightZoneManager.
	 * @throws InterruptedException
	 */
	public FlightZoneManager() throws InterruptedException{
		droneFleet = DroneFleet.getInstance();
		safetyMgr = new DroneSeparationMonitor();
		flights = new Flights(safetyMgr);
	}
	
	public DroneFleet getDroneFleet(){
		return droneFleet;  // replace with iterator later.
	}
	
	/**
	 * Runs on an independent thread
	 */
	public void startThread(){
		(new Thread(this)).start();	
	}
	
	/**
	 * Creates a new flight plan
	 * @param start
	 * @param wayPoints
	 */
	public void planFlight(Coordinates start, ArrayList<Coordinates> wayPoints){
		FlightPlan flightPlan = new FlightPlan(start,wayPoints);
		flights.addNewFlight(flightPlan);
	}
	
	/**
	 * 
	 * @return all current flights.
	 */
	public Flights getFlights(){
		return flights;
	}
	
	/**
	 * Launches a single drone to its currently defined waypoints.
	 * @param strDroneID
	 * @param startingLocation
	 * @param wayPoints
	 * @throws FlightZoneException 
	 */
	private void launchSingleDroneToWayPoints() throws FlightZoneException{
		// Check to make sure that there is a pending flight plan and available drone.
		if (flights.hasPendingFlight() && droneFleet.hasAvailableDrone()){
			ManagedDrone drone = droneFleet.getAvailableDrone(); 
			if (drone != null) {
				safetyMgr.attachDrone(drone);
				FlightPlan flightPlan = flights.getNextFlightPlan();
				System.out.println(flightPlan.getFlightID());
				IFlightDirector flightDirectives = new SoloDirector(drone);
				flightDirectives.setWayPoints(flightPlan.getWayPoints());
				drone.assignFlight(flightDirectives);
				flightDirectives.flyToNextPoint();
				drone.getFlightModeState().setModeToAwaitingTakeOffClearance();
				flightPlan.setStatusToFlying(drone);	
			}
		}
	}

	/**
	 * Main run routine -- called internally.  Launches a new flight if viable, and checks for flights which have landed.
	 * Launched flights run autonomously at one thread per drone.
	 */
	@Override
	public void run() {
		while (true){
			// Launch new flight if feasible
			int NumberOfLaunchedFlights = flights.getCurrentFlights().size() + flights.getAwaitingTakeOffFlights().size();
			if (flights.hasPendingFlight() && NumberOfLaunchedFlights < flights.getMaximumNumberFlightsAllowed()){
				try {
					launchSingleDroneToWayPoints();
				} catch (FlightZoneException e) {
					e.printStackTrace();
				}
			}			
			// Check if any flights have landed
			flights.checkForLandedFlights(droneFleet, safetyMgr);
			if (flights.hasAwaitingTakeOff())
				try {
					flights.checkForTakeOffReadiness(droneFleet);
				} catch (FlightZoneException e1) {
					System.out.println("Failed Check for takeoff readiness.");
					e1.printStackTrace();
				}
			safetyMgr.checkForViolations();  // Used to run on its own thread!
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
//	/**
//	 * Given a complete filepath to a well structured XML file, loads one flight.
//	 * @param fileName
//	 */
//	public void loadFlightFromXML(String fileName){
//		new LoadXMLFlight(this,fileName);
//	}
	
//	/**
//	 * Given a complete filepath to a well structured XML file, loads one flight.
//	 * @param fileName
//	 */
	public void loadFlightFromXML(){
		new LoadXMLFlight(this);
	}
	
}
