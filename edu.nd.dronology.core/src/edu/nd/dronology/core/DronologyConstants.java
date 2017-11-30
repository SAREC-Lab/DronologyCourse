package edu.nd.dronology.core;

public class DronologyConstants {

	/**
	 * The maximum number of individual uav threads - i.e., the maximum number of
	 * uavs handled by dronology.
	 */
	public static final int MAX_DRONE_THREADS = 50;

	/**
	 * The speed with which the drone returns to its home location once the 'return
	 * to home' command is issued. If set to {@code null}, the uav uses its last
	 * known speed as the return to home speed.
	 */
	public static final Double RETURN_TO_HOME_SPEED = null;

	/**
	 * The safety zone of the uav in m which is used for determining whether a uav
	 * is allowed to take off.
	 */
	public static final double SAFETY_ZONE = 5;

	/**
	 * The takeoff altitude of the uav (vertical distance in m before going to the
	 * first waypoint).
	 */
	public static final double TAKE_OFF_ALTITUDE = 15;

	/**
	 * The distance to the waypoint in m in which the waypoint is considered
	 * reached.
	 */
	public static final double THRESHOLD_WAYPOINT_DISTANCE = 5.0;
	
	/**
	 * The distance to the altitude in m in which the takeoff height
	 * reached.
	 */
	public static final double THRESHOLD_TAKEOFF_HEIGHT = 0.8;

	public static final double HOME_ALTITUDE = 5;

	public static final int MAX_IN_AIR = 10;
	
	

	public static final int NR_MESSAGES_IN_QUEUE = 100;

	public static final int MAX_GROUNDSTATIONS = 5;

	public static final boolean USE_SAFETY_CHECKS = false;

	public static final boolean USE_MONITORING = true;

	/**
	 * Frequency the status check timer is executed (ms)
	 */
	public static final long FREQUENCY_STATUS_CHECKS = 1000;
	
	/**
	 * Frequency the collision avoidance check timer is executed (ms)
	 */
	public static final long FREQUENCY_COLLISION_CHECKS = 1000;

}
