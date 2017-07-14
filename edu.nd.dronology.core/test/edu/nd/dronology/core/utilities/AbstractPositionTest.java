package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.nd.dronology.core.util.AbstractPosition;
import edu.nd.dronology.core.util.LlaCoordinate;

public class AbstractPositionTest {

	@Test
	public void testDistance() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9);
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9);
		assertEquals(91.44, a.distance(b), 0.25);
	}

	@Test
	public void testTravelDistance() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9);
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9);
		assertEquals(91.44, a.travelDistance(b), 0.25);
	}
	
	@Test
	public void testDistance2() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9);
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9).toNVector();
		assertEquals(91.44, a.distance(b), 0.25);
	}

	@Test
	public void testTravelDistance2() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9);
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9).toNVector();
		assertEquals(91.44, a.travelDistance(b), 0.25);
	}

	@Test
	public void testDistance3() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9).toPVector();
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9).toPVector();
		assertEquals(91.44, a.distance(b), 0.25);
	}

	@Test
	public void testTravelDistance3() {
		AbstractPosition a = new LlaCoordinate(41.697983, -86.234213, 261.9).toNVector();
		AbstractPosition b = new LlaCoordinate(41.698808, -86.234222, 261.9);
		assertEquals(91.44, a.travelDistance(b), 0.25);
	}
}
