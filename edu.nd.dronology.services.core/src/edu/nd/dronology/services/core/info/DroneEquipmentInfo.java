package edu.nd.dronology.services.core.info;

public class DroneEquipmentInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2844123024068335148L;
	private String type = "Default";

	public DroneEquipmentInfo(String name, String id) {
		super(name, id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;

	}

}
