package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;

import java.util.ArrayList;

public class StopEveryone implements CollisionAvoider {

    private final double threshold;

    public  StopEveryone(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void avoid(ArrayList<DroneSnapshot> drones) {
        for (int i = 0; i < drones.size() - 1; ++i) {
            for (int j = i + 1; j < drones.size(); ++j) {
                if (i != j) {
                    if (drones.get(i).getPosition().distance(drones.get(j).getPosition()) < this.threshold) {
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
}
