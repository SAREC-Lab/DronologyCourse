package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IDroneEquipment;
import edu.nd.dronology.services.core.persistence.internal.DroneEquipmentXStreamPersistor;


/**
 * Provider implementation for {@link IFlightRoute}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class DroneEquipmentPersistenceProvider extends AbstractItemPersistenceProvider<IDroneEquipment> {

	public DroneEquipmentPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new DroneEquipmentXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static DroneEquipmentPersistenceProvider getInstance() {
		return new DroneEquipmentPersistenceProvider();
	}

}
