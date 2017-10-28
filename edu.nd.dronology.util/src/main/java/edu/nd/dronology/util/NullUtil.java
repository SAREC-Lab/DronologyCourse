
package edu.nd.dronology.util;


import java.util.Collection;
/**
 * 
 * @author Michael Vierhauser
 *
 */
public class NullUtil {
	public static final String NULL = "null";

	public static void checkNull(Object arg) {
		if (arg == null) {
			throw new IllegalArgumentException("Input parameter can not be null");
		}
	}

	public static void checkNull(Object... obj) {
		checkArrayNull(obj);
		for (Object o : obj) {
			checkNull(o);
		}
	}

	public static void checkArrayNull(Object arg) {
		if (arg == null) {
			throw new IllegalArgumentException("Input parameter can not be null");
		}
	}
	
	/**
	 * @param collection
	 * @throws IllegalArgumentException when collection is null or if the collection contains null
	 */
	public static <TYPE> void checkNullElem(final Collection<TYPE> collection	) {
		NullUtil.checkNull(collection);
		// If contains null, behave like checkNull.
		if( collection.contains(null) ) {
			NullUtil.checkNull((Object)null);
		}
	}

}
