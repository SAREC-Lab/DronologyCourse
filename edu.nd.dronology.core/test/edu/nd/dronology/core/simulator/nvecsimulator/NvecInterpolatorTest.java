package edu.nd.dronology.core.simulator.nvecsimulator;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.coordinate.NVector;

public class NvecInterpolatorTest {

	@Test
	public void testMove() {
		NVector current = new LlaCoordinate(31.231, -121.342, 44.21).toNVector();
		NVector target = new LlaCoordinate(31.22222, -122.3333, 44.21).toNVector();
		double distanceToTravel = 25.25; // meters
		double tolerance = NvecInterpolator.TOLERANCE;
		NVector nextPosition = NvecInterpolator.move(current, target, distanceToTravel);

		assertEquals(distanceToTravel, current.distance(nextPosition), tolerance);
		assertEquals(44.21, nextPosition.getAltitude(), tolerance);
	}

	@Test
	public void testMove2() {
		/*
		 * What should the behavior be when traveling to the opposite side of
		 * the Earth? Perhaps the code should throw an exception.
		 */
		NVector current = new NVector(1.0, 0, 0, 0);
		NVector target = new NVector(-1.0, 0, 0, 0);
		double distanceToTravel = 100.0; // meters
		NVector nextPosition = NvecInterpolator.move(current, target, distanceToTravel);
		assertEquals(Double.NaN, nextPosition.getX(), 0.0);
		assertEquals(Double.NaN, nextPosition.getY(), 0.0);
		assertEquals(Double.NaN, nextPosition.getZ(), 0.0);
	}

	@Test
	public void testMove3() {
		/*
		 * Go from one end zone at Notre Dame Stadium to the other. (a distance
		 * of about 91.44 meters or 100 yards)
		 */
		NVector current = new LlaCoordinate(41.697982, -86.234207, 225.95).toNVector();
		NVector target = new LlaCoordinate(41.698810, -86.234221, 225.95).toNVector();
		// make the distance more than 91.44 meters
		double distanceToTravel = 100.0; // meters
		double tolerance = NvecInterpolator.TOLERANCE;
		NVector nextPosition = NvecInterpolator.move(current, target, distanceToTravel);

		// when going to a target position that is less than distance to travel
		// meters away, snap to the target position
		assertEquals(target, nextPosition);
		// made epsilon 1 meter because I got the coordinates of each end zone
		// by right clicking on google maps.
		assertEquals(91.44, current.distance(nextPosition), 1.0);
		assertEquals(225.95, nextPosition.getAltitude(), tolerance);
	}
	
	@Test
	public void testMove4() {
//		55660.181728949785
		NVector current = new LlaCoordinate(0, 0, 50).toNVector();
		NVector target = new LlaCoordinate(0, 1, 50).toNVector();
		for (int i = 0; i < 55660; ++i) {
			current = NvecInterpolator.move(current, target, 1.0);
		}
		assertEquals(0.18, current.distance(new LlaCoordinate(0, 0.5, 50).toNVector()), 0.01);
	}

//	@Test
//	public void testMove4() {
//		NVector current = new NVector(1, 0, 0, 0);
////		NVector target = new LlaCoordinate(41.698810, -86.234221, 225.95).toNVector();
////		NVector target = new LlaCoordinate(0, -90, 0.0).toNVector();
//		NVector target = new NVector(0, 1, 0, 0);
//		System.out.println("distance from 0,0 to 0, 90 is " + NVector.travelDistance(current, target) );
//		double distanceToTravel = 1609000; // meters
//		double tolerance = NvecInterpolator.TOLERANCE;
//		NVector nextPosition = NvecInterpolator.move(current, target, distanceToTravel);
//		
//		System.out.println(current.distance(nextPosition) - distanceToTravel);
//		double travelDist = NVector.travelDistance(current, nextPosition);
//		System.out.println("distance from current to next is " +  travelDist);
//		System.out.println("laser dist is different by " + (travelDist - distanceToTravel));
//		assertEquals(distanceToTravel, current.distance(nextPosition), 1.0);
//		assertEquals(0.0, nextPosition.getAltitude(), tolerance);
//	}
}
