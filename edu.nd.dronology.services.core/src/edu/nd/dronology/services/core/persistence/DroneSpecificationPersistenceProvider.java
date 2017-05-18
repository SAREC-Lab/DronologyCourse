package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IDroneSpecification;
import edu.nd.dronology.services.core.persistence.internal.DroneSpecificationtXStreamPersistor;


/**
 * Provider implementation for {@link IDroneSpecification}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class DroneSpecificationPersistenceProvider extends AbstractItemPersistenceProvider<IDroneSpecification> {

	public DroneSpecificationPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new DroneSpecificationtXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static DroneSpecificationPersistenceProvider getInstance() {
		return new DroneSpecificationPersistenceProvider();
	}

}
