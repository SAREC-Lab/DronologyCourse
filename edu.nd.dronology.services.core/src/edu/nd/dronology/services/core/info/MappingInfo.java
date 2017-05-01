package edu.nd.dronology.services.core.info;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MappingInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1025240228593672234L;

	String artifactid;
	Set<String> mappedElements;
	
	public MappingInfo(String artifactid, Set<String> mappedElements){
		this.artifactid = artifactid;
		this.mappedElements = new HashSet<>(mappedElements);
	}

	public Collection<String> getMappedElementId() {
		return Collections.unmodifiableCollection(mappedElements);
	}

}
