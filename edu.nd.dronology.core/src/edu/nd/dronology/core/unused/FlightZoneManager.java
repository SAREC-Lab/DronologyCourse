package edu.nd.dronology.core.unused;
//package edu.nd.dronology.core.flightzone;
//
//import java.util.List;
//
//import edu.nd.dronology.core.Discuss;
//import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
//import edu.nd.dronology.core.exceptions.FlightZoneException;
//import edu.nd.dronology.core.fleet.DroneFleetManager;
//import edu.nd.dronology.core.flight.FlightPlanFactory;
//import edu.nd.dronology.core.flight.Flights;
//import edu.nd.dronology.core.flight.IFlightDirector;
//import edu.nd.dronology.core.flight.IFlightPlan;
//import edu.nd.dronology.core.flight.internal.SoloDirector;
//import edu.nd.dronology.core.util.LlaCoordinate;
//import edu.nd.dronology.core.vehicle.ManagedDrone;
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
//public class FlightZoneManager implements Runnable {
//
//	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightZoneManager.class);
//
//	private Flights flights;
//	private DroneSeparationMonitor safetyMgr;
//	private DroneFleetManager droneFleet;
//
//	/**
//	 * Constructs a new FlightZoneManager.
//	 * 
//	 * @throws InterruptedException
//	 */
//	public FlightZoneManager() throws InterruptedException {
//		droneFleet = DroneFleetManager.getInstance();
//		safetyMgr = DroneSeparationMonitor.getInstance();
//		flights = new Flights(safetyMgr);
//	}
//
//	public DroneFleetManager getDroneFleet() {
//		return droneFleet; // replace with iterator later.
//	}
//
//	/**
//	 * Runs on an independent thread
//	 */
//	public void startThread() {
//		(new Thread(this)).start();
//	}
//
//	/**
//	 * Creates a new flight plan
//	 * 
//	 * @param start
//	 * @param wayPoints
//	 */
//	public void planFlight(String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
//		IFlightPlan flightPlan = FlightPlanFactory.create(planName, start, wayPoints);
//		flights.addNewFlight(flightPlan);
//	}
//
//	/**
//	 * 
//	 * @return all current flights.
//	 */
//	public Flights getFlights() {
//		return flights;
//	}
//
//	/**
//	 * Launches a single drone to its currently defined waypoints.
//	 * 
//	 * @param strDroneID
//	 * @param startingLocation
//	 * @param wayPoints
//	 * @throws FlightZoneException
//	 */
//	private void launchSingleDroneToWayPoints() throws FlightZoneException {
//		// Check to make sure that there is a pending flight plan and available
//		// drone.
//		if (flights.hasPendingFlight() && droneFleet.hasAvailableDrone()) {
//			ManagedDrone drone = droneFleet.getAvailableDrone();
//			if (drone != null) {
//				safetyMgr.attachDrone(drone);
//				IFlightPlan flightPlan = flights.activateNextFlightPlan();
//				LOGGER.info(flightPlan.getFlightID());
//				IFlightDirector flightDirectives = new SoloDirector(drone);
//
//				flightDirectives.setWayPoints(flightPlan.getWayPoints());
//				drone.assignFlight(flightDirectives);
//
//				drone.getFlightModeState().setModeToAwaitingTakeOffClearance();
//
//				// flightDirectives.flyToNextPoint();
//				// flightPlan.setStatusToFlying(drone);
//			}
//		}
//	}
//
//	@Discuss(discuss = "Check if thread  safe... re-organize flight management...")
//	private ManagedDrone launchSingleDrone() throws FlightZoneException {
//		// Check to make sure that there is a pending flight plan and available
//		// drone.
//		if (flights.hasPendingFlight() && droneFleet.hasAvailableDrone()) {
//			IFlightPlan flightPlan = flights.getNextFlightPlan();
//			ManagedDrone drone;
//			if (flightPlan.getDesignatedDroneId() != null) {
//				drone = droneFleet.getAvailableDrone(flightPlan.getDesignatedDroneId());
//			} else {
//				drone = droneFleet.getAvailableDrone();
//			}
//
//			if (drone != null) {
//				safetyMgr.attachDrone(drone);
//				flightPlan = flights.activateNextFlightPlan();
//				LOGGER.info(flightPlan.getFlightID() + " assigned to " + drone.getDroneName());
//				IFlightDirector flightDirectives = new SoloDirector(drone);
//				flightDirectives.setWayPoints(flightPlan.getWayPoints());
//				drone.assignFlight(flightDirectives);
//				// this needs to be moved to launch....
//				flightPlan.setStatusToFlying(drone);
//				drone.getFlightModeState().setModeToAwaitingTakeOffClearance();
//				return drone;
//			}
//		}
//		return null;
//
//	}
//
//	private void launchToWaypoint(ManagedDrone drone) throws FlightZoneException {
//		drone.getFlightDirective().flyToNextPoint();
//		// flightPlan.setStatusToFlying(drone);
//
//	}
//
//	/**
//	 * Main run routine -- called internally. Launches a new flight if viable, and checks for flights which have landed. Launched flights run autonomously at one thread per drone.
//	 */
//	@Discuss(discuss = "change from thread to listener based")
//	public void run2() {
//		while (true) {
//			// Launch new flight if feasible
//			int numberOfLaunchedFlights = flights.getCurrentFlights().size() + flights.getAwaitingTakeOffFlights().size();
//			if (flights.hasPendingFlight() && numberOfLaunchedFlights < flights.getMaximumNumberFlightsAllowed()) {
//				try {
//					launchSingleDroneToWayPoints();
//				} catch (FlightZoneException e) {
//					LOGGER.error(e);
//				}
//			}
//			// Check if any flights have landed
//			flights.checkForLandedFlights(droneFleet, safetyMgr);
//			if (flights.hasAwaitingTakeOff())
//				try {
//					flights.checkForTakeOffReadiness(droneFleet);
//				} catch (FlightZoneException e1) {
//					LOGGER.error("Failed Check for takeoff readiness.", e1);
//				}
//			safetyMgr.checkForViolations(); // Used to run on its own thread!
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				LOGGER.error(e);
//			}
//		}
//	}
//
//	@Override
//	@Discuss(discuss = "change from thread to listener based")
//	public void run() {
//		while (true) {
//			ManagedDrone launched = null;
//			// Launch new flight if feasible
//			int numberOfLaunchedFlights = flights.getCurrentFlights().size() + flights.getAwaitingTakeOffFlights().size();
//
//			if (flights.hasPendingFlight() && numberOfLaunchedFlights < flights.getMaximumNumberFlightsAllowed()) {
//				try {
//					launched = launchSingleDrone();
//				} catch (FlightZoneException e) {
//					LOGGER.error(e);
//				}
//
//				if (flights.hasAwaitingTakeOff()) {
//					LOGGER.info("Awaiting Takeoff:" + flights.getAwaitingTakeOffFlights().get(0).getFlightID());
//					try {
//						flights.checkForTakeOffReadiness(droneFleet);
//					} catch (FlightZoneException e1) {
//						LOGGER.error("Failed Check for takeoff readiness.", e1);
//					}
//				}
//
//				// try {
//				// if (launched != null) {
//				// launchToWaypoint(launched);
//				// }
//				// } catch (FlightZoneException e) {
//				// LOGGER.error(e);
//				// }
//
//			}
//			if (flights.hasAwaitingTakeOff() && numberOfLaunchedFlights < flights.getMaximumNumberFlightsAllowed()) {
//				LOGGER.info("Awaiting Takeoff:" + flights.getAwaitingTakeOffFlights().get(0).getFlightID());
//				try {
//					flights.checkForTakeOffReadiness(droneFleet);
//				} catch (FlightZoneException e1) {
//					LOGGER.error("Failed Check for takeoff readiness.", e1);
//				}
//			}
//
//			// Check if any flights have landed
//			flights.checkForLandedFlights(droneFleet, safetyMgr);
//
//			safetyMgr.checkForViolations(); // Used to run on its own thread!
//
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				LOGGER.error(e);
//			}
//		}
//	}
//
//	public void planFlight(String uavid, String planName, LlaCoordinate start, List<LlaCoordinate> wayPoints) {
//		IFlightPlan flightPlan = FlightPlanFactory.create(uavid, planName, start, wayPoints);
//		flights.addNewFlight(flightPlan);
//
//	}
//
//}
