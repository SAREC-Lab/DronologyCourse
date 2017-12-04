package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.coordinate.LlaCoordinate;

import java.util.ArrayList;

/**
 *
 * 
 */
public class DroneSnapshot {
    
    private LlaCoordinate position;

    private final ArrayList<Command> commands;

    public DroneSnapshot(LlaCoordinate position) {
        this.position = position;
        this.commands = new ArrayList<>();
    }
    
    public LlaCoordinate getPosition() {
        return this.position;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
