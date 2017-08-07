package edu.nd.dronology.monitoring.persistor;

import edu.nd.dronology.monitoring.safety.internal.InfrastructureSafetyCase;
import edu.nd.dronology.services.core.persistence.AbstractItemPersistenceProvider;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public class SACPersistenceProvider extends AbstractItemPersistenceProvider<InfrastructureSafetyCase> {

	public SACPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new ISacXstreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static SACPersistenceProvider getInstance() {
		return new SACPersistenceProvider();
	}

}
