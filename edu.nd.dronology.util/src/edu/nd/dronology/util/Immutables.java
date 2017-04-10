package edu.nd.dronology.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Immutables {
	/**
	 * Will eliminate duplicates.
	 * 
	 * @param coll
	 *          The collection to copy.
	 * @return An unmodifiable (as in {@link Collections#unmodifiableSet(Set)}) copy of the given set.
	 */
	
	public static <ELEMENT_TYPE> Set<ELEMENT_TYPE> hashSetCopy( final Collection<ELEMENT_TYPE> coll) {
		NullUtil.checkNull(coll);
		// Make a copy AND an unmodifiable view.
		return Collections.unmodifiableSet(new HashSet<>(coll));
	}

	/**
	 * May contain duplicates.
	 * 
	 * @param coll
	 *          The collection to copy.
	 * @return An unmodifiable (as in {@link Collections#unmodifiableList(java.util.List)}) copy of the given collection.
	 */
	
	public static <ELEMENT_TYPE> List<ELEMENT_TYPE> linkedListCopy( final Collection<ELEMENT_TYPE> coll) {
		NullUtil.checkNull(coll);
		// Make a copy AND an unmodifiable view.
		return Collections.unmodifiableList(new LinkedList<>(coll));
	}

	/**
	 * @param map
	 *          The map to copy.
	 * @return An unmodifiable (as in {@link Collections#unmodifiableMap(Map)}) copy of the given map.
	 */
	
	public static <KEY_TYPE, VALUE_TYPE> Map<KEY_TYPE, VALUE_TYPE> hashMapCopy(
			 final Map<KEY_TYPE, VALUE_TYPE> map) {
		NullUtil.checkNull(map);
		// Make a copy AND an unmodifiable view.
		return Collections.unmodifiableMap(new HashMap<>(map));
	}
}
