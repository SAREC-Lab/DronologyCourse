package edu.nd.dronology.monitoring.safety;

import java.util.List;

public interface ISafetyCase {

	List<ISACAssumption> getAssumptions();

	ISACAssumption getAssumption(String id);

}
