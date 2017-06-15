package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.PVector;

public class TestPVector {

	/*
	 * How off should toLla be? How about 5mm? To get that we need to convert
	 * 5mm to meters: 0.005. Now we need to find how many degrees around the
	 * earth = 0.005 Lets assume a worse case scenario, we are measuring at the
	 * equator in the east/west direction. In this case we should use the
	 * semi-major axis: 6378137.0 meters for our radius
	 * 
	 * radians * radius = sector_length
	 * sector_length = 0.005
	 * radius = 6378137.0
	 * so radians = sector_length / radius
	 * 
	 * 
	 * 0.005 / 6378137.0 = radians = 7.8392797e-10
	 * 
	 * now we need to know how many degrees that is:
	 * Math.toDegrees(7.8392797e-10) = 4.491576412325821E-8 so our epsilon for
	 * testing latitude and longitude should be 4.491576412325821E-8
	 * 
	 */
	private static final double EPSILON = 4.491576412325821E-8;

	@Test
	public void test() {
		// this measures the distance across the football field at Notre Dame
		// Stadium
		PVector a = new LlaCoordinate(41.697983, -86.234213, 225.95).toPVector();
		PVector b = new LlaCoordinate(41.697987, -86.233629, 225.95).toPVector();
//		System.out.println(dist(a, b)); // should be about 45 - 50 meters
//		System.out.println(a.toLlaCoordinate().getAltitude());
//		fail("Not yet implemented");
	}

	private static double dist(PVector a, PVector b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		double dz = a.getZ() - b.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public void testToLla() {
		double lat = 0.0;
		double lon = 0.0;
		double alt = 0.0;
		PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
		LlaCoordinate lla = p.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), EPSILON);
		assertEquals(lon, lla.getLongitude(), EPSILON);
		assertEquals(alt, lla.getAltitude(), EPSILON);
	}
	
	@Test
	public void testToLla0() {
	  double lat = 0.0;
	  double lon = 0.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla1() {
	  double lat = 39.0;
	  double lon = 18.0;
	  double alt = 20.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla2() {
	  double lat = 0.0;
	  double lon = 180.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla3() {
	  double lat = 90.0;
	  double lon = 0.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla4() {
	  double lat = 90.0;
	  double lon = 180.0;
	  double alt = 10.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla5() {
	  double lat = 90.0;
	  double lon = -179.9999999;
	  double alt = 10.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla6() {
	  double lat = -90.0;
	  double lon = 0.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla7() {
	  double lat = -90.0;
	  double lon = -179.9999999;
	  double alt = 10.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla8() {
	  double lat = 45.0;
	  double lon = 45.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla9() {
	  double lat = 45.0;
	  double lon = -45.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla10() {
	  double lat = -45.0;
	  double lon = -45.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla11() {
	  double lat = -45.0;
	  double lon = 45.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla12() {
	  double lat = 45.0;
	  double lon = 135.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla13() {
	  double lat = -45.0;
	  double lon = 135.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla14() {
	  double lat = -45.0;
	  double lon = -135.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla15() {
	  double lat = 45.0;
	  double lon = -135.0;
	  double alt = 0.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla16() {
	  double lat = 37.63054;
	  double lon = -122.468357;
	  double alt = 75.4;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla17() {
	  double lat = -33.856562;
	  double lon = 151.215061;
	  double alt = 1.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla18() {
	  double lat = 35.798201;
	  double lon = -117.635996;
	  double alt = 663.31;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla19() {
	  double lat = 36.246102;
	  double lon = -116.818402;
	  double alt = -75.03;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}

	@Test
	public void testToLla20() {
	  double lat = 36.191038;
	  double lon = -122.021897;
	  double alt = -1298.88;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}
	
	@Test
	public void testToLla21() {
	  double lat = -9.0;
	  double lon = -179.9999999;
	  double alt = 10.0;
	  PVector p = new LlaCoordinate(lat, lon, alt).toPVector();
	  LlaCoordinate lla = p.toLlaCoordinate();
	  assertEquals(lat, lla.getLatitude(), EPSILON);
	  assertEquals(lon, lla.getLongitude(), EPSILON);
	  assertEquals(alt, lla.getAltitude(), EPSILON);
	}
}
