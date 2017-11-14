package edu.nd.dronology.core.collisionavoidance.guidancecommands;

/**
 * A command that tells the UAV to hover in place.
 */
public class StopCommand extends Command {

    private final double time;

    /**
     * <p>
     * Creates a stop command. This command tells the UAV to hover in place for the specified number of seconds. If
     * given a negative or zero time parameter, then this command will remain in effect indefinitely.
     * </p>
     * <p>
     * <p>
     * For example, to create a stop command that lasts for 10 seconds
     * <pre>
     *         {@code
     *         StopCommand stop = new StopCommand(10.0);
     *         }
     *     </pre>
     * </p>
     * <p>
     * <p>
     * To create a stop command that lasts until someone or something intervenes
     * <pre>
     *         {@code
     *         StopCommand stopForever = new StopCommand(0);
     *         }
     *     </pre>
     * </p>
     *
     * @param time in seconds that this stop command is to remain in effect. A value less than or equal to 0 indicates
     *             that this command should remain in effect indefinitely or until someone or something intervenes.
     */
    public StopCommand(double time) {
        this.time = time;
    }

    /**
     * Return the amount of time this command should remain in effect. A value less than or equal to 0 indicates that
     * this command should remain in effect indefinitely.
     *
     * @return time this stop command is to remain in effect. A value less than or equal to 0 indicates that this
     * command should remain in effect indefinitely.
     */
    public double getTime() {
        return time;
    }

}
