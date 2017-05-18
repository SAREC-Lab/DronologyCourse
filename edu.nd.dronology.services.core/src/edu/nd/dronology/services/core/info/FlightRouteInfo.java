package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.core.util.Coordinate;

public class FlightRouteInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7327376857430499641L;
	private List<MappingInfo> mappingInfos = new ArrayList<>();
	private String category;
	private List<Coordinate> coordinates = new ArrayList<>();

	public FlightRouteInfo(String name, String id) {
		super(name, id);
	}

	public Collection<MappingInfo> getArtifactMappings() {
		return Collections.unmodifiableCollection(mappingInfos);
	}

	public void addMappingInfo(MappingInfo mpInfo) {
		mappingInfos.add(mpInfo);

	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;

	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void addCoordinate(Coordinate coordinate) {
		coordinates.add(coordinate);
	}
	
	public void rempoveCoordinate(Coordinate coordinate) {
		coordinates.remove(coordinate);
	}



}
