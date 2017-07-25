package edu.nd.dronology.core.flightzone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.fleet.DroneFleetManager;
import edu.nd.dronology.core.flight.FlightPlanFactory;
import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.flight.IFlightPlan;
import edu.nd.dronology.core.flight.IPlanStatusChangeListener;
import edu.nd.dronology.core.flight.PlanPoolManager;
import edu.nd.dronology.core.flight.internal.SoloDirector;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightZoneManager2 implements IPlanStatusChangeListener {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightZoneManager2.class);

	private PlanPoolManager planPoolManager;
	private DroneSeparationMonitor safetyMgr;
	private DroneFleetManager droneFleet;

	private Timer timer;

	private AtomicInteger activeUAVS = new AtomicInteger(0);

	private final List<IFlightPlan> awaitingTakeOffFlights = Collections.synchronizedList(new ArrayList<>());

	private List<IFlightPlan> awaitingLandingFlights = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Constructs a new FlightZoneManager.
	 * 
	 * @throws InterruptedException
	 */
	public FlightZoneManager2() throws InterruptedException {
		droneFleet = DroneFleetManager.getInstance();
		safetyMgr = DroneSeparationMonitor.getInstance();
		planPoolManager = PlanPoolManager.getInstance();
		planPoolManager.addPlanStatusChangeListener(this);
		timer = new Timer();
		timer.scheduleAtFixedRate(new StatusCheckTask(), 100, 1000);
	}

	public DroneFleetManager getDroneFleet() {
		return droneFleet; // replace with iterator later.
	}

	private ManagedDrone tryAssignUAV() throws DroneException, FlightZoneException {
		IFlightPlan nextPlan = planPoolManager.getNextPendingPlan();
		ManagedDrone drone;
		if (nextPlan.getDesignatedDroneId() == null) {
			drone = droneFleet.getAvailableDrone();
			if (drone != null) {
				planPoolManager.assignPlan(nextPlan, drone.getDroneName());
			}
		} else {
			drone = droneFleet.getAvailableDrone(nextPlan.getDesignatedDroneId());
		}

		// TODO try to find a free uav from all plans..
		if (drone != null && (planPoolManager.getCurrentPlan(drone.getDroneName()) == null)) {
			safetyMgr.attachDrone(drone);
			planPoolManager.activatePlan(nextPlan, drone.getDroneName());
			if (drone.getFlightModeState().isOnGround()) {
				awaitingTakeOffFlights.add(nextPlan);
			}

			IFlightDirector flightDirectives = new SoloDirector(drone);
			flightDirectives.setWayPoints(nextPlan.getWayPoints());
			drone.assignFlight(flightDirectives);
			// // this needs to be moved to launch....
			nextPlan.setStatusToFlying(drone);
			if (drone.getFlightModeState().isInAir()) {
				drone.getFlightModeState().setModeToFlying();
			} else {
				drone.getFlightModeState().setModeToAwaitingTakeOffClearance();
			}
			return drone;
		}
		return null;
	}

	private class StatusCheckTask extends TimerTask {

		@Override
		public void run() {
			try {
				safetyMgr.checkForViolations();
				try {
					// checkForLandedFlights();
					checkForCompletedPlans();
				} catch (DroneException e) {
					LOGGER.error(e);
				}

				if (activeUAVS.get() == DronologyConstants.MAX_IN_AIR) {
					checkPendingForFlying();

				} else if (planPoolManager.hasPendingFlights()) {
					try {
						tryAssignUAV();
						checkPendingForFlyingWithTakeoff();
					} catch (DroneException e) {
						LOGGER.error(e);
					}

				}
				if (hasAwaitingTakeOff() && activeUAVS.get() < DronologyConstants.MAX_IN_AIR) {
					LOGGER.info("Awaiting Takeoff:" + getAwaitingTakeOffFlights().get(0).getFlightID());
					try {
						checkForTakeOffReadiness();
					} catch (FlightZoneException e1) {
						LOGGER.error("Failed Check for takeoff readiness.", e1);
					}
				}
				if (hasAwaitingLanding()) {
					checkForLandingReadiness();
				}

			} catch (Throwable t) {
				LOGGER.error(t);
			}
		}

	}

	/**
	 * Checks if the next pending flight is able to takeoff. Currently takeoff
	 * occurs in order of pending list.
	 * 
	 * @param droneFleet
	 * @throws FlightZoneException
	 */
	public void checkForTakeOffReadiness() throws FlightZoneException {
		// Technical debt.
		// Checks first waiting drone each time it is called.
		if (!awaitingTakeOffFlights.isEmpty()) {
			IFlightPlan awaitingFlightPlan = awaitingTakeOffFlights.get(0);
			ManagedDrone drone = awaitingFlightPlan.getAssignedDrone();
			if (safetyMgr.permittedToTakeOff(drone)) {
				// LOGGER.info(drone.getDroneName() + " taking off");
				drone.setTargetAltitude(awaitingFlightPlan.getTakeoffAltitude());
				drone.takeOff();
				activeUAVS.incrementAndGet();
				awaitingTakeOffFlights.remove(awaitingFlightPlan);
			}
		}
	}

	public void checkPendingForFlying() {
		for (IFlightPlan pendingPlan : planPoolManager.getPendingPlans()) {
			try {
				checkForScheduling(pendingPlan);
			} catch (DroneException | FlightZoneException e) {
				LOGGER.error(e);
			}
		}

	}

	public void checkPendingForFlyingWithTakeoff() {
		for (IFlightPlan pendingPlan : planPoolManager.getPendingPlans()) {
			try {
				checkForSchedulingWithTakeoff(pendingPlan);
			} catch (DroneException | FlightZoneException e) {
				LOGGER.error(e);
			}
		}

	}

	private void checkForScheduling(IFlightPlan pendingPlan) throws DroneException, FlightZoneException {
		ManagedDrone drone;
		if (pendingPlan.getDesignatedDroneId() == null) {
			drone = droneFleet.getAvailableDrone();
			if (drone != null) {
				planPoolManager.assignPlan(pendingPlan, drone.getDroneName());
			}
		} else {
			drone = droneFleet.getAvailableDrone(pendingPlan.getDesignatedDroneId());
		}

		if (drone == null || !drone.getFlightModeState().isInAir()
				|| (planPoolManager.getCurrentPlan(drone.getDroneName()) != null)) {
			if (drone != null) {
				droneFleet.returnDroneToAvailablePool(drone);
			}
			return;
		}

		safetyMgr.attachDrone(drone);
		planPoolManager.activatePlan(pendingPlan, drone.getDroneName());

		IFlightDirector flightDirectives = new SoloDirector(drone);
		flightDirectives.setWayPoints(pendingPlan.getWayPoints());
		drone.assignFlight(flightDirectives);
		// // this needs to be moved to launch....
		pendingPlan.setStatusToFlying(drone);
		if (drone.getFlightModeState().isInAir()) {
			drone.getFlightModeState().setModeToFlying();
		}
	}

	private void checkForSchedulingWithTakeoff(IFlightPlan pendingPlan) throws DroneException, FlightZoneException {
		ManagedDrone drone;
		if (pendingPlan.getDesignatedDroneId() == null) {
			drone = droneFleet.getAvailableDrone();
			if (drone != null) {
				planPoolManager.assignPlan(pendingPlan, drone.getDroneName());
			}
		} else {
			drone = droneFleet.getAvailableDrone(pendingPlan.getDesignatedDroneId());
		}

		if (drone == null || (planPoolManager.getCurrentPlan(drone.getDroneName()) != null)) {
			return;
		}

		safetyMgr.attachDrone(drone);
		planPoolManager.activatePlan(pendingPlan, drone.getDroneName());

		IFlightDirector flightDirectives = new SoloDirector(drone);
		flightDirectives.setWayPoints(pendingPlan.getWayPoints());
		drone.assignFlight(flightDirectives);
		// // this needs to be moved to launch....
		pendingPlan.setStatusToFlying(drone);
		if (drone.getFlightModeState().isInAir()) {
			drone.getFlightModeState().setModeToFlying();
		} else if (drone.getFlightModeState().isOnGround()) {
			awaitingTakeOffFlights.add(pendingPlan);
			drone.getFlightModeState().setModeToAwaitingTakeOffClearance();
		}

	}

	public void checkForLandingReadiness() {
		if (!awaitingLandingFlights.isEmpty() && awaitingLandingFlights.get(0).isCompleted()) {
			try {
				IFlightPlan awaitingFlightPlan = awaitingLandingFlights.get(0);
				ManagedDrone drone = droneFleet.getRegisteredDrone(awaitingFlightPlan.getDesignatedDroneId());
				LOGGER.info("Drone '" + drone.getDroneName() + "' ready to land");

				drone.land();
				// land after alt <1
				activeUAVS.decrementAndGet();
				awaitingLandingFlights.remove(0);
			} catch (FlightZoneException | DroneException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public boolean hasAwaitingLanding() {
		return awaitingLandingFlights.size() > 0;
	}

	public List<IFlightPlan> getAwaitingTakeOffFlights() {
		return Collections.unmodifiableList(awaitingTakeOffFlights);
	}

	public void checkForCompletedPlans() throws DroneException {
		planPoolManager.checkFormCompletedPlans();

	}

	public boolean hasAwaitingTakeOff() {
		return !awaitingTakeOffFlights.isEmpty();

	}

	public void planFlight(String uavid, String planName, List<Waypoint> waypoints) throws DroneException {
		IFlightPlan plan = FlightPlanFactory.create(uavid, planName, waypoints);
		PlanPoolManager.getInstance().addNewPlan(plan);

	}

	public void planFlight(String planName, List<Waypoint> waypoints) throws DroneException {

		IFlightPlan plan = FlightPlanFactory.create(planName, waypoints);
		PlanPoolManager.getInstance().addNewPlan(plan);

	}

	@Override
	public void notifyPlanChange(IFlightPlan changedPlan) {
		if (changedPlan.isCompleted()) {
			ManagedDrone drone = changedPlan.getAssignedDrone();
			if (drone != null) {
				droneFleet.returnDroneToAvailablePool(drone);
			}
		}

	}

	public void returnToHome(String uavid) throws DroneException {
		LOGGER.info(uavid + " returning to home");
		ManagedDrone drone = droneFleet.getRegisteredDrone(uavid);
		LlaCoordinate baseCoordinate = drone.getBaseCoordinates();

		LlaCoordinate homeCoordinate = new LlaCoordinate(baseCoordinate.getLatitude(), baseCoordinate.getLongitude(),
				DronologyConstants.HOME_ALTITUDE);
		Waypoint wps = new Waypoint(homeCoordinate);
		List<Waypoint> wpsList = new ArrayList<>();
		wpsList.add(wps);
		IFlightPlan homePlane = FlightPlanFactory.create(uavid, "Return to Home", wpsList);
		try {
			planPoolManager.overridePlan(homePlane, uavid);
			if (drone.getFlightModeState().isFlying()) {
				drone.getFlightModeState().setModeToInAir();
			}
			IFlightDirector flightDirectives = new SoloDirector(drone);
			flightDirectives.setWayPoints(homePlane.getWayPoints());
			drone.assignFlight(flightDirectives);
			awaitingLandingFlights.add(homePlane);

			drone.getFlightModeState().setModeToFlying();
			drone.returnToHome();
			homePlane.setStatusToFlying(drone);
		} catch (FlightZoneException e) {
			LOGGER.error(e);
		}
	}

	public void cancelPendingFlights(String uavid) throws DroneException {
		PlanPoolManager.getInstance().cancelPendingPlans(uavid);
	}

	public void pauseFlight(String uavid) throws DroneException {
		LOGGER.info(uavid + " Pause current flight");
		ManagedDrone drone = droneFleet.getRegisteredDrone(uavid);
		drone.haltInPlace(30000);
	}
}
