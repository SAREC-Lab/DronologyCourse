package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStopEveryone {

    /*
    This test makes sure that StopEveryone brings everyone to a stop when two of the drones are too close to each other.
     */
    @Test
    public void testTooClose() {
        /*
        Create 3 coordinates at the South Bend Radio Control Club, where two drones are 5 meters apart and a third
        drone is a bit more than 50 meters away from both.
         */
        LlaCoordinate l1 = new LlaCoordinate(41.519125, -86.240702, 270);
        LlaCoordinate l2 = l1.findLla(new Vector3D(5.0, 0.0, 0.0));
        LlaCoordinate l3 = l1.findLla(new Vector3D(2.5, 50.0, 0.0));

        assertEquals(5.0, l1.distance(l2), 0.05);
        assertEquals(50.06, l1.distance(l3), 0.05);
        assertEquals(50.06, l2.distance(l3), 0.05);

        DroneSnapshot d1 = new DroneSnapshot(l1);
        d1.getCommands().add(new WaypointCommand(l3));
        d1.getCommands().add(new WaypointCommand(l2));
        d1.getCommands().add(new WaypointCommand(l1));
        ArrayList<Command> d1Cmds = new ArrayList<>(d1.getCommands());

        DroneSnapshot d2 = new DroneSnapshot(l2);
        d2.getCommands().add(new WaypointCommand(l1));
        d2.getCommands().add(new WaypointCommand(l3));
        d2.getCommands().add(new WaypointCommand(l2));
        ArrayList<Command> d2Cmds = new ArrayList<>(d2.getCommands());

        DroneSnapshot d3 = new DroneSnapshot(l3);
        d3.getCommands().add(new WaypointCommand(l2));
        d3.getCommands().add(new WaypointCommand(l1));
        d3.getCommands().add(new WaypointCommand(l3));
        ArrayList<Command> d3Cmds = new ArrayList<>(d3.getCommands());

        ArrayList<DroneSnapshot> drones = new ArrayList<>();
        drones.add(d1);
        drones.add(d2);
        drones.add(d3);
        ArrayList<DroneSnapshot> dronesCopy = new ArrayList<>(drones);
        /*
        Run the CollisionAvoider where two drones are considered too close if they are less than 10 meters apart
         */
        CollisionAvoider stopEveryoneAvoider = new StopEveryone(10);
        stopEveryoneAvoider.avoid(drones);

        assertTrue("The collision avoider didn't remove any drone", dronesCopy.equals(drones));
        assertTrue("Drone d1 has 1 command now", drones.get(0).getCommands().size() == 1);
        assertTrue("Drone d1 had all its commands replaced with a StopCommand", drones.get(0).getCommands().get(0) instanceof StopCommand);

        assertTrue("Drone d2 has 1 command now", drones.get(1).getCommands().size() == 1);
        assertTrue("Drone d2 had all its commands replaced with a StopCommand", drones.get(1).getCommands().get(0) instanceof StopCommand);

        assertTrue("Drone d3 has 1 command now", drones.get(2).getCommands().size() == 1);
        assertTrue("Drone d3 had all its commands replaced with a StopCommand", drones.get(2).getCommands().get(0) instanceof StopCommand);
    }

    /*
    This test makes sure that the StopEveryone CollisionAvoider does nothing when all the drones are far enough apart.
     */
    @Test
    public void testFarAway() {
        /*
        Create 3 coordinates at the South Bend Radio Control Club all about
        50 meters away from one another (they form an equilateral triangle in the sky).
         */
        LlaCoordinate l1 = new LlaCoordinate(41.518953, -86.239872, 270);
        LlaCoordinate l2 = l1.findLla(new Vector3D(43.30127, 25.0, 0.0));
        LlaCoordinate l3 = l2.findLla(new Vector3D(0.0, -50.0, 0.0));

        assertEquals(50.0, l1.distance(l2), 0.05);
        assertEquals(50.0, l1.distance(l3), 0.05);
        assertEquals(50.0, l2.distance(l3), 0.05);

        /*
        Create drone snapshots where all the drones are flying in a clock-wise loop around the triangle
         */
        DroneSnapshot d1 = new DroneSnapshot(l1);
        d1.getCommands().add(new WaypointCommand(l3));
        d1.getCommands().add(new WaypointCommand(l2));
        d1.getCommands().add(new WaypointCommand(l1));
        ArrayList<Command> d1Cmds = new ArrayList<>(d1.getCommands());
        assertTrue("Drone d1 has the same commands", d1Cmds.equals(d1.getCommands()));

        DroneSnapshot d2 = new DroneSnapshot(l2);
        d2.getCommands().add(new WaypointCommand(l1));
        d2.getCommands().add(new WaypointCommand(l3));
        d2.getCommands().add(new WaypointCommand(l2));
        ArrayList<Command> d2Cmds = new ArrayList<>(d2.getCommands());

        DroneSnapshot d3 = new DroneSnapshot(l3);
        d3.getCommands().add(new WaypointCommand(l2));
        d3.getCommands().add(new WaypointCommand(l1));
        d3.getCommands().add(new WaypointCommand(l3));
        ArrayList<Command> d3Cmds = new ArrayList<>(d3.getCommands());

        ArrayList<DroneSnapshot> drones = new ArrayList<>();
        drones.add(d1);
        drones.add(d2);
        drones.add(d3);
        ArrayList<DroneSnapshot> dronesCopy = new ArrayList<>(drones);
        /*
        Run the CollisionAvoider where two drones are considered too close if they are less than 10 meters apart
         */
        CollisionAvoider stopEveryoneAvoider = new StopEveryone(10);
        stopEveryoneAvoider.avoid(drones);

        /*
        Make sure nothing has changed.
         */
        assertTrue("The collision avoider didn't remove any drone", dronesCopy.equals(drones));
        assertTrue("Drone d1 has the same commands", d1Cmds.equals(drones.get(0).getCommands()));
        assertTrue("Drone d1 has the same commands", d2Cmds.equals(drones.get(1).getCommands()));
        assertTrue("Drone d1 has the same commands", d3Cmds.equals(drones.get(2).getCommands()));
    }
}
