package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;

import java.util.ArrayList;

/**
 * The StopEveryone CollisionAvoider is a failsafe that only triggers if it detects two drones are intruding into each
 * others space because something has gone wrong. Use this with mission plans that carefully navigate the drones to
 * avoid crashing into one another. StopEveryone assumes the drones will follow a mission plan that takes into account
 * where all the drones will be in space and time. When the StopEveryone CollisionAvoider is triggered the mission is
 * aborted, and humans need to land the drones manually.
 */
public class StopEveryone implements CollisionAvoider {

    private final double threshold;

    /**
     * Initializes a newly created StopEveryone object that triggers all drones to stop whatever they're doing and hover
     * in place, if any two drones move closer than the threshold distance to one another.
     *
     * @param threshold distance in meters. If any two drones move close enough to be within the threshold distance,
     *                  then StopEveryone will command all drones to stop whatever they're doing and hover in place.
     */
    public  StopEveryone(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void avoid(ArrayList<DroneSnapshot> drones) {
        for (int i = 0; i < drones.size() - 1; ++i) {
            for (int j = i + 1; j < drones.size(); ++j) {
                if (drones.get(i).getPosition().distance(drones.get(j).getPosition()) < this.threshold) {
                    System.out.println("Drone "+ i + " is too close to drone " + j +" ("+drones.get(i).getPosition().distance(drones.get(j).getPosition())+" meters)! Stopping everyone ");
                    for (int k = 0; k < drones.size(); k++) {
                        drones.get(k).getCommands().clear();
                        drones.get(k).getCommands().add(new StopCommand(-1.0));
                    }
                    return;
                }
            }
        }
    }
}
