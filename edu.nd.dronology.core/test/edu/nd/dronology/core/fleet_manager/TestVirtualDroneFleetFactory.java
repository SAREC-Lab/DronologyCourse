package edu.nd.dronology.core.fleet_manager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.fleet.VirtualDroneFleetFactory;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

public class TestVirtualDroneFleetFactory {

	VirtualDroneFleetFactory testInstance;

	@Before
	public void setUp() throws Exception {

		testInstance = VirtualDroneFleetFactory.getInstance();

	}

	@Test
	public void testInitializeDrone() {
		// List<ManagedDrone> drones = testInstance.getDrones();
		// assertEquals(0, drones.size());
		try {
			ManagedDrone d = testInstance.initializeDrone("1", "abc", 12, 12, 12);
		} catch (DroneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO.. add test

	}

//	@Test(expected = UnsupportedOperationException.class)
//	public void testGetDronesModify() {
//		List<ManagedDrone> drones = testInstance.getDrones();
//		 drones.add(new ManagedDrone(new VirtualDrone("XXX")));
//	}

}
