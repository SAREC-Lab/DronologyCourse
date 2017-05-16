package edu.nd.dronology.services.core.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.core.util.Coordinates;

public class SimulatorScenarioInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7327376857430499641L;

	private String category = "Default";
	
	


	public SimulatorScenarioInfo(String name, String id) {
		super(name, id);
	}
	
	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}

	

}
