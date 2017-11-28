package edu.nd.dronology.core.collisionavoidance.guidancecommands;

/**
 * A command that tells a UAV to fly in a direction for a specified amount of time.
 */
public class NedCommand extends Command {
    private final double north, east, down, time;

    /**
     * <p>
     * Creates a command that tells a UAV to fly in the direction given as a NED vector for the given amount of time.
     * </p>
     *
     * <p>
     * Each component of the NED vector is given in meters per second. The time parameter is given in seconds.
     * </p>
     *
     * <p>
     *     For example, to create a NedCommand that tells the UAV to fly north at 5 meters per second for 10 seconds.
     *     <pre>
     *         {@code
     *         NedCommand north = new NedCommand(5, 0, 0, 10);
     *         }
     *     </pre>
     * </p>
     *
     * @param north the north component of the velocity vector given in meters per second
     * @param east the east component of the velocity vector given in meters per second
     * @param down the down component of the velocity vector given in meters per second
     * @param time the amount of time to fly at the given velocity in seconds
     */
    public NedCommand(double north, double east, double down, double time) {
        this.north = north;
        this.east = east;
        this.down = down;
        this.time = time;
    }

    /**
     *
     * @return the north component of the velocity vector in meters per second
     */
    public double getNorth() {
        return north;
    }

    /**
     *
     * @return the east component of the velocity vector in meters per second
     */
    public double getEast() {
        return east;
    }

    /**
     *
     * @return the down component of the velocity vector in meters per second
     */
    public double getDown() {
        return down;
    }

    /**
     *
     * @return the amount of time to fly at the given velocity in seconds
     */
    public double getTime() {
        return time;
    }
}
