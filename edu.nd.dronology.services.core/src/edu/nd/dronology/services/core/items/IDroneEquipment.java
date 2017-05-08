package edu.nd.dronology.services.core.items;

public interface IDroneEquipment extends IPersistableItem{






	String getDescription();

	void setType(String type);
	
	void setDescription(String description);
	
	String getType();

}
