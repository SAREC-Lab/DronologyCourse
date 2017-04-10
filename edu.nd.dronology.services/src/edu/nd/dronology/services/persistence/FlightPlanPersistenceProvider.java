package edu.nd.dronology.services.persistence;

import edu.nd.dronology.services.core.persistence.apache.AbstractItemPersistenceProvider;
import edu.nd.dronology.services.instances.flightpath.IFlightPath;

/**
 * Provider implementation for {@link IFlightPath}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class FlightPlanPersistenceProvider extends AbstractItemPersistenceProvider<IFlightPath> {

	public FlightPlanPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new FlightPlanXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static FlightPlanPersistenceProvider getInstance() {
		return new FlightPlanPersistenceProvider();
	}

}
