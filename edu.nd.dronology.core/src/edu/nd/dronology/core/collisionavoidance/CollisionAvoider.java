package edu.nd.dronology.core.collisionavoidance;

import java.util.ArrayList;

/**
 * <p>
 *     A collision avoidance strategy.
 * </p>
 * <p>
 *     This is the strategy pattern from Design Patterns (Gang of four) where all information is passed in as a
 *     parameter.
 * </p>
 *
 * <p>
 *     When implementing this class you need to use the data provided in the list of DroneSnapshot(s) to figure out
 *     how to command each drone so that they don’t crash into one another. You change where the drones will fly by
 *     changing the list of commands in each DroneSnapshot(s). For example, if you want a drone to pause
 *     (hover in place) for 5 seconds before continuing with its mission, you would:
 * </p>
 * <pre>
 * {@code
 *
 * DroneSnapshot drone = ...
 * drone.getCommands().add(0, new StopCommand(5.0));
 * }
 * </pre>
 */
public interface CollisionAvoider {
    public void avoid(ArrayList<DroneSnapshot> drones);
}
