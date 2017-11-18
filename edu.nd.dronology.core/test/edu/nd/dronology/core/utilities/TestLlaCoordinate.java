package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.coordinate.NVector;

public class TestLlaCoordinate {

	@Test
	public void testHashCode() {
		HashSet<LlaCoordinate> set = new HashSet<>();
		LlaCoordinate x = new LlaCoordinate(24.214, -89.42, 34.0123);
		LlaCoordinate x2 = new LlaCoordinate(24.214, -89.42, 34.0123);
		LlaCoordinate notX = new LlaCoordinate(0.0, 0.0, 0.0);
		assertTrue(x.hashCode() == x2.hashCode());
		set.add(x);
		set.add(notX);
		assertEquals(2, set.size());
		assertFalse(set.add(x2));
		assertEquals(2, set.size());
	}

	@Test
	public void testEqualsObject() {
		LlaCoordinate x = new LlaCoordinate(24.214, -89.42, 34.0123);
		assertTrue(x.equals(x));
		
		LlaCoordinate x2 = new LlaCoordinate(24.214, -89.42, 34.0123);
		assertTrue(x.equals(x2));
		
		LlaCoordinate notX = new LlaCoordinate(0.0, 0.0, 0.0);
		assertFalse(x.equals(notX));
		
		assertFalse(x.equals(null));
		assertFalse(x.equals("some random string"));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testLatitudeTooLarge() {
		new LlaCoordinate(90.000001, 0.0, 0.0);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testLatitudeTooSmall() {
		new LlaCoordinate(-90.000001, 0.0, 0.0);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testLongitudeTooLarge() {
		new LlaCoordinate(0.0, 180.000001, 0.0);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testLongitudeTooSmall() {
		new LlaCoordinate(0.0, -180.0, 0.0);
	}
	
	@Test
	public void testGetters() {
		double lat = 34.4;
		double lon = -121.43221;
		double alt = 102.1;
		LlaCoordinate x = new LlaCoordinate(lat, lon, alt);
		assertEquals(lat, x.getLatitude(), 0.0);
		assertEquals(lon, x.getLongitude(), 0.0);
		assertEquals(alt, x.getAltitude(), 0.0);
	}
	
	@Test
	public void testToString() {
		LlaCoordinate x = new LlaCoordinate(37.818877, -122.478344, 76.2);
		assertEquals("LlaCoordinate(37.818877, -122.478344, 76.200000)", x.toString());
	}
	
	@Test
	public void testToNVector() {
		NVector n = new NVector(1.0, 0.0, 0, 10.0);
		LlaCoordinate x = new LlaCoordinate(0, 0, 10.0);
		assertEquals(n, x.toNVector());
	}
	
	@Test
	public void testToNVector2() {
		NVector n = new NVector(0.0, 1.0, 0.0, -230.3);
		LlaCoordinate x = new LlaCoordinate(0, 90, -230.3);
		assertEquals(0, n.distance(x.toNVector()), 1e-9);
	}

}
