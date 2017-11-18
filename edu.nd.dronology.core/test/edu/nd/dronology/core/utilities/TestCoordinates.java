package edu.nd.dronology.core.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public class TestCoordinates {

	private LlaCoordinate c1;
	private LlaCoordinate c2;
	private LlaCoordinate c4;

	@Before
	public void setUp() throws Exception {
		c1 = new LlaCoordinate(1, 2, 3);
		c2 = new LlaCoordinate(2, 3, 4);
		c4 = new LlaCoordinate(2, 3, 4);
	}

	@Test
	public void testEquals() {
		assertNotEquals(c1, c2);
		assertEquals(c1, c1);

		assertEquals(c2, c4);
		// c4.setAltitude(55d);
		// assertNotEquals(c2, c4);
	}

	@Test
	public void testEqualsInHashCollection() {
		Set<LlaCoordinate> cord = new HashSet<>();
		cord.add(c1);
		cord.add(c2);
		cord.add(c4);

		assertEquals(2, cord.size());
		assertTrue(cord.remove(c4));
		assertEquals(1, cord.size());
		// c1.setAltitude(25);

		assertTrue(cord.remove(c1));
		assertEquals(0, cord.size());
	}

}
