package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.coordinate.NVector;
import edu.nd.dronology.core.coordinate.PVector;

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
		NVector n = new LlaCoordinate(37.638680, -122.420983, 16.57).toNVector();
		NVector n1 = new LlaCoordinate(37.638680, -122.420983, 16.57).toNVector();
		NVector n2 = new LlaCoordinate(37.719840, -122.496163, 12.81).toNVector();
		HashSet<NVector> set = new HashSet<NVector>();
		set.add(n);
		set.add(n1);
		assertEquals(1, set.size());
		set.add(n2);
		assertEquals(2, set.size());
	}

	@Test
	public void testEquals() {
		NVector n = new LlaCoordinate(37.638680, -122.420983, 16.57).toNVector();
		NVector n1 = new LlaCoordinate(37.638680, -122.420983, 16.57).toNVector();
		NVector n2 = new LlaCoordinate(37.719840, -122.496163, 12.81).toNVector();
		assertEquals(n, n1);
		assertNotEquals(n1, n2);
	}

	@Test
	public void testToString() {
		NVector x = new NVector(3.0, 4.0, 0.0, 76.4);
		assertEquals("NVector(0.600000, 0.800000, 0.000000, altitude=76.400000)", x.toString());
	}

	@Test
	public void testDistance() {
		/*
		 * Went to google maps and found a football field. Football fields are
		 * usually 100 yards from end zone to end zone. I right clicked on each
		 * end zone and found the latitude and longitude. I then found the
		 * elevation of the field. I got the values for n1 and n2 from Levi
		 * Stadium (home field for the 49ers).
		 */
		NVector n1 = new LlaCoordinate(37.402719, -121.969790, 4.70).toNVector();
		NVector n2 = new LlaCoordinate(37.403442, -121.970283, 4.70).toNVector();
		/*
		 * Of course right clicking on google maps is not very precise so I set
		 * epsilon to half a foot in meters.
		 * 
		 * 0.1524 meters = 0.5 feet
		 * 
		 * The distance function works in meters
		 */
		assertEquals(91.44, n1.distance(n2), 0.1524);
	}
	
	@Test
	public void testDistance2() {
		/*
		 * Suppose position B is 20 meters above position A. Then the distance
		 * from A to B should be 20 meters
		 */
		NVector a = new LlaCoordinate(37.698276, -123.004222, 81.2).toNVector();
		NVector b = new LlaCoordinate(37.698276, -123.004222, 101.2).toNVector();
		
		assertEquals(20.0, a.distance(b), 0.00001);
	}

	@Test
	public void testGetters() {
		NVector n = new NVector(1, 0, 0, 2);
		assertEquals(1.0, n.getX(), 0.0);
		assertEquals(2.0, n.getAltitude(), 0.0);

		NVector n1 = new NVector(0, 1, 0, 0);
		assertEquals(1.0, n1.getY(), 0.0);

		NVector n2 = new NVector(0, 0, 1, 0);
		assertEquals(1.0, n2.getZ(), 0.0);

	}

	/*
	 * In testToPVector(), testPvector1(), testPvector2(), ..., testPvector9()
	 * The local variables x, y, and z where calculated in python using a
	 * library called nvector. Here is the python3 code:
	 * 
	 * import nvector as nv wgs84 = nv.FrameE(name='WGS84') pointA =
	 * wgs84.GeoPoint(latitude=40, longitude=-74, z=-10.0,degrees=True)
	 * print(pointA.to_ecef_vector().pvector.ravel())
	 * 
	 * Note: z = -1 * altitude To install nvector you can use pip:
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

	@Test
	public void testToLla() {
		double lat = 40.690577;
		double lon = -74.045691;
		double alt = 10.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla2() {
		double lat = 90.0;
		double lon = 0.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla3() {
		double lat = -90.0;
		double lon = 0.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla4() {
		double lat = 0.0;
		double lon = 180.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla5() {
		double lat = 37.738863;
		double lon = -122.454088;
		double alt = 82.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla6() {
		double lat = -37.738863;
		double lon = -122.454088;
		double alt = 82.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla7() {
		double lat = 0.0;
		double lon = 0.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla9() {
		double lat = 45.0;
		double lon = 45.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla10() {
		double lat = 45.0;
		double lon = 45.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla11() {
		double lat = -45.0;
		double lon = 45.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla12() {
		double lat = -45.0;
		double lon = -45.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla13() {
		double lat = 45.0;
		double lon = -45.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla14() {
		double lat = 45.0;
		double lon = 135.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla15() {
		double lat = -45.0;
		double lon = 135.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla16() {
		double lat = -45.0;
		double lon = -135.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla17() {
		double lat = 45.0;
		double lon = -135.0;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}

	@Test
	public void testToLla18() {
		double lat = 0.0;
		double lon = -179.999999999999;
		double alt = 0.0;
		NVector n = new LlaCoordinate(lat, lon, alt).toNVector();
		LlaCoordinate lla = n.toLlaCoordinate();
		assertEquals(lat, lla.getLatitude(), FLOATING_POINT_ERROR);
		assertEquals(lon, lla.getLongitude(), FLOATING_POINT_ERROR);
		assertEquals(alt, lla.getAltitude(), FLOATING_POINT_ERROR);
	}
}
