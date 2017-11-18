package edu.nd.dronology.core.vehicle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.RateLimiter;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.fleet.DroneFleetManager;
import edu.nd.dronology.core.flight.FlightDirectorFactory;
import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.vehicle.commands.AbstractDroneCommand;
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
public class ManagedDrone implements Runnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedDrone.class);

	private AtomicBoolean cont = new AtomicBoolean(true);

	private RateLimiter LIMITER = RateLimiter.create(1000);

	private static final ExecutorService EXECUTOR_SERVICE = Executors
			.newFixedThreadPool(DronologyConstants.MAX_DRONE_THREADS, new NamedThreadFactory("ManagedDrone"));

	private final IDrone drone; // Controls primitive flight commands for drone

	private DroneFlightStateManager droneState;
	private DroneSafetyStateManager droneSafetyState;

	@Discuss(discuss = "do we need this here?")
	private boolean missionCompleted = false;

	@Discuss(discuss = "do we need this here?")
	private LlaCoordinate targetCoordinates = null;

	@Discuss(discuss = "why not final? - new flight director for each flight??")
	private IFlightDirector flightDirector = null; // Each drone can be assigned
	// a single flight plan.
	private double targetAltitude = 0;

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

	public void returnToHome() {
		synchronized (droneSafetyState) {
			getFlightSafetyModeState().setSafetyModeToNormal();

			if (currentHaltTimer != null) {
				currentHaltTimer.cancel();
				currentHaltTimer = null;
			}

		}

	}

	/**
	 * 
	 * @param targetAltitude
	 *          Sets target altitude for takeoff
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
			while (cont.get() && !Thread.currentThread().isInterrupted()) {
				LIMITER.acquire();

				// Probably not necessary anymore... TODO: fix- do not try to assign point in every iteration of the loop...
				if (flightDirector != null && droneState.isFlying()) {
					targetCoordinates = flightDirector.flyToNextPoint();

					if (!drone.move(0.1)) {
						LOGGER.missionInfo(drone.getDroneName() + " - Waypoint reached - " + targetCoordinates.toString());
						flightDirector.clearCurrentWayPoint();
					}
					checkForEndOfFlight();
				}
				if (droneState.isTakingOff()) {
					if (drone.getAltitude() >= (targetAltitude - DronologyConstants.THRESHOLD_TAKEOFF_HEIGHT)) {
						LOGGER.info("Target Altitude reached - ready for flying");
						try {
							droneState.setModeToFlying();
						} catch (FlightZoneException e) {
							LOGGER.error(e);
						}
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e);
		}
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

	private boolean checkForTakeOff() {

		if (flightDirector != null && !flightDirector.readyToTakeOff()) {
			return false;
		}
		if (droneState.isTakingOff()) {
			return false;
		}
		if (!permissionForTakeoff()) {
			return false;
		}
		LOGGER.info("Passed takeoff test");
		return true;
	}

	// needs refactoring to improve performance...
	public boolean permissionForTakeoff() {
		double dronDistance = 0;
		List<ManagedDrone> flyingDrones = DroneFleetManager.getInstance().getRegisteredDrones();
		for (ManagedDrone drone2 : flyingDrones) {
			if (!this.equals(flyingDrones)
					&& (drone2.getFlightModeState().isFlying() || drone2.getFlightModeState().isInAir())) {
				dronDistance = this.getCoordinates().distance(drone2.getCoordinates());
				if (dronDistance < DronologyConstants.SAFETY_ZONE) {
					LOGGER.error("Safety Distance Violation - Drone not allowed to TakeOff! distance: " + dronDistance
							+ " safety zone: " + DronologyConstants.SAFETY_ZONE + " => " + dronDistance);
					return false;
				}
			}
		}
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

	public void sendCommand(AbstractDroneCommand command) throws DroneException {
		drone.sendCommand(command);

	}

}
