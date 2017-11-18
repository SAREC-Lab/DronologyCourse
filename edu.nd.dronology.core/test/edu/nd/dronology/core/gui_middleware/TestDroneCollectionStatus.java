package edu.nd.dronology.core.gui_middleware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.nd.dronology.core.flight.internal.SoloDirector;
import edu.nd.dronology.core.vehicle.proxy.UAVProxyManager;

@RunWith(Parameterized.class)
public class TestDroneCollectionStatus {

	

	    @Parameterized.Parameters
	    public static List<Object[]> data() {
	        return Arrays.asList(new Object[10][0]);
	    }
	
	
	
	
	
	SoloDirector testInstance;

	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void testgetInstanceNonThreaded() {
		
		instance1 = UAVProxyManager.getInstance();
		instance2 = UAVProxyManager.getInstance();
		
		assertNotNull(instance1);
		assertNotNull(instance2);
		assertEquals(instance1, instance2);
	}
	

	@Test
	public void testgetInstanceThreaded() {

		final CyclicBarrier gate = new CyclicBarrier(3);

		Thread t1 = new Thread() {

			@Override
			public void run() {
				try {
					gate.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
				instance1 = UAVProxyManager.getInstance();
			}
		};
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					gate.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
				instance2 = UAVProxyManager.getInstance();
			}
		};

		t1.start();
		t2.start();
		
		try {
			gate.await();
			t1.join();
			t2.join();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		assertNotNull(instance1);
		assertNotNull(instance2);
		assertEquals(instance1, instance2);

	}

	private volatile UAVProxyManager instance1;
	private volatile UAVProxyManager instance2;

}
