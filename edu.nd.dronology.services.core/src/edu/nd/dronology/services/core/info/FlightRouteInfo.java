package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.core.util.LlaCoordinate;

public class FlightRouteInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7327376857430499641L;
	private List<MappingInfo> mappingInfos = new ArrayList<>();
	private String category;
	private List<LlaCoordinate> coordinates = new ArrayList<>();
	private double lenght = 0;
	private long dateCreated;
	private long dateModified;
	private double distance;

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

	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public List<LlaCoordinate> getCoordinates() {
		return coordinates;
	}

	public void addCoordinate(LlaCoordinate coordinate) {
		coordinates.add(coordinate);
	}

	public void rempoveCoordinate(LlaCoordinate coordinate) {
		coordinates.remove(coordinate);
	}

	public double getLenght() {
		return lenght;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public long getDateModified() {
		return dateModified;
	}

	public void setDistance(double distance) {
		this.distance = distance;

	}

	// public void updateDistance() {
	// //
	// }

}
