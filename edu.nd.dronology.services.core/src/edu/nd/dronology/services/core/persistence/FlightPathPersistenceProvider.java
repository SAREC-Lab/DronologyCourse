package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IFlightPath;

/**
 * Provider implementation for {@link IFlightPath}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class FlightPathPersistenceProvider extends AbstractItemPersistenceProvider<IFlightPath> {

	public FlightPathPersistenceProvider() {
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

	public static FlightPathPersistenceProvider getInstance() {
		return new FlightPathPersistenceProvider();
	}

}
