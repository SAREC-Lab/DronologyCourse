package edu.nd.dronology.monitoring.safety.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.monitoring.safety.IExternalSafetyCase;
import edu.nd.dronology.monitoring.safety.ISACAssumption;
import edu.nd.dronology.monitoring.validation.EvalFunction;

public class UAVSaeftyCase implements IExternalSafetyCase {

	private String uavid;
	private final String id;
	private List<Assumption> assumptions;

	public UAVSaeftyCase(String id, String uavid) {
		this.uavid = uavid;
		this.id = id;
		assumptions = new ArrayList<>();
	}

	public void setUAVId(String uavid) {
		this.uavid = uavid;

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

	@Override
	public List<ISACAssumption> getAssumptions() {
		return Collections.unmodifiableList(assumptions);
	}

	@Override
	public void addAssumption(Assumption ass) {
		assumptions.add(ass);

	}

	@Override
	public List<ISACAssumption> getStaticEvaluateableAssumptions() {
		List<ISACAssumption> staticAssumptions = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isStatic()) {
				staticAssumptions.add(ass);
			}
		}
		return Collections.unmodifiableList(staticAssumptions);
	}

	@Override
	public List<ISACAssumption> getPluggableAssumptions() {
		List<ISACAssumption> pluggableAssumptins = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isPlugable()) {
				pluggableAssumptins.add(ass);
			}
		}
		return Collections.unmodifiableList(pluggableAssumptins);
	}

	@Override
	public String getUAVId() {
		return uavid;
	}

	@Override
	public List<String> getStaticParameters() {
		List<String> parameters = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isStatic()) {
				EvalFunction func = ass.getFunction();
				for (String param : func.getParameters()) {
					if (param.startsWith(ISACAssumption.ESAC_PREFIX)) {
						parameters.add(param);
					}
				}
			}
		}
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public List<String> getMonitorableParameters() {
		List<String> parameters = new ArrayList<>();
		for (ISACAssumption ass : assumptions) {
			if (ass.isMonitorable()) {
				EvalFunction func = ass.getFunction();
				for (String param : func.getParameters()) {
					if (param.startsWith(ISACAssumption.ESAC_PREFIX)) {
						parameters.add(param);
					}
				}
			}
		}
		return Collections.unmodifiableList(parameters);

	}

}
