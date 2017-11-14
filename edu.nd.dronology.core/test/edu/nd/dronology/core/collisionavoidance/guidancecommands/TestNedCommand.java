package edu.nd.dronology.core.collisionavoidance.guidancecommands;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestNedCommand {
    @Test
    public void testGetters() {
        NedCommand example = new NedCommand(1.0, 2.0, 3.0, 4.0);
        assertEquals(1.0, example.getNorth(),0.0);
        assertEquals(2.0, example.getEast(),0.0);
        assertEquals(3.0, example.getDown(),0.0);
        assertEquals(4.0, example.getTime(),0.0);
    }
}
