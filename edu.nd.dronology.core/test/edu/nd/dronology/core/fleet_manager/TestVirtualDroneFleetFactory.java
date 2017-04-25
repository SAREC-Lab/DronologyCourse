package edu.nd.dronology.core.fleet_manager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.VirtualDrone;

public class TestVirtualDroneFleetFactory {

	VirtualDroneFleetFactory testInstance;

	@Before
	public void setUp() throws Exception {
<<<<<<< HEAD
		try {
			testInstance = new VirtualDroneFleetFactory();
		} catch (FlightZoneException e) {
			e.printStackTrace();
		}
=======
		testInstance = new VirtualDroneFleetFactory();
>>>>>>> branch 'master' of https://github.com/SAREC-Lab/Dronology.git

	}


	@Test
	public void testInitializeDrone() {
		List<ManagedDrone> drones = testInstance.getDrones();
		assertEquals(0, drones.size());
		ManagedDrone d = testInstance.initializeDrone("1", "abc", 12, 12, 12);
		//TODO.. add test
		
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetDronesModify() {
		List<ManagedDrone> drones = testInstance.getDrones();
		drones.add(new ManagedDrone(new VirtualDrone("XXX"), "XXX"));
	}


	
}
