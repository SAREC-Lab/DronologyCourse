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

    @Test
    public void testExample() {
        /*
        this is the example in the java docs
         */
        NedCommand north = new NedCommand(5, 0, 0, 10);
        assertEquals(5.0, north.getNorth(),0.0);
        assertEquals(0.0, north.getEast(),0.0);
        assertEquals(0.0, north.getDown(),0.0);
        assertEquals(10.0, north.getTime(),0.0);
    }
}
