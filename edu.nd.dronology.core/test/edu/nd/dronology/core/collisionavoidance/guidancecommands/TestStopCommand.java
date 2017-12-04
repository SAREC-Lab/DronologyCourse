package edu.nd.dronology.core.collisionavoidance.guidancecommands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestStopCommand {
    @Test
    public void testGetTime1() {
        StopCommand stop = new StopCommand(4);
        assertEquals(4.0, stop.getTime(), 0.0);
    }

    @Test
    public void testGetTime2() {
        StopCommand stop = new StopCommand(0);
        assertEquals(0.0, stop.getTime(), 0.0);
    }

    @Test
    public void testGetTime3() {
        StopCommand stop = new StopCommand(-1.0);
        assertEquals(-1.0, stop.getTime(), 0.0);
    }

    @Test
    public void testGetTime4() {
        StopCommand stop = new StopCommand(10.0);
        assertEquals(10.0, stop.getTime(), 0.0);
    }
}
