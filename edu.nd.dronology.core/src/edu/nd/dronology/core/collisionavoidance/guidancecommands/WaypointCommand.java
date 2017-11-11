package edu.nd.dronology.core.collisionavoidance.guidancecommands;

import edu.nd.dronology.core.util.LlaCoordinate;

public class WaypointCommand extends Command {
    private final LlaCoordinate destination;

    public WaypointCommand(LlaCoordinate destination) {
        this.destination = destination;
    }
}
