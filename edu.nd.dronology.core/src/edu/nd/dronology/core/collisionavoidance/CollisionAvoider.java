package edu.nd.dronology.core.collisionavoidance;

import java.util.ArrayList;

/**
 *
 * 
 */
public interface CollisionAvoider {
    public void avoid(ArrayList<DroneSnapshot> drones);
}
