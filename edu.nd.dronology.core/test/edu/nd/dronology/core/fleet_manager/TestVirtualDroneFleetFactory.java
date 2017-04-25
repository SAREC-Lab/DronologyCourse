package edu.nd.dronology.core.fleet_manager;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.VirtualDrone;
import edu.nd.dronology.core.drones_runtime.iDrone;
import edu.nd.dronology.core.gui_middleware.DroneStatus;
import edu.nd.dronology.core.home_bases.BaseManager;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.FlightZoneException;

public class TestVirtualDroneFleetFactory {

	VirtualDroneFleetFactory testInstance;

	@Before
	public void setUp() throws Exception {
		try {
			testInstance = new VirtualDroneFleetFactory();
		} catch (FlightZoneException e) {
			e.printStackTrace();
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithNull() {
		new VirtualDroneFleetFactory(1, null);

	}

	@Test
	public void testGetDrones() {
		List<ManagedDrone> drones = testInstance.getDrones();
		assertEquals(3, drones.size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetDronesModify() {
		List<ManagedDrone> drones = testInstance.getDrones();
		drones.add(new ManagedDrone(new VirtualDrone("XXX"), "XXX"));
	}


	
}
