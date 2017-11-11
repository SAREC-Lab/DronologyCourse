package edu.nd.dronology.core.collisionavoidance.guidancecommands;

public class NedCommand extends Command {
    private final double north, east, down, time;

    public NedCommand(double north, double east, double down, double time) {
        this.north = north;
        this.east = east;
        this.down = down;
        this.time = time;
    }
}
