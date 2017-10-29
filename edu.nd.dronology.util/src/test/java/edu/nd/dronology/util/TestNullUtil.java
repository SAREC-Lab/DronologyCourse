package edu.nd.dronology.util;

import org.junit.Test;
import edu.nd.dronology.util.NullUtil;

public class TestNullUtil {
	@Test(expected = IllegalArgumentException.class)
	public void testCheckNull() {
		Object arg = null;
		NullUtil.checkNull(arg);
	}
	
	@Test
	public void testCheckNull2() {
		NullUtil.checkNull("test");
	}
}
