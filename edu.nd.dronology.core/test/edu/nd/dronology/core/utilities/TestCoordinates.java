package edu.nd.dronology.core.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.nd.dronology.core.util.Coordinate;

public class TestCoordinates {
	
	
	private Coordinate c1;
	private Coordinate c2;
	private Coordinate c4;



	@Before
	public void setUp() throws Exception {
		c1 = new Coordinate(1, 2, 3);
		c2	= new Coordinate(2, 3, 4);
		c4 = new Coordinate(2, 3, 4);
	}

	
	@Test
	public void testEquals() {
		assertNotEquals(c1, c2);
		assertEquals(c1, c1);
		
		
		assertEquals(c2, c4);
		c4.setAltitude(55);
		assertNotEquals(c2, c4);
	}
	
	
	@Test
	public void testEqualsInHashCollection() {
		Set<Coordinate> cord = new HashSet<>();
		cord.add(c1);
		cord.add(c2);
		cord.add(c4);
		
		assertEquals(2, cord.size());
		assertTrue(cord.remove(c4));
		assertEquals(1, cord.size());
		c1.setAltitude(25);
		
		assertTrue(cord.remove(c1));
		assertEquals(0, cord.size());
	}

}
