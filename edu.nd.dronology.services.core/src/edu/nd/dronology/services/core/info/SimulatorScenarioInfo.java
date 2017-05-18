package edu.nd.dronology.services.core.info;

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
