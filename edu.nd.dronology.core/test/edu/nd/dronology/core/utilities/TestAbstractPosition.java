package edu.nd.dronology.core.utilities;

import static org.junit.Assert.*;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import edu.nd.dronology.core.util.AbstractPosition;
import edu.nd.dronology.core.util.LlaCoordinate;

public class TestAbstractPosition {
	
	private static final double EPSILON = 0.000001;
	
	@Test
	public void testRotMat() {
		LlaCoordinate t = new LlaCoordinate(0, 0, 0);
		RealMatrix r = t.toRotMatrix();
		RealMatrix e = new Array2DRowRealMatrix(new double[][] {{0,0,1.}, {0,1.,0}, {-1., 0, 0}});
		assertEquals(e, r);
		
	}
	
	@Test
	public void testRotMat2() {
		RealMatrix e = new Array2DRowRealMatrix(new double[][]{{-0.17364818,  0, 0.98480775}, {0., 1., 0.}, {-0.98480775, 0., -0.17364818}});
		LlaCoordinate t = new LlaCoordinate(10, 0, 0);
		RealMatrix r = t.toRotMatrix();
//		printMatrix(e);
//		System.out.println();
//		System.out.println("actual");
//		printMatrix(r);
		checkMatrix(e, r);

	}
	
	public static void printMatrix(RealMatrix e) {
		for (int i = 0; i < 3; ++i) {
			StringBuffer b = new StringBuffer();
			for (int k = 0; k < 3; ++k) {
				b.append("" + e.getEntry(i, k) + "   ");
			}
			System.out.println(b.toString());
		}
	}
	
	private void checkMatrix(RealMatrix expected, RealMatrix actual) {
		RealMatrix e = expected;
		RealMatrix r = actual;
		for (int i = 0; i < 3; ++i)
			checkColumn(e.getColumn(i), r.getColumn(i));
	}
	
	private void checkColumn(double[] a, double[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; ++i) {
			assertEquals(a[i], b[i], EPSILON);
		}
	}

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
