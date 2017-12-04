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
 *
 * <h2>FAQ</h2>
 * <h3>Who is this for?</h3>
 * <p>Developers who want to create a collision avoidance algorithm with Dronology.</p>
 * <h3>What do you need to know?</h3>
 * <p>You need to know how to read the state of each drone and how to command each drone to avoid crashing.</p>
 * <h3> How do you read the state of each drone?</h3>
 * <p>You iterate through the list of Drone Snapshots. Each Drone Snapshot includes getters for data sensed by each drone. For example, you can call the get position method to access the drone's position:</p>
 * <pre>
 *     {@code
 *     drone.getPosition();
 *     }
 * </pre>
 * <h3>How do you control each drone?</h3>
 * <p>Each drone snapshot includes a get Commands method that gives you access to an array list of commands. By changing this list you are changing the queue of actions a given drone will execute. For example, to have a drone pause for 5 seconds before continuing on with its mission, insert a stop command at the start of the commands list:</p>
 *
 * <pre>
 *     {@code
 *     drone.getCommands().add(0, new StopCommand(5.0));
 *     }
 * </pre>
 *
 * <p>This inserts the stop command at the start of the commands list. When Dronology sees this change, it will radio the drone's on board autopilot and tell it to hover in place for 5 seconds. After that Dronology will radio the autopilot again with the next command in the get commands list. Here are 3 examples of possible commands you can create:
 * <ul>
 *     <li>A NedCommand. This command tells a drone to fly at given North, East, and Down velocity for a specified amount of time. @see {@link edu.nd.dronology.core.collisionavoidance.guidancecommands.NedCommand}</li>
 *     <li>A StopCommand. This tells a drone to hover in place for a specified amount of time. @see {@link edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand}</li>
 *     <li>A WaypointCommand. This tells a drone to fly to a given waypoint. @see {@link edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand}</li>
 * </ul>
 * Finally, you can also remove commands from the commands list.
 * </p>
 */
public interface CollisionAvoider {
    public void avoid(ArrayList<DroneSnapshot> drones);
}
