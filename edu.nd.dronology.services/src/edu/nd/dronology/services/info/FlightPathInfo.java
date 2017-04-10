package edu.nd.dronology.services.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.services.core.info.RemoteInfoObject;

public class FlightPathInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7327376857430499641L;
	private List<MappingInfo> mappingInfos = new ArrayList<>();

	public FlightPathInfo(String name, String id) {
		super(name, id);
	}

	public Collection<MappingInfo> getArtifactMappings() {
		return Collections.unmodifiableCollection(mappingInfos);
	}

	public void addMappingInfo(MappingInfo mpInfo) {
		mappingInfos.add(mpInfo);

	}

}
