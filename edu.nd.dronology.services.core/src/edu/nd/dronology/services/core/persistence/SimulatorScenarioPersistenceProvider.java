package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.persistence.internal.SimulatorScenarioXStreamPersistor;

/**
 * Provider implementation for {@link ISimulatorScenario}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class SimulatorScenarioPersistenceProvider extends AbstractItemPersistenceProvider<ISimulatorScenario> {

	public SimulatorScenarioPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new SimulatorScenarioXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static SimulatorScenarioPersistenceProvider getInstance() {
		return new SimulatorScenarioPersistenceProvider();
	}

}
