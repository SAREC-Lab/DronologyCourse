package edu.nd.dronology.core.vehicle;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.RateLimiter;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.air_traffic_control.DroneSeparationMonitor;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.flight.FlightDirectorFactory;
import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.core.vehicle.ManagedDrone.HaltTimerTask;
import edu.nd.dronology.util.NamedThreadFactory;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Creates a Managed drone.
 * 
 * @author Jane Cleland-Huang
 * @version 0.01
 */
@Discuss(discuss = "does the drone need to be observable?")
public class ManagedDrone extends Observable implements Runnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedDrone.class);

	private RateLimiter LIMITER = RateLimiter.create(1000);

	private static final ExecutorService EXECUTOR_SERVICE = Executors
			.newFixedThreadPool(DronologyConstants.MAX_DRONE_THREADS, new NamedThreadFactory("ManagedDrone"));

	private final IDrone drone; // Controls primitive flight commands for drone

	@Discuss(discuss = "why does each drone has his on instance of that? - This variable is never set!")
	private final DroneSeparationMonitor safetyMgr;

	private DroneFlightStateManager droneState;
	private DroneSafetyStateManager droneSafetyState;

	@Discuss(discuss = "do we need this here?")
	private boolean missionCompleted = false;

	@Discuss(discuss = "do we need this here?")
	private LlaCoordinate targetCoordinates = null;

	@Discuss(discuss = "should this be configurable?")
	private static final int NORMAL_SLEEP = 1;
	private int currentSleep = NORMAL_SLEEP;

	@Discuss(discuss = "why not final? - new flight director for each flight??")
	private IFlightDirector flightDirector = null; // Each drone can be assigned
	// a single flight plan.
	private double targetAltitude = 0;

	private final Lock lock = new ReentrantLock();
	private final Condition continueCycle = lock.newCondition();

	private Timer haltTimer = new Timer();
	private HaltTimerTask currentHaltTimer;

	/**
	 * Constructs drone
	 * 
	 * @param drone
	 * @param drnName
	 */

	@Discuss(discuss = "FlightDirector should be injected.. we might end up having different ones...")
	public ManagedDrone(IDrone drone) {
		NullUtil.checkNull(drone);
		safetyMgr = DroneSeparationMonitor.getInstance();
		this.drone = drone;// specify
		droneState = new DroneFlightStateManager(this);
		droneSafetyState = new DroneSafetyStateManager();
		drone.getDroneStatus().setStatus(droneState.getStatus());
		this.flightDirector = FlightDirectorFactory.getFlightDirector(this); // Don't
		droneState.addStateChangeListener(() -> notifyStateChange());
	}

	private void notifyStateChange() {
		drone.getDroneStatus().setStatus(droneState.getStatus());
	}

	/**
	 * Assigns a flight directive to the managed drone
	 * 
	 * @param flightDirective
	 */
	public void assignFlight(IFlightDirector flightDirective) {
		this.flightDirector = flightDirective;
	}

	/**
	 * Removes an assigned flight
	 */
	public void unassignFlight() {
		flightDirector = null; // DANGER. NEEDS FIXING. CANNOT UNASSIGN FLIGHT
		// WITHOUT RETURNING TO BASE!!!
		LOGGER.warn("Unassigned DRONE: " + getDroneName());
	}

	// /**
	// * @return latitude of current drone position
	// */
	// public double getLatitude() {
	// return drone.getLatitude(); // currentPosition.getLatitude();
	// }
	//
	// /**
	// *
	// * @return longitude of current drone position
	// */
	// public double getLongitude() {
	// return drone.getLongitude();
	// }

	public void returnToHome() {
		synchronized (droneSafetyState) {
			getFlightSafetyModeState().setSafetyModeToNormal();

			if (currentHaltTimer != null) {
				currentHaltTimer.cancel();
				currentHaltTimer = null;
			}

		}

	}

	// /**
	// *
	// * @return Altitude of current drone position
	// */
	// public double getAltitude() {
	// return drone.getAltitude();
	// }

	/**
	 * 
	 * @param targetAltitude
	 *            Sets target altitude for takeoff
	 */
	public void setTargetAltitude(double targetAltitude) {
		this.targetAltitude = targetAltitude;
	}

	/**
	 * Controls takeoff of drone
	 * 
	 * @throws FlightZoneException
	 */
	public void takeOff() throws FlightZoneException {
		missionCompleted = false;
		if (targetAltitude == 0) {
			throw new FlightZoneException("Target Altitude is 0");
		}
		droneState.setModeToTakingOff();
		drone.takeOff(targetAltitude);

	}

	/**
	 * Delegates flyto behavior to virtual or physical drone
	 * 
	 * @param targetCoordinates
	 * @param speed
	 */
	public void flyTo(LlaCoordinate targetCoordinates, Double speed) {
		drone.flyTo(targetCoordinates, speed);
	}

	/**
	 * Gets current coordinates from virtual or physical drone
	 * 
	 * @return current coordinates
	 */
	public LlaCoordinate getCoordinates() {
		return drone.getCoordinates();
	}

	public void start() {
		// thread.start();
		LOGGER.info("Starting Drone '" + drone.getDroneName() + "'");
		EXECUTOR_SERVICE.submit(this);
	}

	@Override
	public void run() {
		try {
			while (true) {// && j < 500){
				// Drone has been temporarily halted. Reset to normal mode once
				// sleep is completed.
				LIMITER.acquire();
				setSleep(NORMAL_SLEEP);
				// try {
				// continueCycle.await();
				// } catch (InterruptedException e) {
				// LOGGER.error(e);
				// }
				// synchronized (droneSafetyState) {
				// if (droneSafetyState.isSafetyModeHalted()) {
				// droneSafetyState.setSafetyModeToNormal();
				// droneState.setModeToFlying();
				//
				// }
				// }

				// Drone currently is assigned a flight directive.
				if (flightDirector != null && droneState.isFlying()) {
					targetCoordinates = flightDirector.flyToNextPoint();

					// Move the drone. Returns FALSE if it cannot move because
					// it
					// has reached destination
					if (!drone.move(10)) {
						LOGGER.missionInfo(
								drone.getDroneName() + " - Waypoint reached - " + targetCoordinates.toString());
						flightDirector.clearCurrentWayPoint();
					}
					// Check for end of flight
					checkForEndOfFlight();

					// Check for takeoff conditions
					// checkForTakeOff();

					// Set check voltage
					drone.setVoltageCheckPoint();
					// Added another check for previously denied takeoff...
					// not working right now because safety manger is null...
				}
				if (droneState.isAwaitingTakeoffClearance()) {
					// checkForTakeOff();
				}
				if (droneState.isTakingOff()) {
					if (drone.getAltitude() >= (targetAltitude - 3)) {
						LOGGER.info("Target Altitude reached - ready for flying");
						try {
							droneState.setModeToFlying();
							// drone.getDroneStatus().setStatus(droneState.getStatus());
						} catch (FlightZoneException e) {
							LOGGER.error(e);
						}

					}
				}
				// while (drone.getAltitude() < (targetAltitude - 3)) {
				// // TODO: ask about how to properly when finished taking off
				// LOGGER.info("Waiting for drone #" +
				// drone.getDroneStatus().getID() + " to complete takeoff...");
				//
				// System.out.println(drone.getAltitude());
				// try {
				// Thread.sleep(500);
				// } catch (InterruptedException e) {
				// LOGGER.error(e);
				// }
				// }

				// LOGGER.info(drone.getDroneName() + " " +
				// droneState.getStatus());
			}
		} catch (Throwable e) {
			LOGGER.error(e);
		}
	}

	private void setSleep(int sleepInms) {
		if (currentSleep == sleepInms) {
			return;
		}
		currentSleep = sleepInms;
		double permits = 1 / new Double(currentSleep);
		// double permits = (1 / currentSleep) * 1000d;
		LIMITER.setRate(permits);
		LOGGER.info("Permits set to " + permits);
	}

	// Check for end of flight. Land if conditions are satisfied
	private boolean checkForEndOfFlight() {
		if (flightDirector != null && flightDirector.readyToLand())
			return false; // it should have returned here.
		if (droneState.isLanding())
			return false;
		if (droneState.isOnGround())
			return false;
		if (droneState.isInAir())
			return false;

		// Otherwise
		try {
			land();
		} catch (FlightZoneException e) {
			LOGGER.error(getDroneName() + " is not able to land!", e);
		}
		return true;
	}

	// Check for takeoff. Takeoff if conditions are satisfied.
	private boolean checkForTakeOff() {

		if (flightDirector != null && !flightDirector.readyToTakeOff())
			return false;
		if (droneState.isTakingOff())
			return false;
		if (safetyMgr == null) // Sometimes caused at startup by race conditions
			return false;
		if (!safetyMgr.permittedToTakeOff(this))
			return false;

		LOGGER.info("Passed takeoff test");
		// Otherwise
		// try {
		// takeOff();
		// } catch (FlightZoneException e) {
		// LOGGER.error(getDroneName() + " is not able to takeoff!", e);
		// }
		return true;
	}

	/**
	 * 
	 * @return unique drone ID
	 */
	public String getDroneName() {
		return drone.getDroneName();
	}

	/**
	 * 
	 * @return target coordinates
	 */
	public LlaCoordinate getTargetCoordinates() {
		return targetCoordinates;
	}

	/**
	 * 
	 * @return current flight directive assigned to the managed drone
	 */
	public IFlightDirector getFlightDirective() {
		return flightDirector;
	}

	/**
	 * Land the drone. Delegate land functions to virtual or physical drone
	 * 
	 * @throws FlightZoneException
	 */
	public void land() throws FlightZoneException {
		if (!droneState.isLanding() || !droneState.isOnGround()) {
			droneState.setModeToLanding();
			drone.land();
			droneState.setModeToOnGround();
			unassignFlight();
		}
	}

	/**
	 * Temporarily Halt
	 * 
	 * @param haltinms
	 */
	public void haltInPlace(int haltinms) {
		synchronized (droneSafetyState) {
			try {
				if (currentHaltTimer != null) {
					currentHaltTimer.cancel();
					droneSafetyState.setSafetyModeToNormal();
					droneState.setModeToFlying();
					currentHaltTimer = null;
				} else {
					droneSafetyState.setSafetyModeToHalted();
					droneState.setModeToInAir();
					currentHaltTimer = new HaltTimerTask();
					haltTimer.schedule(currentHaltTimer, haltinms);
				}

			} catch (FlightZoneException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * 
	 * return current flight mode state
	 * 
	 * @return droneState
	 */
	public DroneFlightStateManager getFlightModeState() {
		return droneState;
	}

	/**
	 * 
	 * @return current safety mode state
	 */
	public DroneSafetyStateManager getFlightSafetyModeState() {
		return droneSafetyState;
	}

	/**
	 * Set mission completed status
	 */
	public void setMissionCompleted() {
		missionCompleted = true;
	}

	/**
	 * 
	 * @return mission status
	 */
	public boolean missionInProgress() {
		return !missionCompleted;
	}

	/**
	 * Retrieve battery status from drone
	 * 
	 * @return remaining voltage
	 */
	public double getBatteryStatus() {
		return drone.getBatteryStatus();
	}

	public LlaCoordinate getBaseCoordinates() {
		return drone.getBaseCoordinates();
	}

	public class HaltTimerTask extends TimerTask {

		@Override
		public void run() {
			synchronized (droneSafetyState) {
				if (!droneSafetyState.isSafetyModeHalted()) {
					currentHaltTimer = null;
					return;
				}

				try {
					droneSafetyState.setSafetyModeToNormal();
					droneState.setModeToFlying();
					currentHaltTimer = null;
				} catch (FlightZoneException e) {
					LOGGER.error(e);
				}
			}
		}

	}

}
