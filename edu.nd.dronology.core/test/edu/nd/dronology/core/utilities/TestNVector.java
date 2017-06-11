package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.NVector;
import edu.nd.dronology.core.util.PVector;

public class TestNVector {

	private static final double FLOATING_POINT_ERROR = 5e-3; // 5 millimeters

	@Test
	public void testNVector() {
		NVector x = new NVector(3.0, 4.0, 0.0, 2.0);
		assertEquals(0.6, x.getX(), 0);
		assertEquals(0.8, x.getY(), 0);
		assertEquals(0.0, x.getZ(), 0);
		assertEquals(2.0, x.getAltitude(), 0);
	}

	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		NVector x = new NVector(3.0, 4.0, 0.0, 76.4);
		assertEquals("NVector(0.600000, 0.800000, 0.000000, altitude=76.400000)", x.toString());
	}

	/*
	 * In testToPVector(), testPvector1(), testPvector2(), ..., testPvector9()
	 * The local variables x, y, and z where calculated in python using a
	 * library called nvector. Here is the python3 code:

import nvector as nv wgs84 = nv.FrameE(name='WGS84')
pointA = wgs84.GeoPoint(latitude=40, longitude=-74, z=-10.0,degrees=True)
print(pointA.to_ecef_vector().pvector.ravel())
 
	 * Note: z = -1 * altitude
	 * To install nvector you can use pip:
	 * 
	 * pip3 install nvector
	 * 
	 */
	@Test
	public void testToPVector() {
		NVector n = new LlaCoordinate(0, 90, 0).toNVector();
		double x = 0, y = 6.37813700e+06, z = 0;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector2() {
		NVector n = new LlaCoordinate(37.819411, -122.478419, 0.0).toNVector();
		double x = -2708936.74654039, y = -4255715.42370547, z = 3889629.31691433;
		PVector p = n.toPVector();
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector3() {
		NVector n = new LlaCoordinate(90, 0, 0).toNVector();
		double x = 0, y = 0, z = 6.35675231e+06;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector4() {
		NVector n = new LlaCoordinate(-33.856909, 151.215171, 0).toNVector();
		double x = -4646956.98574331, y = 2553084.13930831, z = -3533277.16762843;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector5() {
		NVector n = new LlaCoordinate(75.389116, -96.925293, 0).toNVector();
		double x = -194604.77438434, y = -1602196.61867588, z = 6149864.48514232;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector6() {
		NVector n = new LlaCoordinate(0, 0, 0).toNVector();
		double x = 6378137.0, y = 0.0, z = 0.0;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector7() {
		NVector n = new LlaCoordinate(-90.0, -90.0, 0.0).toNVector();
		double x = 0.0, y = 0.0, z = -6.35675231e+06;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector8() {
		NVector n = new LlaCoordinate(0.0, 180.0, 0.0).toNVector();
		double x = -6.37813700e+06, y = 0.0, z = 0.0;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToPVector9() {
		NVector n = new LlaCoordinate(40.690577, -74.045691, 10.0).toNVector();
		double x = 1331218.53835838, y = -4656522.4580859, z = 4136435.86455259;
		PVector p = n.toPVector();
		assertEquals(x, p.getX(), FLOATING_POINT_ERROR);
		assertEquals(y, p.getY(), FLOATING_POINT_ERROR);
		assertEquals(z, p.getZ(), FLOATING_POINT_ERROR);
	}
}
