package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.PVector;

public class TestPVector {

	@Test
	public void test() {
		// this measures the distance across the football field at Notre Dame Stadium
		PVector a = new LlaCoordinate(41.697983, -86.234213, 225.95).toPVector();
		PVector b = new LlaCoordinate(41.697987, -86.233629, 225.95).toPVector();
		System.out.println(dist(a,b)); // should be about 45 - 50 meters
		fail("Not yet implemented");
	}
	
	private static double dist(PVector a, PVector b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		double dz = a.getZ() - b.getZ();
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

}
