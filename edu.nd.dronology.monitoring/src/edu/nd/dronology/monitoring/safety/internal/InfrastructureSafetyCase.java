package edu.nd.dronology.monitoring.safety.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.monitoring.safety.IInfrastructureSafetyCase;
import edu.nd.dronology.monitoring.safety.ISACAssumption;

public class InfrastructureSafetyCase implements IInfrastructureSafetyCase {

	private String id;
	private List<ISACAssumption> assumptions;

	public InfrastructureSafetyCase() {
		assumptions = new ArrayList<>();
	}

	@Override
	public List<ISACAssumption> getAssumptions() {
		return Collections.unmodifiableList(assumptions);
	}

	public void addAssumption(Assumption ass) {
		assumptions.add(ass);

	}

	public List<ISACAssumption> getStaticEvaluateableAssumptions() {
		List<ISACAssumption> staticAssumptions = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isStatic()) {
				staticAssumptions.add(ass);
			}
		}
		return Collections.unmodifiableList(staticAssumptions);
	}

	public List<ISACAssumption> getMonitoralbeAssumptions() {
		List<ISACAssumption> monitorableAssumptions = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isMonitorable()) {
				monitorableAssumptions.add(ass);
			}
		}
		return Collections.unmodifiableList(monitorableAssumptions);
	}

	@Override
	public ISACAssumption getAssumption(String id) {
		for (ISACAssumption ass : assumptions) {
			if (ass.getId().equals(id)) {
				return ass;
			}
		}
		return null;
	}

}
