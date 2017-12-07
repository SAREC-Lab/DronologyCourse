package edu.nd.dronology.core.collisionavoidance.guidancecommands;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestWaypointCommand {
    @Test
    public void testWaypointCommand() {
        LlaCoordinate ndStadium = new LlaCoordinate(41.698394, -86.233923, 236.0);
        WaypointCommand cmd = new WaypointCommand(ndStadium);
        assertEquals(new LlaCoordinate(41.698394, -86.233923, 236.0), cmd.getDestination());
    }
}
